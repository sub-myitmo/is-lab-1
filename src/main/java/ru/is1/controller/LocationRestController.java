package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.controller.dto.location.LocationRequest;
import ru.is1.controller.dto.location.LocationResponse;
import ru.is1.dal.entity.Location;
import ru.is1.domain.service.LocationService;

import java.util.List;
import java.util.Optional;

@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationRestController {

    @Inject
    private LocationService locationService;


    @GET
    public Response getAllLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            List<LocationResponse> response = locations.stream()
                    .map(LocationResponse::fromEntity)
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
    public Response getLocationById(@PathParam("id") Long id) {
        try {
            Optional<Location> location = locationService.getLocationById(id);
            if (location.isPresent()) {
                return Response.ok(LocationResponse.fromEntity(location.get())).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Location not found with id: " + id))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    public Response createLocation(LocationRequest request) {
        try {
            Location created = locationService.createLocation(LocationRequest.toEntity(request));
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
