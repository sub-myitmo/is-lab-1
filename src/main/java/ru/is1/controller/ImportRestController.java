package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import ru.is1.config.ws.Broadcaster;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.controller.dto.imports.ImportResponse;
import ru.is1.controller.dto.imports.ImportsWrapper;
import ru.is1.dal.entity.ImportStatus;
import ru.is1.dal.entity.UserImport;
import ru.is1.domain.service.ImportService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
public class ImportRestController {

    @Inject
    private ImportService importService;
    @Inject
    private Broadcaster broadcaster;

    @GET
    public Response getAllImports() {
        try {
            List<UserImport> importsList = importService.getAllImports();
            List<ImportResponse> response = importsList.stream()
                    .map(ImportResponse::fromEntity)
                    .toList();
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/pagination")
    public Response getAllCoordinatesPaginated(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("field") @DefaultValue("id") String field,
            @QueryParam("direction") @DefaultValue("asc") String direction) {

        try {
            if (!isValidDirection(direction)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid direction parameter. Must be 'asc' or 'desc'"))
                        .build();
            }

            if (!isValidSortField(field)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid field parameter for sorting. Must be one of: id, status, count, errors"))
                        .build();
            }

            List<UserImport> userImports = importService.getEntitiesPaginated(page * size, size, field, direction);
            long totalCount = importService.getTotalEntitiesCount();

            List<ImportResponse> importResponseList = userImports.stream()
                    .map(ImportResponse::fromEntity)
                    .toList();
            ImportsWrapper response = new ImportsWrapper(importResponseList, totalCount, page, size);
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importJsonData(MultipartFormDataInput input) {
        try {
            var filePart = input.getFormDataMap().get("file");
            if (filePart == null || filePart.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Файл не предоставлен"))
                        .build();
            }

            InputPart inputPart = filePart.get(0);
            String fileName = inputPart.getHeaders().getFirst("Content-Disposition")
                    .split("filename=")[1].replace("\"", "");

            if (!fileName.toLowerCase().endsWith(".json")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Файл должен быть в формате JSON"))
                        .build();
            }

            InputStream fileInputStream = inputPart.getBody(InputStream.class, null);
            UserImport userImport = importService.importFromJson(fileInputStream);


            if (Objects.equals(userImport.getStatus(), ImportStatus.ERROR.name())) {
                broadcaster.broadcast("FAILURE_CREATED", userImport.getId(), "Import");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ImportResponse.fromEntity(userImport)).build();
            }

            broadcaster.broadcast("CREATED", userImport.getId(), "Import");
            return Response.ok(ImportResponse.fromEntity(userImport)).build();

        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Неожиданная ошибка: " + e.getMessage()))
                    .build();
        }
    }

    private boolean isValidSortField(String field) {
        Set<String> validSortFields = Set.of("id", "status", "count", "errors");
        return validSortFields.contains(field);
    }

    private boolean isValidDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) || "desc".equalsIgnoreCase(direction);
    }
}