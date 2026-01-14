package com.example.lab1.mappers;

import com.example.lab1.dto.ImportHistoryDto;
import com.example.lab1.entities.ImportHistory;

public class ImportHistoryMapper {
    public static ImportHistoryDto toDTO(ImportHistory history) {
        if (history == null) {
            return null;
        }
        
        ImportHistoryDto dto = new ImportHistoryDto();
        dto.setId(history.getId());
        dto.setStatus(history.getStatus());
        dto.setUserName(history.getUserName());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setObjectsCount(history.getObjectsCount());
        dto.setErrorMessage(history.getErrorMessage());
        dto.setFileObjectName(history.getFileObjectName());
        
        return dto;
    }
}

