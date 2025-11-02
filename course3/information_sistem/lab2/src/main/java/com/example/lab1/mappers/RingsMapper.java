package com.example.lab1.mappers;

import com.example.lab1.dto.MagicCityDto;
import com.example.lab1.dto.RingDto;
import com.example.lab1.entities.MagicCity;
import com.example.lab1.entities.Ring;

public class RingsMapper {

    public static RingDto toDTO(Ring entity) {
        if (entity == null) return null;

        RingDto dto = new RingDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPower(entity.getPower());
        return dto;
    }

    public static Ring toEntity(RingDto dto) {
        if (dto == null) return null;

        Ring entity = new Ring();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPower(dto.getPower());
        return entity;
    }
}
