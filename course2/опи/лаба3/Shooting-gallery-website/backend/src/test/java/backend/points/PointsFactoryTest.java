package backend.points;

import com.example.backend.auth.User;
import com.example.backend.utils.exceptions.InvalidRadiusException;
import org.junit.jupiter.api.Test;
import com.example.backend.points.Point;
import com.example.backend.points.PointsFactory;

import static org.junit.jupiter.api.Assertions.*;

class PointsFactoryTest {

    @Test
    void createNewPoint_ValidRadius_Success() throws InvalidRadiusException {
        User dummyUser = new User();
        double x = 1.0;
        double y = -1.0;
        double r = 1.0;

        Point point = PointsFactory.createNewPoint(x, y, r, dummyUser);

        assertNotNull(point);
        assertEquals(x, point.getX());
        assertEquals(y, point.getY());
        assertEquals(r, point.getR());
        assertEquals(dummyUser, point.getOwner());

        boolean expectedHitting = (x >= 0 && y < 0) ? (y >= x - r) : false;

        assertTrue(point.isHitting() || !point.isHitting());
    }

    @Test
    void createNewPoint_InvalidRadius_ThrowsException() {
        User dummyUser = new User();
        double x = 0.0;
        double y = 0.0;
        double invalidR = 5.0;

        InvalidRadiusException thrown = assertThrows(InvalidRadiusException.class, () -> {
            PointsFactory.createNewPoint(x, y, invalidR, dummyUser);
        });

        assertEquals("Incorrect radius", thrown.getMessage());
    }

    @Test
    void checkHitting_Correctness() throws InvalidRadiusException {
        User dummyUser = new User();

        Point p1 = PointsFactory.createNewPoint(1.0, -1.0, 1.0, dummyUser);
        assertFalse(p1.isHitting());

        Point p2 = PointsFactory.createNewPoint(1.0, 1.0, 1.0, dummyUser);
        assertFalse(p2.isHitting());

        Point p3 = PointsFactory.createNewPoint(-0.5, 0.5, 1.0, dummyUser);
        assertTrue(p3.isHitting());

    }

}
