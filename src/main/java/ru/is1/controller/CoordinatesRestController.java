package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.is1.config.ws.Broadcaster;
import ru.is1.controller.dto.coordinates.CoordinatesRequest;
import ru.is1.controller.dto.coordinates.CoordinatesResponse;
import ru.is1.controller.dto.coordinates.CoordinatesWrapper;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.dal.entity.Coordinates;
import ru.is1.domain.service.CoordinatesService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesRestController {

    @Inject
    private CoordinatesService coordinatesService;
    @Inject
    private Broadcaster broadcaster;

    @GET
    public Response getAllCoordinates() {
        try {
            List<Coordinates> coordinatesList = coordinatesService.getAllCoordinates();
            List<CoordinatesResponse> response = coordinatesList.stream()
                    .map(CoordinatesResponse::fromEntity)
                    .toList();
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCoordinatesById(@PathParam("id") Long id) {
        try {
            Optional<Coordinates> coordinates = coordinatesService.getEntityById(id);
            if (coordinates.isPresent()) {
                return Response.ok(CoordinatesResponse.fromEntity(coordinates.get())).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Coordinates not found with id: " + id))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    public Response createCoordinates(CoordinatesRequest request) {
        try {
            Coordinates created = coordinatesService.createEntity(CoordinatesRequest.toEntity(request));
            broadcaster.broadcast("CREATE", created.getId(), "Coordinates");
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateCoordinates(@PathParam("id") Long id, CoordinatesRequest request) {
        try {
            Coordinates coordinates = CoordinatesRequest.toEntity(request);
            coordinates.setId(id);
            Coordinates newCoordinates = coordinatesService.updateEntity(coordinates);
            broadcaster.broadcast("UPDATE", newCoordinates.getId(), "Coordinates");
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
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
                        .entity(new ErrorResponse("Invalid field parameter for sorting. Must be one of: id, x, y"))
                        .build();
            }

            List<Coordinates> coordinates = coordinatesService.getEntitiesPaginated(page * size, size, field, direction);
            long totalCount = coordinatesService.getTotalEntitiesCount();

            List<CoordinatesResponse> coordinatesList = coordinates.stream()
                    .map(CoordinatesResponse::fromEntity)
                    .toList();
            CoordinatesWrapper response = new CoordinatesWrapper(coordinatesList, totalCount, page, size);
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }


    @DELETE
    @Path("/{id}")
    public Response deleteCoordinates(@PathParam("id") Long id) {
        try {
            boolean deleted = coordinatesService.deleteEntity(id);
            if (deleted) {
                broadcaster.broadcast("DELETE", id, "Coordinates");
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Coordinates not found with id: " + id))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }


    private boolean isValidSortField(String field) {
        Set<String> validSortFields = Set.of("id", "x", "y");
        return validSortFields.contains(field);
    }

    private boolean isValidDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) || "desc".equalsIgnoreCase(direction);
    }
}
