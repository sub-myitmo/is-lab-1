package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
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

    @Override
    protected void initializeLazyFields(Person person) {
        Hibernate.initialize(person.getLocation());
        Hibernate.initialize(person.getCoordinates());
    }

    @Override
    protected boolean canDelete(Long id, Person person) {
        return true;
    }

    public long getTotalCount() {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(cb.count(root));
            return session.createQuery(query).uniqueResult();
        }
    }

    public List<Person> findWithPagination(int first, int pageSize, String field, String direction) {
        try (Session session = factory.openSession()) {
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

            List<Person> persons = session.createQuery(query)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();

            // Инициализируем ленивые поля
            persons.forEach(this::initializeLazyFields);
            return persons;
        }
    }

    public List<Person> search(int first, int pageSize, String field, String namePattern, String direction) {
        try (Session session = factory.openSession()) {
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

            List<Person> persons = session.createQuery(query)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();

            persons.forEach(this::initializeLazyFields);
            return persons;
        }
    }

    public Optional<Long> findByPassportID(String passportID) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(root.get("id"))
                    .where(cb.equal(root.get("passportID"), passportID));

            Long result = session.createQuery(query).setMaxResults(1).uniqueResult();
            return Optional.ofNullable(result);
        }
    }

    public Optional<Person> findMinPassportID() {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Person.class);
            var root = query.from(Person.class);

            query.select(root)
                    .orderBy(cb.asc(root.get("passportID")));

            Person person = session.createQuery(query).setMaxResults(1).uniqueResult();
            if (person != null) {
                initializeLazyFields(person);
            }
            return Optional.ofNullable(person);
        }
    }

    public long countByNationalityLessThan(Country nationality) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(cb.count(root))
                    .where(cb.lessThan(root.get("nationality"), nationality));

            return session.createQuery(query).uniqueResult();
        }
    }

    public long countByNationalityGreaterThan(Country nationality) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(cb.count(root))
                    .where(cb.greaterThan(root.get("nationality"), nationality));

            return session.createQuery(query).uniqueResult();
        }
    }

    public long countByHairColor(Color hairColor) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(cb.count(root))
                    .where(cb.equal(root.get("hairColor"), hairColor));

            return session.createQuery(query).uniqueResult();
        }
    }

    public long countByEyeColor(Color eyeColor) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(cb.count(root))
                    .where(cb.equal(root.get("eyeColor"), eyeColor));

            return session.createQuery(query).uniqueResult();
        }
    }


    @Transactional
    public boolean deletePersonAndAllTransitivelyRelated(Long initialPersonId) {
        try (Session session = factory.openSession()) {
            Person initial = session.get(Person.class, initialPersonId);
            if (initial == null) {
                return false;
            }

            Set<Long> personIds = new HashSet<>();
            Set<Long> coordIds = new HashSet<>();
            Set<Long> locationIds = new HashSet<>();

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

                // Если есть новые Person — собираем их Coordinates и Location
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

            // Удаляем всё (в правильном порядке: сначала Person, потом зависимости)
            if (!personIds.isEmpty()) {
                session.createMutationQuery("DELETE FROM Person p WHERE p.id IN :ids")
                        .setParameter("ids", personIds)
                        .executeUpdate();
            }

            if (!coordIds.isEmpty()) {
                session.createMutationQuery("DELETE FROM Coordinates c WHERE c.id IN :ids")
                        .setParameter("ids", coordIds)
                        .executeUpdate();
            }

            if (!locationIds.isEmpty()) {
                session.createMutationQuery("DELETE FROM Location l WHERE l.id IN :ids")
                        .setParameter("ids", locationIds)
                        .executeUpdate();
            }

            return true;
        }
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