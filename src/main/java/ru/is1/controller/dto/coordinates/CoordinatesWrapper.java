package ru.is1.controller.dto.coordinates;

import java.util.List;

public class CoordinatesWrapper {
    public List<CoordinatesResponse> coordinates;
    public long totalCount;
    public int currentPage;
    public int pageSize;

    public CoordinatesWrapper(List<CoordinatesResponse> coordinates, long totalCount, int currentPage, int pageSize) {
        this.coordinates = coordinates;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}