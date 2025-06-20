import com.example.backend.auth.AuthService;

import com.example.backend.auth.User;

import com.example.backend.auth.jwt.JwtUtil;
import com.example.backend.auth.jwt.TokensPair;
import com.example.backend.auth.mongodb.UserMongodbManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private UserMongodbManager mockDbManager;

    @BeforeEach
    void setup() {
        mockDbManager = mock(UserMongodbManager.class);
        authService = new AuthService();
        authService.setDbManager(mockDbManager);
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        when(mockDbManager.getUserByUsername("newUser")).thenReturn(null);

        TokensPair tokens = new TokensPair("access-token", "refresh-token");
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.generateTokensByUser(any())).thenReturn(tokens);

            TokensPair result = authService.createUser("newUser", "password123");

            assertNotNull(result);
            assertEquals("access-token", result.accessToken());
            assertEquals("refresh-token", result.refreshToken());

            verify(mockDbManager).addUser(any(User.class));
        }
    }

    @Test
    void testCreateUserAlreadyExists() throws Exception {
        when(mockDbManager.getUserByUsername("existingUser")).thenReturn(new User());

        TokensPair result = authService.createUser("existingUser", "pass");

        assertNull(result);

        verify(mockDbManager, never()).addUser(any());
    }

    @Test
    void testCheckUserExists() throws Exception {
        when(mockDbManager.getUserByUsername("user")).thenReturn(new User());

        boolean exists = authService.checkUser("user");
        assertTrue(exists);
    }


    @Test
    void testDeleteUserSuccess() throws Exception {
        User user = new User();
        when(mockDbManager.getUserByUsername("user")).thenReturn(user);
        when(mockDbManager.deleteUser(user)).thenReturn(true);

        boolean deleted = authService.deleteUser("user");
        assertTrue(deleted);

        verify(mockDbManager).deleteUser(user);
    }

    @Test
    void testDeleteUserFailure() throws Exception {
        User user = new User();
        when(mockDbManager.getUserByUsername("user")).thenReturn(user);
        when(mockDbManager.deleteUser(user)).thenReturn(false);

        boolean deleted = authService.deleteUser("user");
        assertFalse(deleted);
    }

    @Test
    void testRefreshToken() throws Exception, NoSuchAlgorithmException {
        User user = new User();
        when(mockDbManager.getUserByUsername("user")).thenReturn(user);

        TokensPair tokens = new TokensPair("new-access", "new-refresh");
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.generateTokensByUser(user)).thenReturn(tokens);

            TokensPair result = authService.refreshToken("user");
            assertEquals("new-access", result.accessToken());
            assertEquals("new-refresh", result.refreshToken());

            verify(mockDbManager).updateUser(user);
            assertEquals("new-refresh", user.getRefreshToken());
        }
    }

    @Test
    void testLogout() throws Exception {
        User user = new User();
        when(mockDbManager.getUserByUsername("user")).thenReturn(user);

        authService.logout("user");

        assertNull(user.getRefreshToken());
        verify(mockDbManager).updateUser(user);
    }
}