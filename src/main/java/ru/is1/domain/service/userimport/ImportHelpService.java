package ru.is1.domain.service.userimport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.is1.dal.dao.CoordinatesDAO;
import ru.is1.dal.dao.ImportDAO;
import ru.is1.dal.dao.LocationDAO;
import ru.is1.dal.dao.PersonDAO;
import ru.is1.dal.entity.Coordinates;
import ru.is1.dal.entity.Location;
import ru.is1.dal.entity.Person;
import ru.is1.dal.entity.UserImport;
import ru.is1.domain.exception.SQLExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportHelpService {

    @Inject
    private ImportDAO importDAO;

    @Inject
    private PersonDAO personDAO;

    @Inject
    private CoordinatesDAO coordinatesDAO;

    @Inject
    private LocationDAO locationDAO;

    @Transactional(rollbackOn = Exception.class)
    public int importPersons(Person[] personsData) {
        int importedCount = 0;
        String errors = "";
        try {
            List<Coordinates> coordinatesList = new ArrayList<>();
            List<Location> locationsList = new ArrayList<>();
            List<Person> personList = new ArrayList<>();
            for (Person personData : personsData) {
                Coordinates coordinates = new Coordinates();
                coordinates.setX(personData.getCoordinates().getX());
                coordinates.setY(personData.getCoordinates().getY());
                coordinatesList.add(coordinates);

                Location location = new Location();
                location.setX(personData.getLocation().getX());
                location.setY(personData.getLocation().getY());
                location.setZ(personData.getLocation().getZ());
                locationsList.add(location);
            }
            if (locationDAO.existsAnyByEntity(locationsList)) {
                errors += "Один из импортируемых location уже существует! ";
            } else {
                locationsList = locationDAO.batchSave(locationsList);
            }

            if (coordinatesDAO.existsAnyByEntity(coordinatesList)) {
                errors += "Один из импортируемых coordinates уже существует! ";
            } else {
                coordinatesList = coordinatesDAO.batchSave(coordinatesList);
            }

            if (!errors.isEmpty()) {
                throw new RuntimeException(errors);
            }

            for (int i = 0; i < personsData.length; i++) {
                Person personData = personsData[i];
                Location location = locationsList.get(i);
                Coordinates coordinates = coordinatesList.get(i);

                Person person = new Person();
                person.setName(personData.getName());
                person.setCoordinates(coordinates);
                person.setLocation(location);
                person.setEyeColor(personData.getEyeColor());
                person.setHairColor(personData.getHairColor());
                person.setHeight(personData.getHeight());
                person.setWeight(personData.getWeight());
                person.setPassportID(personData.getPassportID());
                person.setCreationDate(LocalDateTime.now());
                person.setNationality(personData.getNationality());
                personList.add(person);
                importedCount++;
            }

            if (personDAO.existsAnyByEntity(personList)) {
                errors += "Один из импортируемых person с passportID уже существует! ";
                throw new RuntimeException(errors);
            } else {
                personDAO.batchSave(personList);
            }

        } catch (Exception e) {
            // Извлекаем SQLException из цепочки причин
            Throwable cause = e;
            SQLException sqlEx = null;

            while (cause != null) {
                if (cause instanceof SQLException) {
                    sqlEx = (SQLException) cause;
                    break;
                }
                cause = cause.getCause();
            }

            if (sqlEx != null) {
                throw new RuntimeException(SQLExceptionHandler.getUserFriendlyMessage(sqlEx), e);
            } else {
                // Иначе используем общий обработчик
                throw new RuntimeException(
                        "Ошибка при импорте данных: " +
                                truncateMessage(e.getMessage(), 200),
                        e
                );
            }
        }

        return importedCount;
    }

    private String truncateMessage(String message, int maxLength) {
        if (message == null) return "Неизвестная ошибка";
        if (message.length() <= maxLength) return message;
        return message.substring(0, maxLength) + "...";
    }

    @Transactional
    public UserImport saveImport(UserImport userImport) {
        return importDAO.save(userImport);
    }
}
