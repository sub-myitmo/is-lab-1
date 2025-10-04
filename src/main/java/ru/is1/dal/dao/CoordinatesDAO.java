package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.is1.config.utils.HibernateUtil;
import ru.is1.dal.entity.Coordinates;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoordinatesDAO {

    public Coordinates save(Coordinates coordinates) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (coordinates.getId() == null) {
                session.persist(coordinates);
            } else {
                coordinates = session.merge(coordinates);
            }
            tx.commit();
            return coordinates;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Optional<Coordinates> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Coordinates coordinates = session.get(Coordinates.class, id);
            return Optional.ofNullable(coordinates);
        }
    }

    public List<Coordinates> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Coordinates", Coordinates.class).list();
        }
    }

    public List<Coordinates> findAllWithPagination(int first, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Coordinates", Coordinates.class)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
        }
    }

    public List<Coordinates> findByXValue(Float x) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Coordinates c WHERE c.x = :x", Coordinates.class)
                    .setParameter("x", x)
                    .list();
        }
    }

    public List<Coordinates> findByYValue(Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Coordinates c WHERE c.y = :y", Coordinates.class)
                    .setParameter("y", y)
                    .list();
        }
    }

    public List<Coordinates> findByXAndY(Float x, Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Coordinates c WHERE c.x = :x AND c.y = :y", Coordinates.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .list();
        }
    }

    public boolean delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Coordinates coordinates = session.get(Coordinates.class, id);
            if (coordinates != null && !isCoordinatesUsed(id)) {
                session.remove(coordinates);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    public boolean isCoordinatesUsed(Long coordinatesId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.coordinates.id = :coordinatesId", Long.class)
                    .setParameter("coordinatesId", coordinatesId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public long getTotalCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(c) FROM Coordinates c", Long.class)
                    .uniqueResult();
        }
    }

    public List<Coordinates> findUnusedCoordinates() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Coordinates c WHERE c.id NOT IN " +
                                    "(SELECT p.coordinates.id FROM Person p WHERE p.coordinates IS NOT NULL)", Coordinates.class)
                    .list();
        }
    }

    public boolean existsByXAndY(Float x, Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(c) FROM Coordinates c WHERE c.x = :x AND c.y = :y", Long.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public Optional<Coordinates> findByXAndYFirst(Float x, Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Coordinates coordinates = session.createQuery(
                            "FROM Coordinates c WHERE c.x = :x AND c.y = :y", Coordinates.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(coordinates);
        }
    }
}