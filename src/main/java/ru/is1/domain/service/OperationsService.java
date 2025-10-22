package ru.is1.domain.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.is1.dal.dao.PersonDAO;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Person;

import java.util.Optional;

@ApplicationScoped
public class OperationsService {

    @Inject
    private PersonDAO personDAO;

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
}
