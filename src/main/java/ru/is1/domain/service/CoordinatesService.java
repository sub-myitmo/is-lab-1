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
        return coordinatesDAO.save(coordinates);
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
        coordinates.setId(id);
        return coordinatesDAO.save(coordinates);
    }

    public boolean deleteCoordinates(Long id) {
        return coordinatesDAO.delete(id);
    }
}