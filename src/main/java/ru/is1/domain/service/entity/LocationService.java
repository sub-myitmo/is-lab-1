package ru.is1.domain.service.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.is1.dal.dao.LocationDAO;
import ru.is1.dal.entity.Location;
import ru.is1.domain.service.BaseService;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationService implements BaseService<Location> {

    @Inject
    private LocationDAO locationDAO;

    @Override
    @Transactional
    public Location createEntity(Location location) {
        return locationDAO.save(location);
    }

    @Override
    @Transactional
    public List<Location> getEntitiesPaginated(int first, int size, String field, String direction) {
        return locationDAO.findWithPagination(first, size, field, direction);
    }

    @Override
    @Transactional
    public Location updateEntity(Location location) {
        if (location.getId() == null) {
            throw new IllegalArgumentException("Location ID cannot be null for update");
        }
        if (locationDAO.existsAnyByLocation(List.of(location))) {
            throw new IllegalArgumentException("Location with this fields already exists");
        }
        return locationDAO.update(location);
    }

    @Override
    @Transactional
    public long getTotalEntitiesCount() {
        return locationDAO.getTotalCount();
    }

    @Override
    @Transactional
    public Optional<Location> getEntityById(Long id) {
        return locationDAO.findById(id);
    }

    @Override
    @Transactional
    public boolean deleteEntity(Long id) {
        return locationDAO.delete(id);
    }

    @Transactional
    public List<Location> getAllLocations() {
        return locationDAO.findAll();
    }

    @Transactional
    public List<Location> getUnusedLocations() {
        return locationDAO.findUnusedLocations();
    }

    @Transactional
    public int removeUnusedLocations() {
        return locationDAO.deleteUnusedLocations();
    }
}