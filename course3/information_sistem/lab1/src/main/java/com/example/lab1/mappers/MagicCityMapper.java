package com.example.lab1.mappers;

import com.example.lab1.dto.MagicCityDto;
import com.example.lab1.entities.MagicCity;

public class MagicCityMapper {

    public static MagicCityDto toDTO(MagicCity entity) {
        if (entity == null) return null;

        MagicCityDto dto = new MagicCityDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setArea(entity.getArea());
        dto.setPopulation(entity.getPopulation());
        dto.setEstablishmentDate(entity.getEstablishmentDate());
        dto.setGovernor(entity.getGovernor());
        dto.setCapital(entity.getCapital());
        dto.setPopulationDensity(entity.getPopulationDensity());
        return dto;
    }

    public static MagicCity toEntity(MagicCityDto dto) {
        if (dto == null) return null;

        MagicCity entity = new MagicCity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setArea(dto.getArea());
        entity.setPopulation(dto.getPopulation());
        entity.setEstablishmentDate(dto.getEstablishmentDate());
        entity.setGovernor(dto.getGovernor());
        entity.setCapital(dto.getCapital());
        entity.setPopulationDensity(dto.getPopulationDensity());
        return entity;
    }
}
