package com.example.backend.auth.jwt;

import com.auth0.jwt.JWT;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.auth.User;
import com.example.backend.utils.exceptions.GenerateTokensException;
import jakarta.ejb.Singleton;

import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

@Singleton
public class JwtUtil {
    private static final String SECRET;

    static {
        try (InputStreamReader reader = new InputStreamReader(JwtUtil.class.getClassLoader().getResourceAsStream("config.properties"))) {
            Properties props = new Properties();
            props.load(reader);
            SECRET = props.getProperty("SECRET_JWT_KEY");
        } catch (Exception e){
            throw new RuntimeException("Error loading properties file");
        }
    }

    public static TokensPair generateTokensByUser(User user){
        String accessToken = JwtUtil.getAccessToken(user.getUsername());
        String refreshToken = JwtUtil.getRefreshToken(user.getUsername());
        if (accessToken == null || refreshToken == null) {
            throw new GenerateTokensException("Failed to generate JWT tokens");
        }
        return new TokensPair(accessToken, refreshToken);
    }

    private static String getAccessToken(String username){
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(SECRET));
    }

    private static String getRefreshToken(String username){
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(14, ChronoUnit.DAYS))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static boolean verifyToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException w) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token){
        if (JwtUtil.verifyToken(token)) {
            return JWT.decode(token).getSubject();
        }
        return null;
    }
}


