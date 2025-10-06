package com.example.lab1.dto;

public class RingDto {

    private Long id;

    private String name; //Поле не может быть null, Строка не может быть пустой

    private Long power; //Значение поля должно быть больше 0, Поле может быть null



    public RingDto() {
    }

    public RingDto(String name, Long power) {
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
