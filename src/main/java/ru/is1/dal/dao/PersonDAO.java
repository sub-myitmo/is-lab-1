package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.config.utils.HibernateUtil;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Person;

import java.util.*;

@ApplicationScoped
@MonitorPerformance
public class PersonDAO {

    public Person save(Person person) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (person.getId() == null) {
                session.persist(person);
            } else {
                person = session.merge(person);
            }
            tx.commit();
            return person;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Optional<Person> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Person person = session.get(Person.class, id);
            if (person != null) {
                Hibernate.initialize(person.getLocation());
                Hibernate.initialize(person.getCoordinates());
            }
            return Optional.ofNullable(person);
        }
    }

    public Person update(Person person) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            person = session.merge(person);
            tx.commit();
            return person;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public boolean deletePersonAndAllTransitivelyRelated(Long initialPersonId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. Проверяем, существует ли начальный Person
            Person initial = session.get(Person.class, initialPersonId);
            if (initial == null) {
                tx.rollback(); // или commit — но rollback логичнее при "ничего не сделано"
                return false;
            }

            // 2. Инициализируем множества для сбора всех связанных сущностей
            Set<Long> personIds = new HashSet<>();
            Set<Long> coordIds = new HashSet<>();
            Set<Long> locationIds = new HashSet<>();

            personIds.add(initialPersonId);
            coordIds.add(initial.getCoordinates().getId());
            locationIds.add(initial.getLocation().getId());

            boolean changed;
            do {
                changed = false;

                // Получаем ID новых Person по текущим связям
                List<Long> newPersonIds = findPersonIdsByCoordOrLocation(session, coordIds, locationIds);

                // Добавляем новых Person
                for (Long pid : newPersonIds) {
                    if (personIds.add(pid)) {
                        changed = true;
                    }
                }

                // Если есть новые Person — собираем их Coordinates и Location
                if (changed && !newPersonIds.isEmpty()) {
                    List<Person> newPersons = session.createQuery(
                                    "SELECT p FROM Person p WHERE p.id IN :ids", Person.class)
                            .setParameter("ids", newPersonIds)
                            .getResultList();

                    for (Person p : newPersons) {
                        if (coordIds.add(p.getCoordinates().getId())) changed = true;
                        if (locationIds.add(p.getLocation().getId())) changed = true;
                    }
                }

            } while (changed);

            // 3. Удаляем всё (в правильном порядке: сначала Person, потом зависимости)
            if (!personIds.isEmpty()) {
                session.createQuery("DELETE FROM Person p WHERE p.id IN :ids")
                        .setParameter("ids", personIds)
                        .executeUpdate();
            }

            if (!coordIds.isEmpty()) {
                session.createQuery("DELETE FROM Coordinates c WHERE c.id IN :ids")
                        .setParameter("ids", coordIds)
                        .executeUpdate();
            }

            if (!locationIds.isEmpty()) {
                session.createQuery("DELETE FROM Location l WHERE l.id IN :ids")
                        .setParameter("ids", locationIds)
                        .executeUpdate();
            }

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    private List<Long> findPersonIdsByCoordOrLocation(Session session, Set<Long> coordIds, Set<Long> locationIds) {
        if (coordIds.isEmpty() && locationIds.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder jpql = new StringBuilder("SELECT p.id FROM Person p WHERE ");
        boolean hasCoord = !coordIds.isEmpty();
        boolean hasLocation = !locationIds.isEmpty();

        if (hasCoord) {
            jpql.append("p.coordinates.id IN :coordIds");
        }
        if (hasLocation) {
            if (hasCoord) {
                jpql.append(" OR ");
            }
            jpql.append("p.location.id IN :locationIds");
        }

        Query<Long> query = session.createQuery(jpql.toString(), Long.class);

        if (hasCoord) {
            query.setParameter("coordIds", coordIds);
        }
        if (hasLocation) {
            query.setParameter("locationIds", locationIds);
        }

        return query.getResultList();
    }
//
//    public boolean delete(Long id) {
//        Transaction tx = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            tx = session.beginTransaction();
//            Person person = session.get(Person.class, id);
//            if (person != null) {
//                session.remove(person);
//                tx.commit();
//                return true;
//            }
//            tx.rollback();
//            return false;
//        } catch (Exception e) {
//            if (tx != null) tx.rollback();
//            return false;
//        }
//    }

    public long getTotalCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(p) FROM Person p", Long.class)
                    .uniqueResult();
        }
    }

    public List<Person> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Person> persons = session.createQuery("FROM Person", Person.class).list();
            // Инициализируем связи для всех объектов
            for (Person p : persons) {
                Hibernate.initialize(p.getLocation());
                Hibernate.initialize(p.getCoordinates());
            }
            return persons;
        }
    }

    public List<Person> findWithPagination(int first, int pageSize, String field, String direction) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Person> persons = session.createQuery("FROM Person p ORDER BY p." + field + " " + direction, Person.class)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
            for (Person p : persons) {
                Hibernate.initialize(p.getLocation());
                Hibernate.initialize(p.getCoordinates());
            }
            return persons;
        }
    }

    // Специальные операции
    public List<Person> search(int first, int pageSize, String field, String namePattern, String direction) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Person> persons = session.createQuery(
                            "FROM Person p WHERE LOWER(p." + field + ") LIKE LOWER(:pattern) ORDER BY p." + field + " " + direction, Person.class)
                    .setParameter("pattern", "%" + namePattern + "%")
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
            for (Person p : persons) {
                Hibernate.initialize(p.getLocation());
                Hibernate.initialize(p.getCoordinates());
            }
            return persons;
        }
    }

    public Optional<Long> findByPassportID(String passportID) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long id = session.createQuery(
                            "SELECT p.id FROM Person p WHERE p.passportID = :passportID", Long.class)
                    .setParameter("passportID", passportID)
                    .setMaxResults(1)
                    .uniqueResult(); // ← обязательно вызвать!
            return Optional.ofNullable(id);
        }
    }

    public Optional<Person> findMinPassportID() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Person person = session.createQuery(
                            "FROM Person p ORDER BY p.passportID ASC", Person.class)
                    .setMaxResults(1)
                    .uniqueResult();
            if (person != null) {
                Hibernate.initialize(person.getLocation());
                Hibernate.initialize(person.getCoordinates());
            }
            return Optional.ofNullable(person);
        }
    }

    public long countByNationalityLessThan(Country nationality) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.nationality < :nationality", Long.class)
                    .setParameter("nationality", nationality)
                    .uniqueResult();
        }
    }

    public long countByNationalityGreaterThan(Country nationality) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.nationality > :nationality", Long.class)
                    .setParameter("nationality", nationality)
                    .uniqueResult();
        }
    }

    public long countByHairColor(Color hairColor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.hairColor = :hairColor", Long.class)
                    .setParameter("hairColor", hairColor)
                    .uniqueResult();
        }
    }

    public long countByEyeColor(Color eyeColor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.eyeColor = :eyeColor", Long.class)
                    .setParameter("eyeColor", eyeColor)
                    .uniqueResult();
        }
    }
}