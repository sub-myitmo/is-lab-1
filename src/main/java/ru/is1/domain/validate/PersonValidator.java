package ru.is1.domain.validate;

import lombok.experimental.UtilityClass;
import ru.is1.dal.entity.Person;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PersonValidator {
    public static List<String> validatePersonsData(Person[] personsData) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < personsData.length; i++) {
            Person person = personsData[i];
            try {
                if (person.getName() == null || person.getName().trim().isEmpty()) {
                    errors.add("Запись " + (i + 1) + ": имя не может быть пустым");
                }
                if (person.getCoordinates() == null) {
                    errors.add("Запись " + (i + 1) + ": координаты обязательны");
                }
                if (person.getLocation() == null) {
                    errors.add("Запись " + (i + 1) + ": локация обязательна");
                }
                if (person.getEyeColor() == null) {
                    errors.add("Запись " + (i + 1) + ": цвет глаз обязателен");
                }
                if (person.getNationality() == null) {
                    errors.add("Запись " + (i + 1) + ": национальность обязательна");
                }
                if (person.getPassportID() == null || person.getPassportID().trim().isEmpty()) {
                    errors.add("Запись " + (i + 1) + ": passportID обязателен");
                }
                if (person.getHeight() <= 0) {
                    errors.add("Запись " + (i + 1) + ": рост должен быть больше 0");
                }
                if (person.getWeight() <= 0) {
                    errors.add("Запись " + (i + 1) + ": вес должен быть больше 0");
                }

                if (person.getCoordinates() != null) {
                    if (person.getCoordinates().getX() == null) {
                        errors.add("Запись " + (i + 1) + ": координата X обязательна");
                    }
                }

                if (person.getLocation() != null) {
                    if (person.getLocation().getX() == null) {
                        errors.add("Запись " + (i + 1) + ": X для локации обязателен");
                    }
                }

            } catch (Exception e) {
                errors.add("Запись " + (i + 1) + ": ошибка валидации - " + e.getMessage());
            }
        }
        return errors;
    }
}
