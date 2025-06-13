package com.example.backend.utils.mongodb;

import com.example.backend.utils.exceptions.DatastoreInitException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import java.io.*;
import java.util.Properties;

public class DatastoreSingletone {
    private static final ThreadLocal<Datastore> threadLocalDatastore = ThreadLocal.withInitial(() -> {
        Datastore datastore;
        try (InputStreamReader reader = new InputStreamReader(DatastoreSingletone.class.getClassLoader().getResourceAsStream("config.properties"))) {
            Properties props = new Properties();
            props.load(reader);
            String dbUri = props.getProperty("MONGOD_URI");
            String dbName = props.getProperty("DB_NAME");
            MongoClient mongoClient = MongoClients.create(dbUri);
            datastore = Morphia.createDatastore(mongoClient, dbName);
        } catch (Exception e){
            throw new RuntimeException("Error loading properties file");
        }
        return datastore;
    });

    public static Datastore getUserDatastore() throws DatastoreInitException {
        if (threadLocalDatastore.get() == null) {
            throw new DatastoreInitException("Datastore not initialized");
        }
        return threadLocalDatastore.get();
    }
}
