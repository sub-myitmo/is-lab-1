package ru.is1.dal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Coordinates implements Identifiable {
    private Long id;

    private Float x;

    private int y;

    private List<Person> persons = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" + "x=" + x + ", y=" + y + '}';
    }
}
