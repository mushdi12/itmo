package com.example.lab1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_history")
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED

    @Column(name = "user_name")
    private String userName;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "objects_count")
    private Integer objectsCount; // только для успешных операций

    @Column(name = "error_message", length = 2000)
    private String errorMessage; // для неуспешных операций

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public ImportHistory() {
    }

    public ImportHistory(String status, String userName, Integer objectsCount) {
        this.status = status;
        this.userName = userName;
        this.objectsCount = objectsCount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getObjectsCount() {
        return objectsCount;
    }

    public void setObjectsCount(Integer objectsCount) {
        this.objectsCount = objectsCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

