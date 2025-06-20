package backend.auth.mongodb;

import com.example.backend.auth.User;
import com.example.backend.utils.mongodb.DatastoreSingletone;
import com.example.backend.utils.exceptions.DatastoreInitException;
import com.example.backend.utils.exceptions.UserNotFoundException;
import com.mongodb.client.result.DeleteResult;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import dev.morphia.query.filters.Filter;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.backend.auth.mongodb.UserMongodbManager;

class UserMongodbManagerTest {

    private Datastore datastore;
    private User user;

    @BeforeEach
    void setup() {
        datastore = mock(Datastore.class);
        user = new User();
        user.setUsername("testUser");
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        Query<User> query = mock(Query.class);
        try (MockedStatic<DatastoreSingletone> mockedStatic = mockStatic(DatastoreSingletone.class)) {
            mockedStatic.when(DatastoreSingletone::getUserDatastore).thenReturn(datastore);

            UserMongodbManager manager = new UserMongodbManager();

            when(datastore.find(User.class)).thenReturn(query);
            when(query.filter((Filter[]) any())).thenReturn(query);
            when(query.first()).thenReturn(null);

            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> manager.updateUser(user));
            assertEquals("User not found", exception.getMessage());
        }
    }



    @Test
    void testDeleteUserSuccess() throws Exception {
        DeleteResult deleteResult = mock(DeleteResult.class);
        try (MockedStatic<DatastoreSingletone> mockedStatic = mockStatic(DatastoreSingletone.class)) {
            mockedStatic.when(DatastoreSingletone::getUserDatastore).thenReturn(datastore);

            UserMongodbManager manager = new UserMongodbManager();

            when(datastore.delete(user)).thenReturn(deleteResult);
            when(deleteResult.getDeletedCount()).thenReturn(1L);

            assertTrue(manager.deleteUser(user));
        }
    }

    @Test
    void testDeleteUserFail() throws Exception {
        DeleteResult deleteResult = mock(DeleteResult.class);
        try (MockedStatic<DatastoreSingletone> mockedStatic = mockStatic(DatastoreSingletone.class)) {
            mockedStatic.when(DatastoreSingletone::getUserDatastore).thenReturn(datastore);

            UserMongodbManager manager = new UserMongodbManager();

            when(datastore.delete(user)).thenReturn(deleteResult);
            when(deleteResult.getDeletedCount()).thenReturn(0L);

            assertFalse(manager.deleteUser(user));
        }
    }

    @Test
    void testGetUserByUsernameSuccess() throws Exception {
        Query<User> query = mock(Query.class);
        try (MockedStatic<DatastoreSingletone> mockedStatic = mockStatic(DatastoreSingletone.class)) {
            mockedStatic.when(DatastoreSingletone::getUserDatastore).thenReturn(datastore);

            UserMongodbManager manager = new UserMongodbManager();

            when(datastore.find(User.class)).thenReturn(query);
            when(query.filter(any())).thenReturn(query);
            when(query.first()).thenReturn(user);

            User result = manager.getUserByUsername("testUser");
            assertEquals(user, result);
        }
    }


}
