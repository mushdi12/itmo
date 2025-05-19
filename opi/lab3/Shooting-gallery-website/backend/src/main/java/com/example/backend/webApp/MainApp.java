package com.example.backend.webApp;

import com.example.backend.auth.AuthController;
import com.example.backend.auth.jwt.JwtAuthenticationFilter;
import com.example.backend.points.PointsController;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class MainApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(AuthController.class);
        resources.add(JwtAuthenticationFilter.class);
        resources.add(PointsController.class);
        return resources;
    }
}