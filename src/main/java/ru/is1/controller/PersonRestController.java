package ru.is1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import ru.is1.controller.dto.person.CountResponse;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.controller.dto.person.PersonRequest;
import ru.is1.controller.dto.person.PersonResponse;
import ru.is1.controller.dto.person.PersonsResponse;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Person;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.is1.domain.service.PersonService;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonRestController {
    @Inject
    private PersonService personService;

    @GET
    public Response getAllPersons(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search,
            @QueryParam("field") @DefaultValue("id") String field,
            @QueryParam("direction") @DefaultValue("asc") String direction) {

        try {
            if (!isValidDirection(direction)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid direction parameter. Must be 'asc' or 'desc'"))
                        .build();
            }

            // Валидация параметра field в зависимости от наличия search
            if (search != null && !search.trim().isEmpty()) {
                if (!isValidSearchField(field)) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Invalid field parameter for search. Must be one of: name, passportID, nationality, eyeColor, hairColor"))
                            .build();
                }
            } else {
                if (!isValidSortField(field)) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Invalid field parameter for sorting. Must be one of: id, name, passportID, nationality, eyeColor, hairColor, height, weight, creationDate"))
                            .build();
                }
            }

            List<Person> persons;
            if (search != null && !search.trim().isEmpty()) {
                persons = personService.searchPersons(page * size, size, field, search, direction);
            } else {
                persons = personService.getPersonsPaginated(page * size, size, field, direction);
            }

            long totalCount = personService.getTotalPersonCount();

            List<PersonResponse> personsList = persons.stream()
                    .map(PersonResponse::fromEntity)
                    .toList();
            PersonsResponse response = new PersonsResponse(personsList, totalCount, page, size);
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getPersonById(@PathParam("id") Long id) {
        try {
            Optional<Person> person = personService.getPersonById(id);
            if (person.isPresent()) {
                return Response.ok(PersonResponse.fromEntity(person.get())).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Person not found with id: " + id))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updatePerson(@PathParam("id") Long id, @QueryParam("locationId") Long locationId,
                                 @QueryParam("coordinatesId") Long coordinatesId, PersonRequest request) {
        try {
            Person person = PersonRequest.toEntity(request);
            person.setId(id);
            personService.updatePerson(person, locationId, coordinatesId);
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

    @DELETE
    @Path("/{id}")
    public Response deletePerson(@PathParam("id") Long id) {
        try {
            boolean deleted = personService.deletePerson(id);
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Person not found with id: " + id))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/with-existing")
    public Response createPersonWithExisting(
            @QueryParam("locationId") Long locationId,
            @QueryParam("coordinatesId") Long coordinatesId,
            Person person) {

        try {
            Person created = personService.createPerson(person, locationId, coordinatesId);
            return Response.status(Response.Status.CREATED).entity(PersonResponse.fromEntity(created)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/check/{passportID}")
    public Response checkPassportID(@PathParam("passportID") String passportID) {
        try {
            Optional<Long> result = personService.findByPassportID(passportID);
            if (result.isEmpty()) {
                return Response.ok(new CountResponse(0)).build();
            } else {
                return Response.ok(new CountResponse(result.get())).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }


    private boolean isValidDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) || "desc".equalsIgnoreCase(direction);
    }

    private boolean isValidSearchField(String field) {
        Set<String> validSearchFields = Set.of("name", "passportID", "nationality", "eyeColor", "hairColor");
        return validSearchFields.contains(field);
    }

    private boolean isValidSortField(String field) {
        Set<String> validSortFields = Set.of("id", "name", "passportID", "nationality", "eyeColor", "hairColor", "height", "weight", "creationDate");
        return validSortFields.contains(field);
    }
}