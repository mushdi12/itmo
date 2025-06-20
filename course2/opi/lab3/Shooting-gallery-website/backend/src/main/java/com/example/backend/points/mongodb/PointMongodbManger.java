package com.example.backend.points.mongodb;

import com.example.backend.auth.User;
import com.example.backend.points.Point;
import com.example.backend.utils.mongodb.DatastoreSingletone;
import com.example.backend.utils.exceptions.DatastoreInitException;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jakarta.ejb.Stateless;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PointMongodbManger{
    public void addPoint(Point point) throws DatastoreInitException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        datastore.save(point);
    }

    public long getCountPointsByUser(User user) throws DatastoreInitException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        Query<Point> result = datastore.find(Point.class).filter(Filters.eq("owner", user));
        return result.count();
    }

    public List<Point> getPointsSlice(User user, long start, long limit) throws DatastoreInitException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        return datastore.find(Point.class).filter(Filters.eq("owner", user)).stream().skip(start).limit(limit).collect(Collectors.toUnmodifiableList());
    }
}
