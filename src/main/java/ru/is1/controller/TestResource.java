package ru.is1.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/ping")
public class TestResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "pong";
    }
}
