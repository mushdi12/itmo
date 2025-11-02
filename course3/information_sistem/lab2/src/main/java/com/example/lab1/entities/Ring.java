package com.example.lab1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "ring")
public class Ring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(name = "ring_name")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Positive
    @Column(name = "ring_power")
    private Long power; //Значение поля должно быть больше 0, Поле может быть null

    public Ring() {
    }

    public Ring(String name, Long power) {
        this.name = name;
        this.power = power;
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

    public Long getPower() {
        return power;
    }

    public void setPower(Long power) {
        this.power = power;
    }


}
