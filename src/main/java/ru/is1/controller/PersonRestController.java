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

import ru.is1.domain.service.PersonService;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonRestController {

//    @Context
//    private ContainerRequestContext requestContext;

    @Inject
    private PersonService personService;

//    @Inject
//    private AuthService authService;

    // Метод для получения текущего пользователя
//    private User getCurrentUser() {
//        String username = (String) requestContext.getProperty("username");
//        if (username == null) {
//            throw new SecurityException("User not authenticated");
//        }
//
//        return authService.getUserByUsername(username)
//                .orElseThrow(() -> new SecurityException("User not found"));
//    }


    @GET
    public Response getAllPersons(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search,
            @QueryParam("field") @DefaultValue("id") String field,
            @QueryParam("direction") @DefaultValue("asc") String direction) {

        try {
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

    // Special operations endpoints
    @GET
    @Path("/min-passport")
    public Response getPersonWithMinPassportID() {
        try {
            Optional<Person> person = personService.findPersonWithMinPassportID();
            if (person.isPresent()) {
                return Response.ok(PersonResponse.fromEntity(person.get())).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No persons found"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/count/nationality-less-than/{nationality}")
    public Response countNationalityLessThan(@PathParam("nationality") String nationality) {
        try {
            Country nationalityEnum = Country.valueOf(nationality.toUpperCase());
            long count = personService.countPersonsWithNationalityLessThan(nationalityEnum);
            return Response.ok(new CountResponse(count)).build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid nationality: " + nationality +
                    ". Allowed values: " + Arrays.toString(Country.values()));
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/count/nationality-greater-than/{nationality}")
    public Response countNationalityGreaterThan(@PathParam("nationality") String nationality) {
        try {
            Country nationalityEnum = Country.valueOf(nationality.toUpperCase());
            long count = personService.countPersonsWithNationalityGreaterThan(nationalityEnum);
            return Response.ok(new CountResponse(count)).build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid nationality: " + nationality +
                    ". Allowed values: " + Arrays.toString(Country.values()));
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/count/hair-color/{hairColor}")
    public Response countByHairColor(@PathParam("hairColor") String hairColor) {
        try {
            Color colorEnum = Color.valueOf(hairColor.toUpperCase());
            long count = personService.countPersonsWithHairColor(colorEnum);
            return Response.ok(new CountResponse(count)).build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid hair color: " + hairColor +
                    ". Allowed values: " + Arrays.toString(Color.values()));
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/count/eye-color/{eyeColor}")
    public Response countByEyeColor(@PathParam("eyeColor") String eyeColor) {
        try {
            Color colorEnum = Color.valueOf(eyeColor.toUpperCase());
            long count = personService.countPersonsWithEyeColor(colorEnum);
            return Response.ok(new CountResponse(count)).build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid eye color: " + eyeColor +
                    ". Allowed values: " + Arrays.toString(Color.values()));
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}