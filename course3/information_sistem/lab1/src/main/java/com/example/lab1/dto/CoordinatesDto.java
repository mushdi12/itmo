package com.example.lab1.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CoordinatesDto {

    private Long id;

    private Integer x; //Значение поля должно быть больше -380, Поле не может быть null

    private Float y; //Максимальное значение поля: 665, Поле не может быть null

    public CoordinatesDto() {
    }

    public CoordinatesDto(Integer x, Float y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
