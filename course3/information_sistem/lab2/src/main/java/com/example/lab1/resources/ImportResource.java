package com.example.lab1.resources;

import com.example.lab1.dto.BookCreatureDto;
import com.example.lab1.dto.ImportRequest;
import com.example.lab1.entities.ImportHistory;
import com.example.lab1.services.ImportHistoryService;
import com.example.lab1.services.ImportService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
public class ImportResource {

    @Inject
    private ImportService importService;

    @Inject
    private ImportHistoryService importHistoryService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(rollbackOn = Exception.class)
    public Response uploadFile(ImportRequest request) {
        System.out.println("=== ИМПОРТ JSON ===");
        
        if (request == null) {
            System.err.println("ОШИБКА: Запрос не предоставлен");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Запрос не предоставлен"))
                    .build();
        }

        String userName = request.getUserName();
        if (userName == null || userName.trim().isEmpty()) {
            userName = "anonymous";
        }

        List<BookCreatureDto> creatures = request.getCreatures();
        if (creatures == null || creatures.isEmpty()) {
            System.err.println("ОШИБКА: Список объектов пуст");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Список объектов для импорта пуст"))
                    .build();
        }

        System.out.println("Начало импорта для пользователя: " + userName + ", объектов: " + creatures.size());

        try {
            ImportService.ImportResult result = importService.importFromJSON(creatures, userName);
            
            if (result.isSuccess()) {
                System.out.println("Импорт успешен: " + result.getObjectsCount() + " объектов");
                return Response.ok(Map.of(
                        "success", true,
                        "objectsCount", result.getObjectsCount(),
                        "historyId", result.getHistoryId()
                )).build();
            } else {
                System.err.println("ОШИБКА ИМПОРТА: " + result.getErrorMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of(
                                "success", false,
                                "error", result.getErrorMessage()
                        )).build();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("ОШИБКА ВАЛИДАЦИИ: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "success", false,
                            "error", "Ошибка валидации: " + e.getMessage()
                    )).build();
        } catch (Exception e) {
            System.err.println("ОШИБКА ПРИ ИМПОРТЕ: " + e.getMessage());
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " (причина: " + e.getCause().getMessage() + ")";
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                            "success", false,
                            "error", "Ошибка при импорте: " + errorMsg
                    )).build();
        }
    }

    @GET
    @Path("/history")
    public Response getHistory() {
        try {
            List<ImportHistory> history = importHistoryService.findAll();
            return Response.ok(history).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/history/{id}")
    public Response getHistoryById(@PathParam("id") Long id) {
        try {
            ImportHistory history = importHistoryService.findById(id);
            if (history == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(history).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}


