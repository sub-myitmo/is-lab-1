package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.is1.config.ws.Broadcaster;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.controller.dto.location.LocationRequest;
import ru.is1.controller.dto.location.LocationResponse;
import ru.is1.controller.dto.location.LocationsWrapper;
import ru.is1.dal.entity.Location;
import ru.is1.domain.service.LocationService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationRestController {

    @Inject
    private LocationService locationService;
    @Inject
    private Broadcaster broadcaster;


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
            Optional<Location> location = locationService.getEntityById(id);
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
            Location created = locationService.createEntity(LocationRequest.toEntity(request));
            broadcaster.broadcast("CREATE", created.getId(), "Location");
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateLocation(@PathParam("id") Long id, LocationRequest request) {
        try {
            Location location = LocationRequest.toEntity(request);
            location.setId(id);
            Location newLocation = locationService.updateEntity(location);
            broadcaster.broadcast("UPDATE", newLocation.getId(), "Location");
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
    public Response getAllLocationsPaginated(
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
                        .entity(new ErrorResponse("Invalid field parameter for sorting. Must be one of: id, x, y, z"))
                        .build();
            }


            List<Location> locations = locationService.getEntitiesPaginated(page * size, size, field, direction);
            long totalCount = locationService.getTotalEntitiesCount();

            List<LocationResponse> locationsList = locations.stream()
                    .map(LocationResponse::fromEntity)
                    .toList();
            LocationsWrapper response = new LocationsWrapper(locationsList, totalCount, page, size);
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }


    @DELETE
    @Path("/{id}")
    public Response deleteLocation(@PathParam("id") Long id) {
        try {
            boolean deleted = locationService.deleteEntity(id);
            if (deleted) {
                broadcaster.broadcast("DELETE", id, "Location");
                return Response.noContent().build();
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


    private boolean isValidSortField(String field) {
        Set<String> validSortFields = Set.of("id", "x", "y", "z");
        return validSortFields.contains(field);
    }

    private boolean isValidDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) || "desc".equalsIgnoreCase(direction);
    }
}
