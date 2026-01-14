package com.example.lab1.resources;

import com.example.lab1.dto.BookCreatureDto;
import com.example.lab1.dto.ImportHistoryDto;
import com.example.lab1.dto.ImportRequest;
import com.example.lab1.entities.ImportHistory;
import com.example.lab1.mappers.ImportHistoryMapper;
import com.example.lab1.services.ImportHistoryService;
import com.example.lab1.services.ImportService;
import com.example.lab1.services.MinIOService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
public class ImportResource {

    @Inject
    private ImportService importService;

    @Inject
    private ImportHistoryService importHistoryService;

    @Inject
    private MinIOService minIOService;

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
            // Проверяем, есть ли файл в запросе
            InputStream fileInputStream = null;
            String fileName = request.getFileName();
            
            if (request.getFileContent() != null && !request.getFileContent().trim().isEmpty()) {
                // Декодируем base64 в InputStream
                try {
                    byte[] fileBytes = java.util.Base64.getDecoder().decode(request.getFileContent());
                    fileInputStream = new java.io.ByteArrayInputStream(fileBytes);
                    System.out.println("Файл получен: " + fileName + ", размер: " + fileBytes.length + " байт");
                } catch (Exception e) {
                    System.err.println("ОШИБКА декодирования файла: " + e.getMessage());
                    // Продолжаем без файла
                }
            }
            
            ImportService.ImportResult result = importService.importFromJSON(
                creatures, 
                userName, 
                fileInputStream, 
                fileName
            );
            
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
            // Преобразуем в DTO для правильной сериализации дат
            // Возвращаем пустой массив, если список пуст
            List<ImportHistoryDto> historyDtos = history != null ? history.stream()
                    .map(ImportHistoryMapper::toDTO)
                    .collect(Collectors.toList()) : new java.util.ArrayList<>();
            return Response.ok(historyDtos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Ошибка при получении истории: " + e.getMessage()))
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
            // Преобразуем в DTO для правильной сериализации дат
            ImportHistoryDto dto = ImportHistoryMapper.toDTO(history);
            return Response.ok(dto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/file/{historyId}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    public Response downloadFile(@PathParam("historyId") Long historyId) {
        try {
            ImportHistory history = importHistoryService.findById(historyId);
            if (history == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(Map.of("error", "История импорта не найдена"))
                        .build();
            }

            String fileObjectName = history.getFileObjectName();
            if (fileObjectName == null || fileObjectName.trim().isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(Map.of("error", "Файл не найден для данной истории импорта"))
                        .build();
            }

            if (!minIOService.isAvailable()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(Map.of("error", "Файловое хранилище недоступно"))
                        .build();
            }

            InputStream fileStream = minIOService.downloadFile(fileObjectName);
            
            // Извлекаем оригинальное имя файла из objectName (убираем timestamp_)
            String originalFileName = fileObjectName;
            if (fileObjectName.contains("_")) {
                originalFileName = fileObjectName.substring(fileObjectName.indexOf("_") + 1);
            }
            
            return Response.ok(fileStream)
                    .type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + originalFileName + "\"")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Ошибка при скачивании файла: " + e.getMessage()))
                    .build();
        }
    }
}


