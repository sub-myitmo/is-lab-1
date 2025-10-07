package ru.is1.dal.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Coordinates {
    private Long id;

    private Float x;

    private int y;

    private List<Person> persons = new ArrayList<>();
}
