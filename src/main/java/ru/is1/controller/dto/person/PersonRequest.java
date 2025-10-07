package ru.is1.controller.dto.person;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Person;

import jakarta.validation.constraints.*;
import java.util.Objects;

@Getter
@Setter
public class PersonRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    @NotBlank(message = "Eye color cannot be blank")
    @Pattern(regexp = "^(GREEN|RED|YELLOW|ORANGE|WHITE)$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Eye color must be one of: GREEN, RED, YELLOW, ORANGE, WHITE")
    private String eyeColor;

    @Pattern(regexp = "^(GREEN|RED|YELLOW|ORANGE|WHITE)?$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Hair color must be one of: GREEN, RED, YELLOW, ORANGE, WHITE or empty")
    private String hairColor;

    @Positive(message = "Height must be positive")
    private int height;

    @Positive(message = "Weight must be positive")
    private int weight;

    @NotBlank(message = "Passport ID cannot be blank")
    private String passportID;

    @NotBlank(message = "Nationality cannot be blank")
    @Pattern(regexp = "^(RUSSIA|SPAIN|INDIA)$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Nationality must be one of: RUSSIA, SPAIN, INDIA")
    private String nationality;

    public static Person toEntity(PersonRequest request) {
        Person entity = new Person();
        entity.setHeight(request.getHeight());
        entity.setName(request.getName());
        entity.setWeight(request.getWeight());
        entity.setPassportID(request.getPassportID());
        entity.setEyeColor(Color.valueOf(request.getEyeColor().toUpperCase()));

        if (request.getHairColor() != null && !request.getHairColor().trim().isEmpty()) {
            entity.setHairColor(Color.valueOf(request.getHairColor().toUpperCase()));
        } else {
            entity.setHairColor(null);
        }

        entity.setNationality(Country.valueOf(request.getNationality().toUpperCase()));
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonRequest that = (PersonRequest) o;
        return height == that.height &&
                weight == that.weight &&
                Objects.equals(name, that.name) &&
                Objects.equals(eyeColor, that.eyeColor) &&
                Objects.equals(hairColor, that.hairColor) &&
                Objects.equals(passportID, that.passportID) &&
                Objects.equals(nationality, that.nationality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, eyeColor, hairColor, height, weight, passportID, nationality);
    }

    @Override
    public String toString() {
        return "PersonRequest{" +
                "name='" + name + '\'' +
                ", eyeColor='" + eyeColor + '\'' +
                ", hairColor='" + hairColor + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", passportID='" + passportID + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }
}