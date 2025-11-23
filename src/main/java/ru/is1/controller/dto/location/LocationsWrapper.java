package ru.is1.controller.dto.location;


import java.util.List;

public class LocationsWrapper {
    public List<LocationResponse> locations;
    public long totalCount;
    public int currentPage;
    public int pageSize;

    public LocationsWrapper(List<LocationResponse> locationResponses, long totalCount, int currentPage, int pageSize) {
        this.locations = locationResponses;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}