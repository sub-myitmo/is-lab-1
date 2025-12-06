package ru.is1.controller.dto.imports;

import lombok.Getter;
import lombok.Setter;
import ru.is1.dal.entity.UserImport;

import java.time.LocalDateTime;

@Getter
@Setter
public class ImportResponse {
    private Long id;
    private String status;
    private int count;
    private String errors;
    private LocalDateTime creationDate;

    public static ImportResponse fromEntity(UserImport entity) {
        ImportResponse response = new ImportResponse();
        response.setId(entity.getId());
        response.setCount(entity.getCount());
        response.setStatus(entity.getStatus());
        response.setErrors(entity.getErrors());
        response.setCreationDate(entity.getCreationDate());
        return response;
    }
}