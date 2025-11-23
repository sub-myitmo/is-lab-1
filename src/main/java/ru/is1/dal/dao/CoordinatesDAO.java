package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.Coordinates;
import ru.is1.dal.entity.Person;

import java.util.List;

@ApplicationScoped
@MonitorPerformance
public class CoordinatesDAO extends AbstractDAO<Coordinates> {

    public CoordinatesDAO() {
        super(Coordinates.class);
    }


    public List<Coordinates> findAll() {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Coordinates.class);
        var root = query.from(Coordinates.class);

        query.select(root);
        return session.createQuery(query).list();

    }

    public List<Coordinates> findByXAndY(Float x, Integer y) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Coordinates.class);
        var root = query.from(Coordinates.class);

        query.select(root).where(cb.and(
                cb.equal(root.get("x"), x),
                cb.equal(root.get("y"), y)
        ));
        return session.createQuery(query).list();

    }

    private boolean isCoordinatesUsed(Long coordinatesId) {
        Session session = factory.getCurrentSession();
        var cb = session.getCriteriaBuilder();
        var query = cb.createQuery(Long.class);
        var root = query.from(Person.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("coordinates").get("id"), coordinatesId));

        Long count = session.createQuery(query).uniqueResult();
        return count != null && count > 0;

    }

//    public long getTotalCount() {
//        Session session = factory.getCurrentSession();
//        var cb = session.getCriteriaBuilder();
//        var query = cb.createQuery(Long.class);
//        var root = query.from(Coordinates.class);
//
//        query.select(cb.count(root));
//        return session.createQuery(query).uniqueResult();
//
//    }

    public List<Coordinates> findUnusedCoordinates() {
        Session session = factory.getCurrentSession();
        return session.createSelectionQuery(
                "FROM Coordinates c WHERE c.id NOT IN " +
                        "(SELECT p.coordinates.id FROM Person p WHERE p.coordinates IS NOT NULL)",
                Coordinates.class
        ).list();
    }

    public int deleteUnusedCoordinates() {
        Session session = factory.getCurrentSession();
        return session.createMutationQuery(
                "DELETE FROM Coordinates c " +
                        "WHERE NOT EXISTS (" +
                        "   SELECT 1 FROM Person p WHERE p.coordinates = c" +
                        ")"
        ).executeUpdate();
    }
}