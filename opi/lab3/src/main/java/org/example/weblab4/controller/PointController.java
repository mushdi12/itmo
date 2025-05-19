package org.example.weblab4.controller;

import org.example.weblab4.entity.Point;
import org.example.weblab4.entity.User;
import org.example.weblab4.service.PointService;
import org.example.weblab4.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private static final Logger logger = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;
    private final UserService userService;

    public PointController(PointService pointService, UserService userService) {
        this.pointService = pointService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> checkPoint(@RequestBody Point point, Authentication authentication) {
        try {
            logger.info("Получен запрос на проверку точки: x={}, y={}, r={}", point.getX(), point.getY(), point.getR());
            
            if (authentication == null) {
                logger.error("Пользователь не аутентифицирован");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
            }

            User user = userService.findByUsername(authentication.getName());
            point.setUserId(user.getId().longValue());

            Point savedPoint = pointService.checkPoint(point);
            logger.info("Точка успешно проверена и сохранена: {}", savedPoint);
            
            return ResponseEntity.ok(savedPoint);
        } catch (Exception e) {
            logger.error("Ошибка при проверке точки: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при проверке точки: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPoints(Authentication authentication) {
        try {
            logger.info("Получен запрос на получение всех точек");
            
            if (authentication == null) {
                logger.error("Пользователь не аутентифицирован");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
            }

            User user = userService.findByUsername(authentication.getName());
            List<Point> points = pointService.getAllPoints(user.getId().longValue());
            
            logger.info("Успешно получены точки для пользователя {}: {} точек", user.getUsername(), points.size());
            return ResponseEntity.ok(points);
        } catch (Exception e) {
            logger.error("Ошибка при получении точек: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении точек: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearPoints(Authentication authentication) {
        try {
            logger.info("Получен запрос на очистку точек");
            
            if (authentication == null) {
                logger.error("Пользователь не аутентифицирован");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
            }

            User user = userService.findByUsername(authentication.getName());
            pointService.clearPoints(user.getId().longValue());
            
            logger.info("Точки успешно очищены для пользователя {}", user.getUsername());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Ошибка при очистке точек: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при очистке точек: " + e.getMessage());
        }
    }
} 