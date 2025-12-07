package ru.is1.dal.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.entity.Coordinates;
import ru.is1.dal.entity.Person;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@MonitorPerformance
public class CoordinatesDAO extends AbstractDAO<Coordinates> {

    public CoordinatesDAO() {
        super(Coordinates.class);
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

    @Override
    public void throwIfExistsAnyByEntity(Coordinates coordinates) {
        if (existsAnyByEntity(List.of(coordinates))) {
            throw new IllegalArgumentException("Location with this fields already exists");
        }
    }

    @Override
    public boolean existsAnyByEntity(List<Coordinates> coordinatesList) {
        if (coordinatesList == null || coordinatesList.isEmpty()) {
            return false;
        }

        Session session = factory.getCurrentSession();

        List<Coordinates> validCoords = coordinatesList.stream()
                .filter(c -> c != null && c.getX() != null)
                .toList();

        if (validCoords.isEmpty()) {
            return false;
        }

        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(c) FROM Coordinates c WHERE "
        );

        List<Object> parameters = new ArrayList<>();

        for (int i = 0; i < validCoords.size(); i++) {
            if (i > 0) {
                hql.append(" OR ");
            }
            hql.append("(c.x = :x").append(i).append(" AND c.y = :y").append(i).append(")");
            parameters.add(validCoords.get(i).getX());
            parameters.add(validCoords.get(i).getY());
        }

        Query<Long> query = session.createQuery(hql.toString(), Long.class);
        for (int i = 0; i < validCoords.size(); i++) {
            query.setParameter("x" + i, validCoords.get(i).getX());
            query.setParameter("y" + i, validCoords.get(i).getY());
        }

        Long count = query.uniqueResult();
        return count != null && count > 0;
    }
}