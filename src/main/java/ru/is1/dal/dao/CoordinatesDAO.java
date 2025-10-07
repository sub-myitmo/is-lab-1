package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.config.utils.HibernateUtil;
import ru.is1.dal.entity.Coordinates;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@MonitorPerformance
public class CoordinatesDAO extends AbstractDAO<Coordinates> {
    public CoordinatesDAO() {
        super(Coordinates.class);
    }

    @Override
    protected void initializeLazyFields(Session session, Coordinates coordinates) {}

    @Override
    protected boolean canDelete(Session session, Long id, Coordinates coordinates) {
        return !isCoordinatesUsed(id);
    }

    public List<Coordinates> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Coordinates", Coordinates.class).list();
        }
    }

    public List<Coordinates> findAllWithPagination(int first, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Coordinates", Coordinates.class)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
        }
    }

    public List<Coordinates> findByXValue(Float x) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Coordinates c WHERE c.x = :x", Coordinates.class)
                    .setParameter("x", x)
                    .list();
        }
    }

    public List<Coordinates> findByYValue(Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Coordinates c WHERE c.y = :y", Coordinates.class)
                    .setParameter("y", y)
                    .list();
        }
    }

    public List<Coordinates> findByXAndY(Float x, Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM Coordinates c WHERE c.x = :x AND c.y = :y", Coordinates.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .list();
        }
    }

    private boolean isCoordinatesUsed(Long coordinatesId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.coordinates.id = :coordinatesId", Long.class)
                    .setParameter("coordinatesId", coordinatesId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public long getTotalCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("SELECT COUNT(c) FROM Coordinates c", Long.class)
                    .uniqueResult();
        }
    }

    public List<Coordinates> findUnusedCoordinates() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM Coordinates c WHERE c.id NOT IN " +
                                    "(SELECT p.coordinates.id FROM Person p WHERE p.coordinates IS NOT NULL)", Coordinates.class)
                    .list();
        }
    }

    public boolean existsByXAndY(Float x, Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                            "SELECT COUNT(c) FROM Coordinates c WHERE c.x = :x AND c.y = :y", Long.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public Optional<Coordinates> findByXAndYFirst(Float x, Integer y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Coordinates coordinates = session.createSelectionQuery(
                            "FROM Coordinates c WHERE c.x = :x AND c.y = :y", Coordinates.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(coordinates);
        }
    }
}