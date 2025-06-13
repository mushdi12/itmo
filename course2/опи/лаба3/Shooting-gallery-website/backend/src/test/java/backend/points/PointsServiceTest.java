package com.example.backend.points;

import com.example.backend.auth.User;
import com.example.backend.auth.mongodb.UserMongodbManager;
import com.example.backend.points.mongodb.PointMongodbManger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsServiceTest {

    @InjectMocks
    PointsService pointsService;

    @Mock
    UserMongodbManager userDBManager;

    @Mock
    PointMongodbManger pointDBManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePoint_Success() throws Exception {
        String username = "testUser";
        double x = 1.0, y = 2.0, r = 0.5;

        User mockUser = new User();
        when(userDBManager.getUserByUsername(username)).thenReturn(mockUser);

        Point createdPoint = PointsFactory.createNewPoint(x, y, r, mockUser);
        doNothing().when(pointDBManager).addPoint(any(Point.class));

        Point result = pointsService.createPoint(x, y, r, username);

        assertNotNull(result);
        assertEquals(x, result.getX());
        assertEquals(y, result.getY());
        assertEquals(r, result.getR());
        assertEquals(mockUser, result.getOwner());

        verify(userDBManager, times(1)).getUserByUsername(username);
        verify(pointDBManager, times(1)).addPoint(any(Point.class));
    }

    @Test
    void testGetSlicePoints_Success() throws Exception {
        String username = "testUser";
        long start = 0;
        long limit = 5;

        User mockUser = new User();
        when(userDBManager.getUserByUsername(username)).thenReturn(mockUser);

        List<Point> mockPoints = List.of(new Point(), new Point());
        when(pointDBManager.getPointsSlice(mockUser, start, limit)).thenReturn(mockPoints);

        List<Point> result = pointsService.getSlicePoints(username, start, limit);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userDBManager, times(1)).getUserByUsername(username);
        verify(pointDBManager, times(1)).getPointsSlice(mockUser, start, limit);
    }

    @Test
    void testGetCountPointsByUsername_Success() throws Exception {
        String username = "testUser";

        User mockUser = new User();
        when(userDBManager.getUserByUsername(username)).thenReturn(mockUser);

        when(pointDBManager.getCountPointsByUser(mockUser)).thenReturn(42L);

        long count = pointsService.getCountPointsByUsername(username);

        assertEquals(42L, count);

        verify(userDBManager, times(1)).getUserByUsername(username);
        verify(pointDBManager, times(1)).getCountPointsByUser(mockUser);
    }

}
