package org.example.weblab4.service;

import org.example.weblab4.entity.Point;
import org.example.weblab4.repository.PointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public Point checkPoint(Point point) {
        logger.debug("Проверка точки: x={}, y={}, r={}", point.getX(), point.getY(), point.getR());
        
        long startTime = System.nanoTime();
        boolean result = checkArea(point.getX(), point.getY(), point.getR());
        long executionTime = (System.nanoTime() - startTime) / 1000; // конвертация в микросекунды
        
        point.setResult(result);
        point.setExecutionTime(executionTime);
        point.setCheckTime(LocalDateTime.now());
        
        Point savedPoint = pointRepository.save(point);
        logger.debug("Точка сохранена: {}", savedPoint);
        
        return savedPoint;
    }

    private boolean checkArea(double x, double y, double r) {
        // Прямоугольник в верхней правой части (0.5 x R)
        boolean inRectangle = x >= 0 && x <= r && y >= 0 && y <= r/2;
        
        // Треугольник в левой нижней части
        // Вершины: (0,0), (-0.5r,0), (0,-r)
        // Уравнение гипотенузы: y = 2x + r
        boolean inTriangle = x <= 0 && x >= -0.5 * r && y <= 0 && y >= -r && y <= 2 * x + r;
        
        // Четверть круга в правой нижней части
        boolean inCircle = x >= 0 && y <= 0 && (x * x + y * y) <= r * r;
        
        return inRectangle || inTriangle || inCircle;
    }

    public List<Point> getAllPoints(Long userId) {
        return pointRepository.findAllByUserId(userId);
    }

    public void clearPoints(Long userId) {
        pointRepository.deleteAllByUserId(userId);
    }
} 