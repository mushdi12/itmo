package com.example.backend.points.mbean;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.Getter;

@WebListener
public class MBeanInitializer implements ServletContextListener {
    @Getter
    private static PointsCounter pointsCounter;
    @Getter
    private static ShapeArea shapeArea;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            pointsCounter = new PointsCounter();
            shapeArea = new ShapeArea();
            MBeanManager.registerMBeans(pointsCounter, shapeArea);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}

}
