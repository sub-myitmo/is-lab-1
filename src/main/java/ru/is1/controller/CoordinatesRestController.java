package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.is1.controller.dto.coordinates.CoordinatesRequest;
import ru.is1.controller.dto.coordinates.CoordinatesResponse;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.dal.entity.Coordinates;
import ru.is1.domain.service.CoordinatesService;

import java.util.List;
import java.util.Optional;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesRestController {

    @Inject
    private  CoordinatesService coordinatesService;

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
            Optional<Coordinates> coordinates = coordinatesService.getCoordinatesById(id);
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
            Coordinates created = coordinatesService.createCoordinates(CoordinatesRequest.toEntity(request));
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }


//    @GET
//    @Path("/available-coordinates")
//    public Response getAvailableCoordinates() {
//        try {
//            List<Coordinates> coordinates = coordinatesService.getAllCoordinates();
//            List<CoordinatesResponse> response = coordinates.stream()
//                    .map(CoordinatesResponse::fromEntity)
//                    .toList();
//            return Response.ok(response).build();
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(new ErrorResponse(e.getMessage()))
//                    .build();
//        }
//    }
}
