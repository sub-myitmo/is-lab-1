package ru.is1.controller.dto.coordinates;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.Coordinates;

@Getter
@Setter
public class CoordinatesResponse {
    private Long id;
    private Float x;
    private int y;


    public static CoordinatesResponse fromEntity(Coordinates entity) {
        CoordinatesResponse response = new CoordinatesResponse();
        response.setId(entity.getId());
        response.setX(entity.getX());
        response.setY(entity.getY());
        return response;
    }
}
