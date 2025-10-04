package ru.is1.controller.dto.person;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Person;


@Getter
@Setter
public class PersonRequest {
    private String name;
    private String eyeColor;
    private String hairColor;
    private int height;
    private int weight;
    private String passportID;
    private String nationality;

    public static Person toEntity(PersonRequest request) {
        Person entity = new Person();
        entity.setHeight(request.getHeight());
        entity.setName(request.getName());
        entity.setWeight(request.getWeight());
        entity.setPassportID(request.getPassportID());
        entity.setEyeColor(Color.valueOf(request.getEyeColor().toUpperCase()));
        entity.setHairColor(request.getHairColor() == null ? null : Color.valueOf(request.getHairColor().toUpperCase()));
        entity.setNationality(Country.valueOf(request.getNationality().toUpperCase()));
        return entity;
    }
}
