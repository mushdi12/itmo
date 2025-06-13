package com.example.backend.points;

import com.example.backend.auth.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Entity("points")
@JsonIgnoreProperties({"owner", "id"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    @Id
    private ObjectId id;
    private double x;
    private double y;
    private double r;
    private boolean hitting;
    @Reference
    private User owner;
}