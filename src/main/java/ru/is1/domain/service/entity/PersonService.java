package ru.is1.domain.service.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.is1.dal.dao.PersonDAO;
import ru.is1.dal.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonService implements BaseService<Person> {

    @Inject
    private PersonDAO personDAO;

    @Inject
    private LocationService locationService;

    @Inject
    private CoordinatesService coordinatesService;


    @Override
    @Transactional
    public Optional<Person> getEntityById(Long id) {
        return personDAO.findById(id);
    }

    @Override
    @Transactional
    public boolean deleteEntity(Long id) {
        return personDAO.delete(id);
    }

    @Override
    @Transactional
    public Person updateEntity(Person person) {
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person ID cannot be null for update");
        }

        Optional<Person> personOptional = getEntityById(person.getId());
        if (personOptional.isPresent()) {
            person.setCreationDate(personOptional.get().getCreationDate());
            return personDAO.update(person);
        } else {
            throw new IllegalArgumentException("Person not found with id: " + person.getId());
        }
    }

    @Override
    @Transactional
    public long getTotalEntitiesCount() {
        return personDAO.getTotalCount();
    }

    @Override
    @Transactional
    public Person createEntity(Person person) {
        person.setCreationDate(LocalDateTime.now());
        return personDAO.update(person);
    }

    @Override
    @Transactional
    public List<Person> getEntitiesPaginated(int first, int size, String field, String direction) {
        return personDAO.findWithPagination(first, size, field, direction);
    }


    @Transactional
    public void checkLocationAndCoordinates(Person person, Long locationId, Long coordinatesId) {
        if (locationId != null) {
            Location location = locationService.getEntityById(locationId).orElseThrow(() -> new IllegalArgumentException("Location not found"));
            person.setLocation(location);
        }
        if (coordinatesId != null) {
            Coordinates coordinates = coordinatesService.getEntityById(coordinatesId).orElseThrow(() -> new IllegalArgumentException("Coordinates not found"));
            person.setCoordinates(coordinates);
        }
    }

    @Transactional
    public Optional<Long> findByPassportID(String passportID) {
        return personDAO.findByPassportID(passportID);
    }

    @Transactional
    public List<Person> searchPersons(int first, int pageSize, String field, String namePattern, String direction) {
        return personDAO.search(first, pageSize, field, namePattern, direction);
    }

    // special
    @Transactional
    public Optional<Person> findPersonWithMinPassportID() {
        return personDAO.findMinPassportID();
    }

    @Transactional
    public long countPersonsWithNationalityLessThan(Country nationality) {
        return personDAO.countByNationalityLessThan(nationality);
    }

    @Transactional
    public long countPersonsWithNationalityGreaterThan(Country nationality) {
        return personDAO.countByNationalityGreaterThan(nationality);
    }

    @Transactional
    public long countPersonsWithHairColor(Color hairColor) {
        return personDAO.countByHairColor(hairColor);
    }

    @Transactional
    public long countPersonsWithEyeColor(Color eyeColor) {
        return personDAO.countByEyeColor(eyeColor);
    }
}