package ru.is1.domain.service.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.is1.dal.dao.CoordinatesDAO;
import ru.is1.dal.entity.Coordinates;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoordinatesService implements BaseService<Coordinates> {

    @Inject
    private CoordinatesDAO coordinatesDAO;

    @Override
    @Transactional
    public Coordinates createEntity(Coordinates coordinates) {
        return coordinatesDAO.save(coordinates);
    }

    @Override
    @Transactional
    public Optional<Coordinates> getEntityById(Long id) {
        return coordinatesDAO.findById(id);
    }

    @Override
    @Transactional
    public List<Coordinates> getEntitiesPaginated(int first, int size, String field, String direction) {
        return coordinatesDAO.findWithPagination(first, size, field, direction);
    }

    @Override
    @Transactional
    public Coordinates updateEntity(Coordinates coordinates) {
        if (coordinates.getId() == null) {
            throw new IllegalArgumentException("Coordinates ID cannot be null for update");
        }
        return coordinatesDAO.update(coordinates);
    }

    @Override
    @Transactional
    public long getTotalEntitiesCount() {
        return coordinatesDAO.getTotalCount();
    }

    @Override
    @Transactional
    public boolean deleteEntity(Long id) {
        return coordinatesDAO.delete(id);
    }

    @Transactional
    public List<Coordinates> getAllCoordinates() {
        return coordinatesDAO.findAll();
    }

    @Transactional
    public List<Coordinates> getUnusedCoordinates() {
        return coordinatesDAO.findUnusedCoordinates();
    }

    @Transactional
    public int removeUnusedCoordinates() {
        return coordinatesDAO.deleteUnusedCoordinates();
    }
}