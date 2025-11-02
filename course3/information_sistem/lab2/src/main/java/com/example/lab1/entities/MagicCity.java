package com.example.lab1.entities;

import com.example.lab1.dto.BookCreatureType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "magic_city")
public class MagicCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(name = "name")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @Positive
    @Column(name = "area")
    private Long area; //Значение поля должно быть больше 0, Поле не может быть null

    @NotNull
    @Positive
    @Column(name = "population")
    private long population; //Значение поля должно быть больше 0

    @Temporal(TemporalType.DATE)
    @Column(name = "establishment_date")
    private java.util.Date establishmentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "governor")
    private BookCreatureType governor; //Поле не может быть null


    @Column(name = "capital")
    private boolean capital;

    @NotNull
    @Positive
    @Column(name = "population_density")
    private Long populationDensity; //Значение поля должно быть больше 0


    @PrePersist
    protected void onCreate() {
        if (establishmentDate == null) {
            establishmentDate = new java.util.Date();
        }
    }

    public MagicCity() {
    }

    public MagicCity(String name, Long area, long population) {
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
