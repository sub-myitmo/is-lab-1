package ru.is1.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.is1.controller.dto.error.ErrorResponse;
import ru.is1.controller.dto.person.CountResponse;
import ru.is1.controller.dto.person.PersonResponse;
import ru.is1.dal.entity.Color;
import ru.is1.dal.entity.Country;
import ru.is1.dal.entity.Person;
import ru.is1.domain.service.OperationsService;
import ru.is1.domain.service.PersonService;

import java.util.Arrays;
import java.util.Optional;

@Path("/operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationsRestController {

    @Inject
    private OperationsService operationsService;

    @GET
    @Path("/min-passport")
    public Response getPersonWithMinPassportID() {
        try {
            Optional<Person> person = operationsService.findPersonWithMinPassportID();
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
            long count = operationsService.countPersonsWithNationalityLessThan(nationalityEnum);
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
            long count = operationsService.countPersonsWithNationalityGreaterThan(nationalityEnum);
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
            long count = operationsService.countPersonsWithHairColor(colorEnum);
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
            long count = operationsService.countPersonsWithEyeColor(colorEnum);
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
