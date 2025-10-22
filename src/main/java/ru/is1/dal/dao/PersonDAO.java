package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.config.ws.DbEvent;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Person;

import java.util.*;

@ApplicationScoped
@MonitorPerformance
public class PersonDAO extends AbstractDAO<Person> {

    public PersonDAO() {
        super(Person.class);
    }

//    @Override
//    protected void initializeLazyFields(Person person) {
//        Hibernate.initialize(person.getLocation());
//        Hibernate.initialize(person.getCoordinates());
//    }

    @Override
    protected boolean canDelete(Long id, Person person) {
        return true;
    }

    @Transactional
    public long getTotalCount() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root));
        return session.createQuery(query).uniqueResult();

    }

    @Transactional
    public List<Person> findWithPagination(int first, int pageSize, String field, String direction) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Person.class);
        var root = query.from(Person.class);

        query.select(root);

        // Добавляем сортировку
        if ("ASC".equalsIgnoreCase(direction)) {
            query.orderBy(cb.asc(root.get(field)));
        } else {
            query.orderBy(cb.desc(root.get(field)));
        }

        // Инициализируем ленивые поля
//        persons.forEach(this::initializeLazyFields);
        return session.createQuery(query)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .list();

    }

    @Transactional
    public List<Person> search(int first, int pageSize, String field, String namePattern, String direction) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Person.class);
        var root = query.from(Person.class);

        query.select(root)
                .where(cb.like(cb.lower(root.get(field)), "%" + namePattern.toLowerCase() + "%"));

        // Добавляем сортировку
        if ("ASC".equalsIgnoreCase(direction)) {
            query.orderBy(cb.asc(root.get(field)));
        } else {
            query.orderBy(cb.desc(root.get(field)));
        }

        //        persons.forEach(this::initializeLazyFields);
        return session.createQuery(query)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .list();

    }

    @Transactional
    public Optional<Long> findByPassportID(String passportID) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(root.get("id"))
                .where(cb.equal(root.get("passportID"), passportID));

        Long result = session.createQuery(query).setMaxResults(1).uniqueResult();
        return Optional.ofNullable(result);
    }

    @Transactional
    public Optional<Person> findMinPassportID() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Person.class);
        var root = query.from(Person.class);

        query.select(root)
                .orderBy(cb.asc(root.get("passportID")));

        Person person = session.createQuery(query).setMaxResults(1).uniqueResult();
//        if (person != null) {
//            initializeLazyFields(person);
//        }
        return Optional.ofNullable(person);
    }

    @Transactional
    public long countByNationalityLessThan(Country nationality) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.lessThan(root.get("nationality"), nationality.name()));

        return session.createQuery(query).uniqueResult();
    }

    @Transactional
    public long countByNationalityGreaterThan(Country nationality) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.greaterThan(root.get("nationality"), nationality.name()));

        return session.createQuery(query).uniqueResult();
    }

    @Transactional
    public long countByHairColor(Color hairColor) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("hairColor"), hairColor.name()));

        return session.createQuery(query).uniqueResult();
    }

    @Transactional
    public long countByEyeColor(Color eyeColor) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("eyeColor"), eyeColor.name()));

        return session.createQuery(query).uniqueResult();
    }


    @Transactional
    public boolean deletePersonAndAllTransitivelyRelated(Long initialPersonId) {

        Session session = factory.getCurrentSession();

        Set<Long> personIds = new HashSet<>();
        Set<Long> coordIds = new HashSet<>();
        Set<Long> locationIds = new HashSet<>();

        collectRelatedIds(initialPersonId, personIds, coordIds, locationIds, session);

        System.out.println("Найдено для удаления: " + personIds.size() + " persons, " +
                coordIds.size() + " coordinates, " + locationIds.size() + " locations");

        if (!personIds.isEmpty()) {
            int deletedPersons = session.createMutationQuery("DELETE FROM Person p WHERE p.id IN :ids")
                    .setParameter("ids", personIds)
                    .executeUpdate();
            System.out.println("Удаление людей прошло успешно: " + deletedPersons);
        }

        if (!coordIds.isEmpty()) {
            int deletedCoords = session.createMutationQuery("DELETE FROM Coordinates c WHERE c.id IN :ids")
                    .setParameter("ids", coordIds)
                    .executeUpdate();
            System.out.println("Удаление координат прошло успешно: " + deletedCoords);
        }

        if (!locationIds.isEmpty()) {
            int deletedLocations = session.createMutationQuery("DELETE FROM Location l WHERE l.id IN :ids")
                    .setParameter("ids", locationIds)
                    .executeUpdate();
            System.out.println("Удаление локаций прошло успешно: " + deletedLocations);
        }

        events.fire(new DbEvent("DELETED", initialPersonId, "Person"));
        return true;
    }

    private void collectRelatedIds(Long initialPersonId, Set<Long> personIds, Set<Long> coordIds,
                                   Set<Long> locationIds, Session session) {
        Person initial = session.get(Person.class, initialPersonId);
        if (initial == null) {
            return;
        }

        personIds.add(initialPersonId);
        coordIds.add(initial.getCoordinates().getId());
        locationIds.add(initial.getLocation().getId());

        boolean changed;
        do {
            changed = false;

            List<Long> newPersonIds = findPersonIdsByCoordOrLocation(coordIds, locationIds, session);

            for (Long pid : newPersonIds) {
                if (personIds.add(pid)) {
                    changed = true;
                }
            }

            if (changed && !newPersonIds.isEmpty()) {
                List<Person> newPersons = session.createSelectionQuery(
                                "SELECT p FROM Person p WHERE p.id IN :ids", Person.class)
                        .setParameter("ids", newPersonIds)
                        .getResultList();

                for (Person p : newPersons) {
                    if (coordIds.add(p.getCoordinates().getId())) changed = true;
                    if (locationIds.add(p.getLocation().getId())) changed = true;
                }
            }

        } while (changed);
    }

    protected List<Long> findPersonIdsByCoordOrLocation(Set<Long> coordIds, Set<Long> locationIds, Session session) {
        if (coordIds.isEmpty() && locationIds.isEmpty()) {
            return Collections.emptyList();
        }

        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(root.get("id"));

        if (coordIds.isEmpty()) {
            query.where(cb.in(root.get("location").get("id")).value(locationIds));
        } else if (locationIds.isEmpty()) {
            query.where(cb.in(root.get("coordinates").get("id")).value(coordIds));
        } else {
            query.where(cb.or(
                    cb.in(root.get("coordinates").get("id")).value(coordIds),
                    cb.in(root.get("location").get("id")).value(locationIds)
            ));
        }

        return session.createQuery(query).list();
    }
}