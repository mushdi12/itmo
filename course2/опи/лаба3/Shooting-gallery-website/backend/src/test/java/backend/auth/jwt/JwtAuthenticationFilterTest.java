package com.example.backend.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private ContainerRequestContext context;

    private MockedStatic<JwtUtil> jwtUtilMock;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        context = mock(ContainerRequestContext.class);
    }


    @Test
    void filter_abortsRequestWhenTokenIsInvalid() throws IOException {
        when(context.getHeaders()).thenReturn(new MultivaluedHashMap<>() {{
            putSingle("Authorization", "Bearer invalid-token");
        }});

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.verifyToken(anyString())).thenReturn(false);

            filter.filter(context);

            verify(context).abortWith(argThat(response -> response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
        }
    }

    @Test
    void filter_abortsRequestWhenUsernameIsNull() throws IOException {
        when(context.getHeaders()).thenReturn(new MultivaluedHashMap<>() {{
            putSingle("Authorization", "Bearer token-without-username");
        }});

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.verifyToken(anyString())).thenReturn(true);
            jwtUtilMock.when(() -> JwtUtil.getUsernameFromToken(anyString())).thenReturn(null);

            filter.filter(context);

            verify(context).abortWith(argThat(response -> response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
        }
    }

    @Test
    void filter_doesNothingWhenNoAuthorizationHeader() throws IOException {
        when(context.getHeaders()).thenReturn(new MultivaluedHashMap<>());

        filter.filter(context);

        verify(context, never()).abortWith(any());
    }
}