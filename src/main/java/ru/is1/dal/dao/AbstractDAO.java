package ru.is1.dal.dao;

import jakarta.inject.Inject;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import ru.is1.config.utils.HibernateSessionFactory;
import ru.is1.dal.Identifiable;

import java.util.Optional;

public abstract class AbstractDAO<T extends Identifiable> {
    private final Class<T> entityClass;

    @Inject
    protected HibernateSessionFactory factory;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional
    public T save(T entity) {
        try (Session session = factory.openSession()) {
            if (entity.getId() == null) {
                session.persist(entity);
            } else {
                entity = session.merge(entity);
            }
            return entity;
        }
    }

    @Transactional
    public T update(T entity) {
        try (Session session = factory.openSession()) {
            return session.merge(entity);
        }
    }

    public Optional<T> findById(Long id) {
        try (Session session = factory.openSession()) {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                initializeLazyFields(entity);
            }
            return Optional.ofNullable(entity);
        }
    }

    @Transactional
    public boolean delete(Long id) {
        try (Session session = factory.openSession()) {
            T entity = session.get(entityClass, id);

            if (entity != null && canDelete(id, entity)) {
                session.remove(entity);
                return true;
            }
            return false;
        }
    }

    protected abstract boolean canDelete(Long id, T entity);

    protected abstract void initializeLazyFields(T entity);
}