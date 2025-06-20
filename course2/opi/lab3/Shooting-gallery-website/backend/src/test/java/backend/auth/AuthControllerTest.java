package com.example.backend.auth;

import com.example.backend.auth.jwt.TokensPair;
import com.example.backend.utils.dtos.ErrorDTO;
import com.example.backend.utils.dtos.RespOneParameterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController controller;
    private AuthService authService;

    private final String REFRESH_TOKEN_NAME = "refresh_token";



    @BeforeEach
    void setup() throws Exception {
        controller = new AuthController();
        authService = mock(AuthService.class);

        Field field = AuthController.class.getDeclaredField("authService");
        field.setAccessible(true);
        field.set(controller, authService);
    }




    @Test
    void testRefreshNoToken() {
        Response response = controller.refresh(null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testRefreshInvalidToken() {
        mockStaticJwtUtil(null);

        Response response = controller.refresh("invalidToken");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }




    @Test
    void testLogoutNoToken() {
        Response response = controller.logout(null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode authForm = mapper.createObjectNode();
        authForm.put("username", "newUser");
        authForm.put("password", "pass123");

        when(authService.checkUser("newUser")).thenReturn(false);
        TokensPair tokensPair = new TokensPair("accessToken", "refreshToken");
        when(authService.createUser("newUser", "pass123")).thenReturn(tokensPair);

        Response response = controller.register(authForm);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        NewCookie cookie = response.getCookies().get(REFRESH_TOKEN_NAME);
        assertEquals("refreshToken", cookie.getValue());
    }


    @Test
    void testSignInSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode authForm = mapper.createObjectNode();
        authForm.put("username", "user1");
        authForm.put("password", "pass");

        when(authService.checkUser("user1")).thenReturn(true);
        TokensPair tokensPair = new TokensPair("accessToken", "refreshToken");
        when(authService.login("user1", "pass")).thenReturn(tokensPair);

        Response response = controller.signIn(authForm);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        NewCookie cookie = response.getCookies().get(REFRESH_TOKEN_NAME);
        assertEquals("refreshToken", cookie.getValue());
    }


    private void mockStaticJwtUtil(String returnUsername) {
        try (var mockedStatic = Mockito.mockStatic(com.example.backend.auth.jwt.JwtUtil.class)) {
            mockedStatic.when(() -> com.example.backend.auth.jwt.JwtUtil.getUsernameFromToken(anyString()))
                    .thenReturn(returnUsername);
        }
    }
}