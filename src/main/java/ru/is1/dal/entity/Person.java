package ru.is1.dal.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_color", nullable = false)
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color")
    private Color hairColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private int height;

    @Column(nullable = false)
    private int weight;

    @Column(name = "passport_id", nullable = false, unique = true)
    private String passportID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country nationality;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;


    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", name='" + name + '\'' +
                ", passportID='" + passportID + '\'' + ", nationality=" + nationality + '}';
    }
}
