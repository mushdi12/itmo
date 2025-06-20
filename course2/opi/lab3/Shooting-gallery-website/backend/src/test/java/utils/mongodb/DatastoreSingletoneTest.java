import com.example.backend.utils.mongodb.DatastoreSingletone;


import com.example.backend.utils.exceptions.DatastoreInitException;
import dev.morphia.Datastore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatastoreSingletoneTest {

    @Test
    void testGetUserDatastoreNotNull() throws DatastoreInitException {
        Datastore datastore = DatastoreSingletone.getUserDatastore();
        assertNotNull(datastore, "Datastore should not be null");
    }

    @Test
    void testThreadLocalDatastoreIsInitialized() throws DatastoreInitException {
        Datastore datastore1 = DatastoreSingletone.getUserDatastore();
        Datastore datastore2 = DatastoreSingletone.getUserDatastore();
        assertSame(datastore1, datastore2, "Datastore should be the same instance for the same thread");
    }


}