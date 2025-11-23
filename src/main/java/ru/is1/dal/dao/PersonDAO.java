package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
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

        return session.createQuery(query)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .list();

    }

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

    public Optional<Person> findMinPassportID() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Person.class);
        var root = query.from(Person.class);

        query.select(root)
                .orderBy(cb.asc(root.get("passportID")));

        Person person = session.createQuery(query).setMaxResults(1).uniqueResult();
        return Optional.ofNullable(person);
    }

    public long countByNationalityLessThan(Country nationality) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.lessThan(root.get("nationality"), nationality.name()));

        return session.createQuery(query).uniqueResult();
    }

    public long countByNationalityGreaterThan(Country nationality) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.greaterThan(root.get("nationality"), nationality.name()));

        return session.createQuery(query).uniqueResult();
    }

    public long countByHairColor(Color hairColor) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("hairColor"), hairColor.name()));

        return session.createQuery(query).uniqueResult();
    }


    public long countByEyeColor(Color eyeColor) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("eyeColor"), eyeColor.name()));

        return session.createQuery(query).uniqueResult();
    }
}