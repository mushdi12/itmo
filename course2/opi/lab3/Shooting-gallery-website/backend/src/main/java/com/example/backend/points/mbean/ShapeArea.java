package com.example.backend.points.mbean;

import javax.management.NotificationBroadcasterSupport;
import java.util.ArrayList;
import java.util.List;

public class ShapeArea extends NotificationBroadcasterSupport implements ShapeAreaMBean {

    private final List<double[]> points = new ArrayList<>(3);

    @Override
    public  void addPoint(double x, double y) {

        if (points.size() == 3) {
            getArea();
            points.remove(0); // удаляем самую старую точку, чтобы хранить последние 3
        }
        points.add(new double[]{x, y});
    }

    @Override
    public  double getArea() {
        if (points.size() < 3) {
            return 0; // недостаточно точек для треугольника
        }
        double[] p1 = points.get(0);
        double[] p2 = points.get(1);
        double[] p3 = points.get(2);

        // Формула площади треугольника через координаты (площадь по формуле Шарля)
        return Math.abs(
                (p1[0] * (p2[1] - p3[1]) +
                        p2[0] * (p3[1] - p1[1]) +
                        p3[0] * (p1[1] - p2[1])) / 2.0);
    }

    @Override
    public synchronized void clearPoints() {
        points.clear();
    }
}

