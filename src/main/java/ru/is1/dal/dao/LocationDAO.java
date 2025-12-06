package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.Location;
import ru.is1.dal.entity.Person;

import java.util.List;

@ApplicationScoped
@MonitorPerformance
public class LocationDAO extends AbstractDAO<Location> {

    public LocationDAO() {
        super(Location.class);
    }

    public List<Location> findAll() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root);
        return session.createQuery(query).list();

    }

    public List<Location> findByXAndYAndZ(Integer x, Long y, Long z) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root).where(cb.and(
                cb.equal(root.get("x"), x),
                cb.equal(root.get("y"), y),
                cb.equal(root.get("z"), z)
        ));
        return session.createQuery(query).list();

    }

    private boolean isLocationUsed(Long locationId) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("location").get("id"), locationId));

        Long count = session.createQuery(query).uniqueResult();
        return count != null && count > 0;

    }

    public List<Location> findUnusedLocations() {
        Session session = factory.getCurrentSession();
        return session.createSelectionQuery(
                "FROM Location l WHERE l.id NOT IN " +
                        "(SELECT p.location.id FROM Person p WHERE p.location IS NOT NULL)",
                Location.class
        ).list();
    }

    public int deleteUnusedLocations() {
        Session session = factory.getCurrentSession();
        return session.createMutationQuery(
                "DELETE FROM Location l " +
                        "WHERE NOT EXISTS (" +
                        "   SELECT 1 FROM Person p WHERE p.location = l" +
                        ")"
        ).executeUpdate();
    }

    public boolean existsAnyByLocation(List<Location> locationList) {
        if (locationList == null || locationList.isEmpty()) {
            return false;
        }

        Session session = factory.getCurrentSession();

        // Фильтруем валидные локации (x обязателен)
        List<Location> validLocations = locationList.stream()
                .filter(loc -> loc != null && loc.getX() != null)
                .toList();

        if (validLocations.isEmpty()) {
            return false;
        }

        // Строим HQL запрос с учетом nullable полей
        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(l) FROM Location l WHERE "
        );

        for (int i = 0; i < validLocations.size(); i++) {
            if (i > 0) {
                hql.append(" OR ");
            }

            Location loc = validLocations.get(i);

            // x всегда есть, проверяем его
            hql.append("(l.x = :x").append(i);

            // Для y - разное условие в зависимости от null
            if (loc.getY() != null) {
                hql.append(" AND l.y = :y").append(i);
            } else {
                hql.append(" AND l.y IS NULL");
            }

            // Для z - разное условие в зависимости от null
            if (loc.getZ() != null) {
                hql.append(" AND l.z = :z").append(i);
            } else {
                hql.append(" AND l.z IS NULL");
            }

            hql.append(")");
        }

        Query<Long> query = session.createQuery(hql.toString(), Long.class);

        // Устанавливаем параметры
        for (int i = 0; i < validLocations.size(); i++) {
            Location loc = validLocations.get(i);
            query.setParameter("x" + i, loc.getX());

            if (loc.getY() != null) {
                query.setParameter("y" + i, loc.getY());
            }

            if (loc.getZ() != null) {
                query.setParameter("z" + i, loc.getZ());
            }
        }

        Long count = query.uniqueResult();
        return count != null && count > 0;
    }
}