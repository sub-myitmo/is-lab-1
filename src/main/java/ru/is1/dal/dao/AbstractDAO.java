package ru.is1.dal.dao;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import ru.is1.config.utils.HibernateSessionFactory;
import ru.is1.config.ws.DbEvent;
import ru.is1.dal.Identifiable;

import java.util.Optional;

public abstract class AbstractDAO<T extends Identifiable> {
    private final Class<T> entityClass;

    @Inject
    Event<DbEvent> events;

    @Inject
    protected HibernateSessionFactory factory;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional
    public T save(T entity) {
        Session session = factory.getCurrentSession();
        if (entity.getId() == null) {
            session.persist(entity);
            System.out.println("Saving: " + entity);
            session.flush();
            events.fire(new DbEvent("CREATED", entity.getId(), entity.getClass().getSimpleName()));
        }
        return entity;
    }

    @Transactional
    public T update(T entity) {
        Session session = factory.getCurrentSession();
        T managed = session.merge(entity);
        System.out.println("Updating: " + entity);
        session.flush();
        events.fire(new DbEvent("UPDATED", entity.getId(), entity.getClass().getSimpleName()));
        return managed;
    }


    @Transactional
    public Optional<T> findById(Long id) {
        Session session = factory.getCurrentSession();
        T entity = session.get(entityClass, id);
//        if (entity != null) {
//            initializeLazyFields(entity);
//        }
        return Optional.ofNullable(entity);
    }

    @Transactional
    public boolean delete(Long id) {
        Session session = factory.getCurrentSession();
        T entity = session.get(entityClass, id);

        if (entity != null && canDelete(id, entity)) {
            session.remove(entity);
            System.out.println("Removing: " + entity);
            session.flush();
            events.fire(new DbEvent("DELETED", id, entity.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    protected abstract boolean canDelete(Long id, T entity);

//    protected abstract void initializeLazyFields(T entity);
}