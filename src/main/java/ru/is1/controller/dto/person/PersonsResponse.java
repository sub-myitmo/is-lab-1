package ru.is1.controller.dto.person;

import java.util.List;

public class PersonsResponse {
    public List<PersonResponse> persons;
    public long totalCount;
    public int currentPage;
    public int pageSize;

    public PersonsResponse(List<PersonResponse> personResponses, long totalCount, int currentPage, int pageSize) {
        this.persons = personResponses;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}
