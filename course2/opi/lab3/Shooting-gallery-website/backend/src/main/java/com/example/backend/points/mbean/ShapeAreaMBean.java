package com.example.backend.points.mbean;

public interface ShapeAreaMBean {
    void addPoint(double x, double y);
    double getArea();
    void clearPoints();
}
