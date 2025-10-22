package ru.is1.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.is1.dal.dao.LocationDAO;
import ru.is1.dal.entity.Location;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationService {

    @Inject
    private LocationDAO locationDAO;

    public Location createLocation(Location location) {
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
        location.setId(id);
        return locationDAO.save(location);
    }

    public boolean deleteLocation(Long id) {
        return locationDAO.delete(id);
    }
}