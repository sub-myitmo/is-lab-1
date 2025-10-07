package ru.is1.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.is1.config.aop.MonitorPerformance;
import ru.is1.dal.dao.LocationDAO;
import ru.is1.dal.entity.Location;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@MonitorPerformance
public class LocationService {

    @Inject
    private LocationDAO locationDAO;

    public Location createLocation(Location location) {
        validateLocation(location);
        return locationDAO.save(location);
    }

    public Optional<Location> getLocationById(Long id) {
        return locationDAO.findById(id);
    }

    public List<Location> getAllLocations() {
        return locationDAO.findAll();
    }

    public List<Location> getUnusedLocations() {
        return locationDAO.findUnusedLocations();
    }

    public Location updateLocation(Long id, Location location) {
        validateLocation(location);
        location.setId(id);
        return locationDAO.save(location);
    }

    public boolean deleteLocation(Long id) {
        if (locationDAO.isLocationUsed(id)) {
            throw new IllegalStateException("Cannot delete location used by persons");
        }
        return locationDAO.delete(id);
    }

    public boolean isLocationUsed(Long locationId) {
        return locationDAO.isLocationUsed(locationId);
    }

    private void validateLocation(Location location) {
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        if (location.getX() == null) throw new IllegalArgumentException("Location X cannot be null");
    }
}