package ru.is1.dal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.Identifiable;

import java.time.LocalDateTime;

@Getter
@Setter
public class Person implements Identifiable {
    private Long id;

    private String name;

    private Coordinates coordinates;

    private LocalDateTime creationDate;

    private Color eyeColor;

    private Color hairColor;

    private Location location;

    private int height;

    private int weight;

    private String passportID;

    private Country nationality;

    public String getEyeColor() {
        return eyeColor != null ? eyeColor.name() : null;
    }

    public void setEyeColor(String color) {
        this.eyeColor = color != null ? Color.valueOf(color) : null;
    }

    public String getHairColor() {
        return hairColor != null ? hairColor.name() : null;
    }

    public void setHairColor(String color) {
        this.hairColor = color != null ? Color.valueOf(color) : null;
    }

    public String getNationality() {
        return nationality != null ? nationality.name() : null;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality != null ? Country.valueOf(nationality) : null;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", name='" + name + '\'' +
                ", passportID='" + passportID + '\'' + ", nationality=" + nationality + ", weight=" + weight + ", height=" + height + '}';
    }
}
