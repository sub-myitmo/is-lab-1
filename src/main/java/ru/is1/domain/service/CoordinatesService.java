package ru.is1.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.dao.CoordinatesDAO;
import ru.is1.dal.entity.Coordinates;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@MonitorPerformance
public class CoordinatesService {

    @Inject
    private CoordinatesDAO coordinatesDAO;

    public Coordinates createCoordinates(Coordinates coordinates) {
        validateCoordinates(coordinates);
        return coordinatesDAO.save(coordinates); // транзакция внутри DAO
    }

    public Optional<Coordinates> getCoordinatesById(Long id) {
        return coordinatesDAO.findById(id);
    }

    public List<Coordinates> getAllCoordinates() {
        return coordinatesDAO.findAll();
    }

    public List<Coordinates> getUnusedCoordinates() {
        return coordinatesDAO.findUnusedCoordinates();
    }

    public Coordinates updateCoordinates(Long id, Coordinates coordinates) {
        validateCoordinates(coordinates);
        coordinates.setId(id);
        return coordinatesDAO.save(coordinates); // транзакция внутри DAO
    }

    public boolean deleteCoordinates(Long id) {
        if (coordinatesDAO.isCoordinatesUsed(id)) {
            throw new IllegalStateException("Cannot delete coordinates used by persons");
        }
        return coordinatesDAO.delete(id); // транзакция внутри DAO
    }

    public boolean isCoordinatesUsed(Long coordinatesId) {
        return coordinatesDAO.isCoordinatesUsed(coordinatesId);
    }

    private void validateCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates cannot be null");
        if (coordinates.getX() == null) throw new IllegalArgumentException("X coordinate cannot be null");
    }
}