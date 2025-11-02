package com.example.lab1.mappers;

import com.example.lab1.dto.CoordinatesDto;
import com.example.lab1.entities.Coordinates;

public class CoordMapper {

    public static CoordinatesDto toDTO(Coordinates entity) {
        if (entity == null) return null;
        CoordinatesDto dto = new CoordinatesDto();

        dto.setId(entity.getId());
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        return dto;
    }

    public static Coordinates toEntity(CoordinatesDto dto) {
        if (dto == null) return null;
        Coordinates entity = new Coordinates();
        entity.setId(dto.getId());
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        return entity;
    }
}
