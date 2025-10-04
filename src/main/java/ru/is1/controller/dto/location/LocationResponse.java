package ru.is1.controller.dto.location;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.Location;

@Getter
@Setter
public class LocationResponse {
    private Long id;
    private Integer x;
    private long y;
    private long z;

    public static LocationResponse fromEntity(Location entity) {
        LocationResponse response = new LocationResponse();
        response.setId(entity.getId());
        response.setX(entity.getX());
        response.setY(entity.getY());
        response.setZ(entity.getZ());
        return response;
    }
}
