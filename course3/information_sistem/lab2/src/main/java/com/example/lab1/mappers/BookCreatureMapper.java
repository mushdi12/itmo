package com.example.lab1.mappers;

import com.example.lab1.dto.BookCreatureDto;
import com.example.lab1.entities.BookCreature;

public class BookCreatureMapper {

    public static BookCreatureDto toDTO(BookCreature entity) {
        if (entity == null) return null;

        BookCreatureDto dto = new BookCreatureDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCoordinates(entity.getCoordinates());
        dto.setCreationDate(entity.getCreationDate());
        dto.setAge(entity.getAge());
        dto.setCreatureType(entity.getCreatureType());
        dto.setCreatureLocation(entity.getCreatureLocation());
        dto.setAttackLevel(entity.getAttackLevel());
        dto.setRing(entity.getRing());
        dto.setDefenseLevel(entity.getDefenseLevel());
        return dto;
    }

    public static BookCreature toEntity(BookCreatureDto dto) {
        if (dto == null) return null;

        BookCreature entity = new BookCreature();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCoordinates(dto.getCoordinates());
        entity.setCreationDate(dto.getCreationDate());
        entity.setAge(dto.getAge());
        entity.setCreatureType(dto.getCreatureType());
        entity.setCreatureLocation(dto.getCreatureLocation());
        entity.setAttackLevel(dto.getAttackLevel());
        entity.setRing(dto.getRing());
        entity.setDefenseLevel(dto.getDefenseLevel());
        return entity;
    }
}
