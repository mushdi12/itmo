package backend.auth.jwt;

import com.example.backend.auth.User;
import com.example.backend.auth.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static String token;
    private static final String TEST_USERNAME = "test_user";

    @BeforeAll
    static void setup() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword("dummy".getBytes());
        user.setSalt("salt");

        token = JwtUtil.generateTokensByUser(user).accessToken();
    }

    @Test
    void testGenerateTokensNotNull() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword("dummy".getBytes());
        user.setSalt("salt");

        var pair = JwtUtil.generateTokensByUser(user);
        assertNotNull(pair.accessToken());
        assertNotNull(pair.refreshToken());
    }

    @Test
    void testVerifyValidToken() {
        assertTrue(JwtUtil.verifyToken(token));
    }

    @Test
    void testVerifyInvalidToken() {
        assertFalse(JwtUtil.verifyToken("invalid.token.here"));
    }

    @Test
    void testExtractUsernameFromValidToken() {
        assertEquals(TEST_USERNAME, JwtUtil.getUsernameFromToken(token));
    }

    @Test
    void testExtractUsernameFromInvalidToken() {
        assertNull(JwtUtil.getUsernameFromToken("bad.token.here"));
    }
}