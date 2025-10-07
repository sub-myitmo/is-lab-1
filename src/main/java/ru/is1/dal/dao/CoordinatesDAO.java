package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.Coordinates;
import ru.is1.dal.entity.Person;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@MonitorPerformance
public class CoordinatesDAO extends AbstractDAO<Coordinates> {

    public CoordinatesDAO() {
        super(Coordinates.class);
    }

    @Override
    protected void initializeLazyFields(Coordinates coordinates) {
    }

    @Override
    protected boolean canDelete(Long id, Coordinates coordinates) {
        return !isCoordinatesUsed(id);
    }

    public List<Coordinates> findAll() {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Coordinates.class);
            var root = query.from(Coordinates.class);

            query.select(root);
            return session.createQuery(query).list();
        }
    }

    public List<Coordinates> findAllWithPagination(int first, int pageSize) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Coordinates.class);
            var root = query.from(Coordinates.class);

            query.select(root);

            return session.createQuery(query)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
        }
    }

    public List<Coordinates> findByXValue(Float x) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Coordinates.class);
            var root = query.from(Coordinates.class);

            query.select(root).where(cb.equal(root.get("x"), x));
            return session.createQuery(query).list();
        }
    }

    public List<Coordinates> findByYValue(Integer y) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Coordinates.class);
            var root = query.from(Coordinates.class);

            query.select(root).where(cb.equal(root.get("y"), y));
            return session.createQuery(query).list();
        }
    }

    public List<Coordinates> findByXAndY(Float x, Integer y) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Coordinates.class);
            var root = query.from(Coordinates.class);

            query.select(root).where(cb.and(
                    cb.equal(root.get("x"), x),
                    cb.equal(root.get("y"), y)
            ));
            return session.createQuery(query).list();
        }
    }

    public boolean existsByXAndY(Float x, Integer y) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Coordinates.class);

            query.select(cb.count(root)).where(cb.and(
                    cb.equal(root.get("x"), x),
                    cb.equal(root.get("y"), y)
            ));

            Long count = session.createQuery(query).uniqueResult();
            return count != null && count > 0;
        }
    }

    public Optional<Coordinates> findByXAndYFirst(Float x, Integer y) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Coordinates.class);
            var root = query.from(Coordinates.class);

            query.select(root).where(cb.and(
                    cb.equal(root.get("x"), x),
                    cb.equal(root.get("y"), y)
            ));

            Coordinates coordinates = session.createQuery(query).setMaxResults(1).uniqueResult();
            return Optional.ofNullable(coordinates);
        }
    }

    private boolean isCoordinatesUsed(Long coordinatesId) {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Person.class);

            query.select(cb.count(root))
                    .where(cb.equal(root.get("coordinates").get("id"), coordinatesId));

            Long count = session.createQuery(query).uniqueResult();
            return count != null && count > 0;
        }
    }

    public long getTotalCount() {
        try (Session session = factory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(Long.class);
            var root = query.from(Coordinates.class);

            query.select(cb.count(root));
            return session.createQuery(query).uniqueResult();
        }
    }

    public List<Coordinates> findUnusedCoordinates() {
        try (Session session = factory.openSession()) {
            return session.createSelectionQuery(
                    "FROM Coordinates c WHERE c.id NOT IN " +
                            "(SELECT p.coordinates.id FROM Person p WHERE p.coordinates IS NOT NULL)",
                    Coordinates.class
            ).list();
        }
    }
}