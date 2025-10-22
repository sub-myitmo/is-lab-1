package ru.is1.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.is1.dal.dao.PersonDAO;
import ru.is1.dal.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonService {

    @Inject
    private PersonDAO personDAO;

    @Inject
    private LocationService locationService;

    @Inject
    private CoordinatesService coordinatesService;


    public Optional<Person> getPersonById(Long id) {
        return personDAO.findById(id);
    }

    public Person createPerson(Person person, Long locationId, Long coordinatesId) {
        checkLocationAndCoordinates(person, locationId, coordinatesId);
        person.setCreationDate(LocalDateTime.now());

        return personDAO.save(person);
    }


    public void updatePerson(Person person, Long locationId, Long coordinatesId) {
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person ID cannot be null for update");
        }

        checkLocationAndCoordinates(person, locationId, coordinatesId);

        Optional<Person> personOptional = getPersonById(person.getId());
        if (personOptional.isPresent()) {
            person.setCreationDate(personOptional.get().getCreationDate());
            personDAO.update(person);
        } else {
            throw new IllegalArgumentException("Person not found with id: " + person.getId());
        }
    }

    private void checkLocationAndCoordinates(Person person, Long locationId, Long coordinatesId) {
        if (locationId != null) {
            Location location = locationService.getLocationById(locationId).orElseThrow(() -> new IllegalArgumentException("Location not found"));
            person.setLocation(location);
        }
        if (coordinatesId != null) {
            Coordinates coordinates = coordinatesService.getCoordinatesById(coordinatesId).orElseThrow(() -> new IllegalArgumentException("Coordinates not found"));
            person.setCoordinates(coordinates);
        }
    }


    public boolean deletePerson(Long id) {
        return personDAO.deletePersonAndAllTransitivelyRelated(id);
    }

    public List<Person> getPersonsPaginated(int first, int size, String field, String direction) {
        return personDAO.findWithPagination(first, size, field, direction);
    }

    public long getTotalPersonCount() {
        return personDAO.getTotalCount();
    }

    public Optional<Long> findByPassportID(String passportID) {
        return personDAO.findByPassportID(passportID);
    }

    public List<Person> searchPersons(int first, int pageSize, String field, String namePattern, String direction) {
        return personDAO.search(first, pageSize, field, namePattern, direction);
    }

}