package org.example.weblab4.controller;

import org.example.weblab4.entity.User;
import org.example.weblab4.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> credentials) {
        logger.info("Получен запрос на регистрацию пользователя: {}", credentials.get("username"));
        try {
            User user = userService.register(credentials.get("username"), credentials.get("password"));
            logger.info("Пользователь успешно зарегистрирован: {}", user.getUsername());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", credentials.get("username"), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        logger.info("Получен запрос на вход пользователя: {}", credentials.get("username"));
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    credentials.get("username"),
                    credentials.get("password")
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Пользователь успешно вошел в систему: {}", credentials.get("username"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Ошибка при входе пользователя {}: {}", credentials.get("username"), e.getMessage());
            return ResponseEntity.badRequest().body("Неверные учетные данные");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Пользователь выходит из системы: {}", username);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
} 