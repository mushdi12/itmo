package com.example.backend.auth;

import com.example.backend.utils.dtos.RespOneParameterDTO;
import com.example.backend.auth.jwt.JwtUtil;
import com.example.backend.auth.jwt.TokensPair;
import com.example.backend.utils.dtos.ErrorDTO;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/auth")
public class AuthController {
    private static final String REFRESH_TOKEN_NAME = "refresh_token";

    @EJB
    private AuthService authService;

    @PATCH
    public Response refresh(@CookieParam(REFRESH_TOKEN_NAME) String oldRefreshToken) {
        if (oldRefreshToken == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = JwtUtil.getUsernameFromToken(oldRefreshToken);
        if (username == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            TokensPair tokensPair = authService.refreshToken(username);
            NewCookie refreshCookie = new NewCookie(REFRESH_TOKEN_NAME,
                    tokensPair.refreshToken(),
                    "/",
                    null,
                    "",
                    60 * 60 * 24 * 14,
                    false,
                    true
            );
            RespOneParameterDTO<String> respDto = new RespOneParameterDTO(tokensPair.accessToken());
            return Response.ok().header("X-Username", username).cookie(refreshCookie).header("Content-Type", "application/json").entity(respDto).build();
        } catch (Exception e) {
            ErrorDTO err = new ErrorDTO("Failed to refresh token");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }

    @DELETE
    public Response logout(@CookieParam(REFRESH_TOKEN_NAME) String oldRefreshToken) {
        if (oldRefreshToken == null) {
            return Response.ok().build();
        }
        try {
            String username = JwtUtil.getUsernameFromToken(oldRefreshToken);
            this.authService.logout(username);
            NewCookie deleteRefreshCookie = new NewCookie(REFRESH_TOKEN_NAME,
                    null,
                    "/",
                    null,
                    "",
                    60 * 60 * 24 * 14,
                    false,
                    true
            );
            return Response.ok().cookie(deleteRefreshCookie).build();
        } catch (Exception e) {
            ErrorDTO err = new ErrorDTO("Failed to delete token");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(JsonNode authForm) {
        String username = authForm.get("username").asText();
        String password = authForm.get("password").asText();
        if (username.isEmpty() || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (this.authService.checkUser(username)){
            ErrorDTO err = new ErrorDTO("Username is already in use");
            return Response.status(Response.Status.CONFLICT).header("Content-Type", "application/json").entity(err).build();
        }
        try {
            TokensPair tokensPair = this.authService.createUser(username, password);
            NewCookie refreshCookie = new NewCookie(REFRESH_TOKEN_NAME,
                    tokensPair.refreshToken(),
                    "/",
                    null,
                    "",
                    60 * 60 * 24 * 14,
                    false,
                    true
            );
            RespOneParameterDTO<String> respDto = new RespOneParameterDTO(tokensPair.accessToken());
            return Response.ok().cookie(refreshCookie).header("Content-Type", "application/json").entity(respDto).build();
        } catch (Exception e) {
            ErrorDTO err = new ErrorDTO("Failed to create new user");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signIn(JsonNode authForm) {
        if (authForm.get("username").isNull() || authForm.get("password").isNull()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String username = authForm.get("username").asText();
        String password = authForm.get("password").asText();
        if (!this.authService.checkUser(username)){
            ErrorDTO err = new ErrorDTO("User doesn't exist");
            return Response.status(Response.Status.CONFLICT).header("Content-Type", "application/json").entity(err).build();
        }
        try {
            TokensPair tokensPair = this.authService.login(username, password);
            NewCookie refreshCookie = new NewCookie(REFRESH_TOKEN_NAME,
                    tokensPair.refreshToken(),
                    "/",
                    null,
                    "",
                    60 * 60 * 24 * 14,
                    false,
                    true
            );
            RespOneParameterDTO<String> respDto = new RespOneParameterDTO(tokensPair.accessToken());
            return Response.ok().cookie(refreshCookie).header("Content-Type", "application/json").entity(respDto).build();
        } catch (Exception e){
            ErrorDTO err = new ErrorDTO("Failed to sign in");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").entity(err).build();
        }
    }
}