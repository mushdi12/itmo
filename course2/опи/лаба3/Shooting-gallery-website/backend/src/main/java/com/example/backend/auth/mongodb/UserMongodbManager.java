package com.example.backend.auth.mongodb;

import com.example.backend.auth.User;
import com.example.backend.utils.mongodb.DatastoreSingletone;
import com.example.backend.utils.exceptions.DatastoreInitException;
import com.example.backend.utils.exceptions.UserNotFoundException;
import com.mongodb.client.result.DeleteResult;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class UserMongodbManager {

    public void addUser(User user) throws DatastoreInitException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        datastore.save(user);
    }

    public void updateUser(User user) throws DatastoreInitException, UserNotFoundException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        User currUser = datastore.find(User.class).filter(Filters.eq("username", user.getUsername())).first();
        if (currUser != null) {
            datastore.save(user);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public boolean deleteUser(User user) throws DatastoreInitException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        DeleteResult delResul = datastore.delete(user);
        return delResul.getDeletedCount() > 0;
    }

    public User getUserByUsername(String username) throws DatastoreInitException, UserNotFoundException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        Query<User> findQuery = datastore.find(User.class).filter(Filters.eq("username", username));
        if (findQuery != null) {
            return findQuery.first();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }
}
