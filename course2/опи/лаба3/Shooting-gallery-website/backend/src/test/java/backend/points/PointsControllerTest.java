package backend.points;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import com.example.backend.points.PointsController;
import com.example.backend.points.PointsService;
import java.util.List;
import com.example.backend.points.Point;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsControllerTest {

    PointsController controller;
    PointsService mockService;
    final String fakeToken = "Bearer faketoken";

    @BeforeEach
    void setup() throws Exception {
        controller = new PointsController();
        mockService = mock(PointsService.class);

        Field field = PointsController.class.getDeclaredField("pointsService");
        field.setAccessible(true);
        field.set(controller, mockService);
    }

    @Test
    void testGetPointsSlice_Success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("start", 0);
        json.put("limit", 10);

        when(mockService.getSlicePoints(any(), eq(0L), eq(10L))).thenReturn(List.of(new Point(), new Point()));

        Response response = controller.getPointsSlice(json, fakeToken);
        assertEquals(200, response.getStatus());
        List<?> points = (List<?>) response.getEntity();
        assertEquals(2, points.size());
    }

    @Test
    void testGetPointsSlice_InvalidJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("start", "abc"); // Invalid

        Response response = controller.getPointsSlice(json, fakeToken);
        assertEquals(400, response.getStatus());
    }


    @Test
    void testCreatePoint_InvalidJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("x", 1.0);
        json.put("y", 2.0);

        Response response = controller.createPoint(fakeToken, json);
        assertEquals(400, response.getStatus());
    }
}
