package ru.is1.dal.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.is1.config.utils.HibernateUtil;
import ru.is1.dal.Identifiable;

import java.util.Optional;

public abstract class AbstractDAO<T extends Identifiable> {
    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (entity.getId() == null) {
                session.persist(entity);
            } else {
                entity = session.merge(entity);
            }
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public T update(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            entity = session.merge(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Optional<T> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                initializeLazyFields(session, entity);
            }
            return Optional.ofNullable(entity);
        }
    }

    public boolean delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T entity = session.get(entityClass, id);

            if (entity != null && canDelete(session, id, entity)) {
                session.remove(entity);
                tx.commit();
                return true;
            }

            // Если не можем удалить, откатываем транзакцию
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return false;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return false;
        }
    }

    protected boolean canDelete(Session session, Long id, T entity) {
        return true;
    };

    protected abstract void initializeLazyFields(Session session, T entity);

}
