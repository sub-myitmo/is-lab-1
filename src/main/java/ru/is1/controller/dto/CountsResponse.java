package ru.is1.controller.dto;

public class CountsResponse {
    public long persons_count;
    public long locations_count;
    public long coordinates_count;

    public CountsResponse(long persons_count, long locations_count, long coordinates_count) {
        this.persons_count = persons_count;
        this.locations_count = locations_count;
        this.coordinates_count = coordinates_count;
    }
}
