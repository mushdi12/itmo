package com.example.backend.points;

import com.example.backend.auth.User;
import com.example.backend.utils.exceptions.InvalidRadiusException;

import java.util.concurrent.CopyOnWriteArrayList;

public class PointsFactory {
    private static final CopyOnWriteArrayList<Double> PERMITTED_RADIUS;

    static{
        PERMITTED_RADIUS = new CopyOnWriteArrayList<>();
        PERMITTED_RADIUS.add(0.0);
        PERMITTED_RADIUS.add(0.5);
        PERMITTED_RADIUS.add(1.0);
        PERMITTED_RADIUS.add(1.5);
        PERMITTED_RADIUS.add(2.0);
    }

    public static Point createNewPoint(double x, double y, double r, User owner) throws InvalidRadiusException {
        if (!PERMITTED_RADIUS.contains(r)) {
            throw new InvalidRadiusException("Incorrect radius");
        }
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setHitting(PointsFactory.checkHitting(x, y, r));
        point.setOwner(owner);
        return point;
    }

    private static boolean checkHitting(double x, double y, double r){
        if (x >= 0 && y>=0){
            return false;
        }
        if (x >= 0 && y < 0){
            return y >= x - r;
        }
        if (x < 0 && y >= 0){
            return x >= -1*r/2 && y <= r;
        }
        if (x < 0 && y < 0){
            return x*x + y*y <= r*r / 4;
        }
        return false;
    }
}