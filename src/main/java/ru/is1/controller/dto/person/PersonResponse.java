package ru.is1.controller.dto.person;

import lombok.Getter;
import lombok.Setter;
import ru.is1.controller.dto.coordinates.CoordinatesResponse;
import ru.is1.controller.dto.location.LocationResponse;
import ru.is1.dal.entity.Person;

import java.time.LocalDateTime;


@Getter
@Setter
public class PersonResponse {
    private Long id;
    private String name;
    private String eyeColor;
    private String hairColor;
    private int height;
    private int weight;
    private String passportID;
    private String nationality;
    private LocationResponse location;
    private CoordinatesResponse coordinates;
    private LocalDateTime creationDate;
//    private Long userId;

    public static PersonResponse fromEntity(Person entity) {
        PersonResponse response = new PersonResponse();
        response.setId(entity.getId());
        response.setHeight(entity.getHeight());
        response.setName(entity.getName());
        response.setWeight(entity.getWeight());
        response.setPassportID(entity.getPassportID());
        response.setEyeColor(entity.getEyeColor());
        response.setHairColor(entity.getHairColor() != null ? entity.getHairColor() : null);
        response.setNationality(entity.getNationality());
        response.setLocation(LocationResponse.fromEntity(entity.getLocation()));
        response.setCoordinates(CoordinatesResponse.fromEntity(entity.getCoordinates()));
        response.setCreationDate(entity.getCreationDate());
//        response.setUserId(entity.getUser().getId());
        return response;
    }
}
