package com.example.lab1.entities;

import com.example.lab1.dto.BookCreatureType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_creature")
public class BookCreature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным,
    // Значение этого поля должно генерироваться автоматически

    @NotBlank
    @Column(nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Coordinates coordinates; //Поле не может быть null

    @NotNull
    @Column(nullable = false)
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Positive
    @Column(nullable = false)
    private long age; //Значение поля должно быть больше 0

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookCreatureType creatureType; //Поле не может быть null

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private MagicCity creatureLocation; //Поле не может быть null

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double attackLevel; //Значение поля должно быть больше 0, Поле не может быть null

    @Positive
    @Column(nullable = false)
    private long defenseLevel; //Значение поля должно быть больше 0

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Ring ring; //Поле не может быть null

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }

    //constructor, getter and setters

    public BookCreature() {
    }

    public BookCreature(Integer id, String name, Coordinates coordinates, LocalDateTime creationDate, long age, BookCreatureType creatureType, MagicCity creatureLocation, Double attackLevel, long defenseLevel, Ring ring) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.age = age;
        this.creatureType = creatureType;
        this.creatureLocation = creatureLocation;
        this.attackLevel = attackLevel;
        this.defenseLevel = defenseLevel;
        this.ring = ring;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public BookCreatureType getCreatureType() {
        return creatureType;
    }

    public void setCreatureType(BookCreatureType creatureType) {
        this.creatureType = creatureType;
    }

    public MagicCity getCreatureLocation() {
        return creatureLocation;
    }

    public void setCreatureLocation(MagicCity creatureLocation) {
        this.creatureLocation = creatureLocation;
    }

    public Double getAttackLevel() {
        return attackLevel;
    }

    public void setAttackLevel(Double attackLevel) {
        this.attackLevel = attackLevel;
    }

    public Ring getRing() {
        return ring;
    }

    public void setRing(Ring ring) {
        this.ring = ring;
    }

    public long getDefenseLevel() {
        return defenseLevel;
    }

    public void setDefenseLevel(long defenseLevel) {
        this.defenseLevel = defenseLevel;
    }
}
