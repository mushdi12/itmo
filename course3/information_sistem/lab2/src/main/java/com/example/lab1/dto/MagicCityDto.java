package com.example.lab1.dto;

import jakarta.persistence.PrePersist;

import java.util.Date;

public class MagicCityDto {

    private Long id;

    private String name; //Поле не может быть null, Строка не может быть пустой

    private Long area; //Значение поля должно быть больше 0, Поле не может быть null

    private long population; //Значение поля должно быть больше 0

    private java.util.Date establishmentDate;

    private BookCreatureType governor; //Поле не может быть null

    private boolean capital;

    private Long populationDensity; //Значение поля должно быть больше 0

    protected void onCreate() {
        if (establishmentDate == null) {
            establishmentDate = new java.util.Date();
        }
    }

    public MagicCityDto() {
    }

    public MagicCityDto(String name, Long area, long population) {
        this.name = name;
        this.area = area;
        this.population = population;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public Date getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public BookCreatureType getGovernor() {
        return governor;
    }

    public void setGovernor(BookCreatureType governor) {
        this.governor = governor;
    }

    public boolean getCapital() {
        return capital;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public Long getPopulationDensity() {
        return populationDensity;
    }

    public void setPopulationDensity(Long populationDensity) {
        this.populationDensity = populationDensity;
    }
}
