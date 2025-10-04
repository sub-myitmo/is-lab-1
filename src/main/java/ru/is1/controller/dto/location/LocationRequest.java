package ru.is1.controller.dto.location;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.Location;

@Getter
@Setter
public class LocationRequest {
    private Integer x;
    private long y;
    private long z;

    public static Location toEntity(LocationRequest request) {
        Location entity = new Location();
        entity.setX(request.getX());
        entity.setY(request.getY());
        entity.setZ(request.getZ());
        return entity;
    }
}
