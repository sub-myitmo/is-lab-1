package ru.is1.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.is1.config.DataWebSocket;
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
        validatePerson(person);

        if (locationId != null) {
            Location location = locationService.getLocationById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Location not found"));
            person.setLocation(location);
        }

        if (coordinatesId != null) {
            Coordinates coordinates = coordinatesService.getCoordinatesById(coordinatesId)
                    .orElseThrow(() -> new IllegalArgumentException("Coordinates not found"));
            person.setCoordinates(coordinates);
        }

        person.setCreationDate(LocalDateTime.now());

        Person created = personDAO.save(person); // транзакция внутри DAO

        // Отправка уведомления через WebSocket с текущими параметрами
        DataWebSocket.broadcastPersonChange("CREATED", created.getId());

        return created;
    }

    public void updatePerson(Person person, Long locationId, Long coordinatesId) {
        validatePerson(person);
        if (person.getId() == null) {
            throw new IllegalArgumentException("Person ID cannot be null for update");
        }

        if (locationId != null) {
            Location location = locationService.getLocationById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Location not found"));
            person.setLocation(location);
        }

        if (coordinatesId != null) {
            Coordinates coordinates = coordinatesService.getCoordinatesById(coordinatesId)
                    .orElseThrow(() -> new IllegalArgumentException("Coordinates not found"));
            person.setCoordinates(coordinates);
        }

        person.setCreationDate(getPersonById(person.getId()).get().getCreationDate());

        Person updated = personDAO.update(person);

        // Отправка уведомления через WebSocket с текущими параметрами
        DataWebSocket.broadcastPersonChange("UPDATED", updated.getId());
    }

    public boolean deletePerson(Long id) {
        if (personDAO.deletePersonAndAllTransitivelyRelated(id)) {
            // Отправка уведомления через WebSocket с текущими параметрами
            DataWebSocket.broadcastPersonChange("DELETED", id);
            return true;
        }
        return false;
    }

    public List<Person> getAllPersons() {
        return personDAO.findAll();
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

    // Специальные операции
    public Optional<Person> findPersonWithMinPassportID() {
        return personDAO.findMinPassportID();
    }

    public long countPersonsWithNationalityLessThan(Country nationality) {
        return personDAO.countByNationalityLessThan(nationality);
    }

    public long countPersonsWithNationalityGreaterThan(Country nationality) {
        return personDAO.countByNationalityGreaterThan(nationality);
    }

    public long countPersonsWithHairColor(Color hairColor) {
        return personDAO.countByHairColor(hairColor);
    }

    public long countPersonsWithEyeColor(Color eyeColor) {
        return personDAO.countByEyeColor(eyeColor);
    }

    public List<Person> searchPersons(int first, int pageSize, String field, String namePattern, String direction) {
        return personDAO.search(first, pageSize, field, namePattern, direction);
    }

    private void validatePerson(Person person) {
        if (person == null) throw new IllegalArgumentException("Person cannot be null");
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (person.getHeight() <= 0) throw new IllegalArgumentException("Height must be > 0");
        if (person.getWeight() <= 0) throw new IllegalArgumentException("Weight must be > 0");
    }
}