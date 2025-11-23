package ru.is1.dal.dao;

import jakarta.inject.Inject;

import org.hibernate.Session;
import ru.is1.config.utils.HibernateSessionFactory;
import ru.is1.dal.Identifiable;

import java.util.*;

public abstract class AbstractDAO<T extends Identifiable> {
    private final Class<T> entityClass;
    @Inject
    protected HibernateSessionFactory factory;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        Session session = factory.getCurrentSession();
        if (entity.getId() == null) {
            session.persist(entity);
            System.out.println("Saving: " + entity);
//            session.flush();
        }
        return entity;
    }

    public T update(T entity) {
        Session session = factory.getCurrentSession();
        T managed = session.merge(entity);
        System.out.println("Updating: " + entity);
//        session.flush();
        return managed;
    }


    public Optional<T> findById(Long id) {
        Session session = factory.getCurrentSession();
        T entity = session.get(entityClass, id);
        return Optional.ofNullable(entity);
    }

    public List<T> findWithPagination(int first, int pageSize, String field, String direction) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(entityClass);
        var root = query.from(entityClass);

        query.select(root);

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

    public boolean delete(Long id) {
        Session session = factory.getCurrentSession();
        T entity = session.get(entityClass, id);

        if (entity != null) {
            session.remove(entity);
            System.out.println("Removing: " + entity);
            return true;
        }
        return false;
    }

    public long getTotalCount() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(entityClass);

        query.select(cb.count(root));
        return session.createQuery(query).uniqueResult();

    }
}