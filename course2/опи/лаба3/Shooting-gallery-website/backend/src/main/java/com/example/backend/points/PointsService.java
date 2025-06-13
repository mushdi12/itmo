package com.example.backend.points;

import com.example.backend.auth.User;
import com.example.backend.auth.mongodb.UserMongodbManager;
import com.example.backend.points.mongodb.PointMongodbManger;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class PointsService {
    @EJB
    private UserMongodbManager userDBManager;

    @EJB
    private PointMongodbManger pointDBManager;

    public Point createPoint(double x, double y, double r, String username) throws Exception{
        User owner = userDBManager.getUserByUsername(username);
        Point point = PointsFactory.createNewPoint(x, y, r, owner);
        this.pointDBManager.addPoint(point);
        return point;
    }

    public List<Point> getSlicePoints(String username, long start, long limit) throws Exception{
        User owner = this.userDBManager.getUserByUsername(username);
        return this.pointDBManager.getPointsSlice(owner, start, limit);
    }

    public long getCountPointsByUsername(String username) throws Exception{
        User owner = userDBManager.getUserByUsername(username);
        return this.pointDBManager.getCountPointsByUser(owner);
    }
}