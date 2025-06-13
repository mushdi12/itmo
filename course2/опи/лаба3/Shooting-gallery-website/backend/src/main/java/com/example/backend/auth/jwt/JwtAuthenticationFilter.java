package com.example.backend.auth.jwt;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Authenticating
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String headerText = containerRequestContext.getHeaders().getFirst("Authorization");
        if (headerText != null && headerText.startsWith("Bearer ")) {
            String token = headerText.substring(7);
            if (token != null && JwtUtil.verifyToken(token)) {
                String username = JwtUtil.getUsernameFromToken(token);
                if (username == null) {
                    containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                } else {
                    containerRequestContext.getHeaders().putSingle("Authorization", token);
                }
            } else {
                containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}
