package ru.is1.controller.dto.coordinates;


import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.Coordinates;

@Getter
@Setter
public class CoordinatesRequest {
    private Float x;
    private int y;

    public static Coordinates toEntity(CoordinatesRequest request) {
        Coordinates entity = new Coordinates();
        entity.setX(request.getX());
        entity.setY(request.getY());
        return entity;
    }
}
