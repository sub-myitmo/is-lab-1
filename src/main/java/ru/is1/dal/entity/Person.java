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

//    private User user;


    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", name='" + name + '\'' +
                ", passportID='" + passportID + '\'' + ", nationality=" + nationality + '}';
    }
}
