package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.Location;
import ru.is1.dal.entity.Person;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@MonitorPerformance
public class LocationDAO extends AbstractDAO<Location> {

    public LocationDAO() {
        super(Location.class);
    }

//    @Override
//    protected void initializeLazyFields(Location location) {
//    }

    @Override
    protected boolean canDelete(Long id, Location location) {
        return !isLocationUsed(id);
    }

    @Transactional
    public List<Location> findAll() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root);
        return session.createQuery(query).list();

    }

    public List<Location> findAllWithPagination(int first, int pageSize) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root);

        return session.createQuery(query)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .list();

    }

    public List<Location> findByXValue(Integer x) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root).where(cb.equal(root.get("x"), x));
        return session.createQuery(query).list();

    }

    public List<Location> findByYValue(Long y) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root).where(cb.equal(root.get("y"), y));
        return session.createQuery(query).list();

    }

    public List<Location> findByZValue(Long z) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root).where(cb.equal(root.get("z"), z));
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

    public boolean existsByXAndYAndZ(Integer x, Long y, Long z) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Location.class);

        query.select(cb.count(root)).where(cb.and(
                cb.equal(root.get("x"), x),
                cb.equal(root.get("y"), y),
                cb.equal(root.get("z"), z)
        ));

        Long count = session.createQuery(query).uniqueResult();
        return count != null && count > 0;

    }

    public Optional<Location> findByXAndYAndZFirst(Integer x, Long y, Long z) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Location.class);
        var root = query.from(Location.class);

        query.select(root).where(cb.and(
                cb.equal(root.get("x"), x),
                cb.equal(root.get("y"), y),
                cb.equal(root.get("z"), z)
        ));

        Location location = session.createQuery(query).setMaxResults(1).uniqueResult();
        return Optional.ofNullable(location);

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

    public long getTotalCount() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Location.class);

        query.select(cb.count(root));
        return session.createQuery(query).uniqueResult();

    }

    public List<Location> findUnusedLocations() {
        Session session = factory.getCurrentSession();
        return session.createSelectionQuery(
                "FROM Location l WHERE l.id NOT IN " +
                        "(SELECT p.location.id FROM Person p WHERE p.location IS NOT NULL)",
                Location.class
        ).list();

    }
}