package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.config.utils.HibernateUtil;
import ru.is1.dal.entity.Coordinates;
import ru.is1.dal.entity.Location;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@MonitorPerformance
public class LocationDAO extends AbstractDAO<Location> {
    public LocationDAO() {
        super(Location.class);
    }

    @Override
    protected void initializeLazyFields(Session session, Location location) {}

    @Override
    protected boolean canDelete(Session session, Long id, Location location) {
        return !isLocationUsed(id);
    }

    public List<Location> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Location", Location.class).list();
        }
    }

    public List<Location> findAllWithPagination(int first, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Location", Location.class)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
        }
    }

    public List<Location> findByXValue(Integer x) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Location l WHERE l.x = :x", Location.class)
                    .setParameter("x", x)
                    .list();
        }
    }

    public List<Location> findByYValue(Long y) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Location l WHERE l.y = :y", Location.class)
                    .setParameter("y", y)
                    .list();
        }
    }

    public List<Location> findByZValue(Long z) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("FROM Location l WHERE l.z = :z", Location.class)
                    .setParameter("z", z)
                    .list();
        }
    }

    public List<Location> findByXAndYAndZ(Integer x, Long y, Long z) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM Location l WHERE l.x = :x AND l.y = :y AND l.z = :z", Location.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .setParameter("z", z)
                    .list();
        }
    }


    public boolean isLocationUsed(Long locationId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                            "SELECT COUNT(p) FROM Person p WHERE p.location.id = :locationId", Long.class)
                    .setParameter("locationId", locationId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public long getTotalCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("SELECT COUNT(l) FROM Location l", Long.class)
                    .uniqueResult();
        }
    }

    public List<Location> findUnusedLocations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "FROM Location l WHERE l.id NOT IN " +
                                    "(SELECT p.location.id FROM Person p WHERE p.location IS NOT NULL)", Location.class)
                    .list();
        }
    }

    public boolean existsByXAndYAndZ(Integer x, Long y, Long z) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                            "SELECT COUNT(l) FROM Location l WHERE l.x = :x AND l.y = :y AND l.z = :z", Long.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .setParameter("z", z)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public Optional<Location> findByXAndYAndZFirst(Integer x, Long y, Long z) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Location location = session.createSelectionQuery(
                            "FROM Location l WHERE l.x = :x AND l.y = :y AND l.z = :z", Location.class)
                    .setParameter("x", x)
                    .setParameter("y", y)
                    .setParameter("z", z)
                    .setMaxResults(1)
                    .uniqueResult();
            return Optional.ofNullable(location);
        }
    }

    public List<Location> searchLocations(Integer x, Long y, Long z) {
        StringBuilder hql = new StringBuilder("FROM Location l WHERE 1=1");
        if (x != null) hql.append(" AND l.x = :x");
        if (y != null) hql.append(" AND l.y = :y");
        if (z != null) hql.append(" AND l.z = :z");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var query = session.createSelectionQuery(hql.toString(), Location.class);
            if (x != null) query.setParameter("x", x);
            if (y != null) query.setParameter("y", y);
            if (z != null) query.setParameter("z", z);
            return query.list();
        }
    }
}