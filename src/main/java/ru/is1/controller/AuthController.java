//package ru.is1.controller;
//
//
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import ru.is1.config.utils.JwtUtil;
//import ru.is1.controller.dto.auth.AuthResponse;
//import ru.is1.controller.dto.auth.LoginRequest;
//import ru.is1.controller.dto.auth.RegisterRequest;
//import ru.is1.controller.dto.auth.UserProfileResponse;
//import ru.is1.dal.entity.User;
//import ru.is1.domain.service.AuthService;
//
//@Path("/auth")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//public class AuthController {
//    @Inject
//    private AuthService authService;
//
//    @POST
//    @Path("/register")
//    public Response register(RegisterRequest request) {
//        try {
//            AuthResponse response = authService.register(request);
//            if (response.getToken() != null) {
//                return Response.ok(response).build();
//            } else {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(response)
//                        .build();
//            }
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(new AuthResponse("Registration failed: " + e.getMessage()))
//                    .build();
//        }
//    }
//
//    @POST
//    @Path("/login")
//    public Response login(LoginRequest request) {
//        try {
//            AuthResponse response = authService.login(request);
//            if (response.getToken() != null) {
//                return Response.ok(response).build();
//            } else {
//                return Response.status(Response.Status.UNAUTHORIZED)
//                        .entity(response)
//                        .build();
//            }
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(new AuthResponse("Login failed: " + e.getMessage()))
//                    .build();
//        }
//    }
//
//    @GET
//    @Path("/verify")
//    public Response verifyToken(@HeaderParam("Authorization") String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return Response.status(Response.Status.UNAUTHORIZED)
//                    .entity(new AuthResponse("Missing or invalid token"))
//                    .build();
//        }
//
//        String token = authHeader.substring(7);
//        String username = JwtUtil.validateToken(token);
//
//        if (username != null) {
//            return Response.ok(new AuthResponse("Token is valid")).build();
//        } else {
//            return Response.status(Response.Status.UNAUTHORIZED)
//                    .entity(new AuthResponse("Invalid token"))
//                    .build();
//        }
//    }
//
//    @GET
//    @Path("/profile")
//    public Response getProfile(@HeaderParam("Authorization") String authHeader) {
//        try {
//            User user = authService.getUserByUsername(JwtUtil.validateToken(authHeader.substring(7)))
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            UserProfileResponse profile = new UserProfileResponse(
//                    user.getUsername(),
//                    user.getEmail()
//            );
//
//            return Response.ok(profile).build();
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(new AuthResponse("Error loading profile: " + e.getMessage()))
//                    .build();
//        }
//    }
//}
