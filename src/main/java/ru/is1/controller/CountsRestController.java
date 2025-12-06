package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.is1.controller.dto.CountsResponse;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.domain.service.entity.CoordinatesService;
import ru.is1.domain.service.entity.LocationService;
import ru.is1.domain.service.entity.PersonService;

@Path("/counts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountsRestController {
    @Inject
    private LocationService locationService;
    @Inject
    private PersonService personService;
    @Inject
    private CoordinatesService coordinatesService;

    @GET
    public Response getAllCounts() {
        try {
            long personsCount = personService.getTotalEntitiesCount();
            long locationsCount = locationService.getTotalEntitiesCount();
            long coordinatesCount = coordinatesService.getTotalEntitiesCount();
            CountsResponse response = new CountsResponse(personsCount, locationsCount, coordinatesCount);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
