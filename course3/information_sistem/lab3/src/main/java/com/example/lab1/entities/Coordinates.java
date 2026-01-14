package com.example.lab1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(-379)
    @Column(name = "coord_x")
    private Integer x; //Значение поля должно быть больше -380, Поле не может быть null

    @NotNull
    @Max(665)
    @Column(name = "coord_y")
    private Float y; //Максимальное значение поля: 665, Поле не может быть null

    public Coordinates() {
    }

    public Coordinates(Integer x, Float y) {
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
