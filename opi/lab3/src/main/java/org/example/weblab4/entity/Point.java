package org.example.weblab4.entity;

import java.time.LocalDateTime;

public class Point {
    private Integer id;
    private Double x;
    private Double y;
    private Double r;
    private Boolean result;
    private Long executionTime;
    private LocalDateTime checkTime;
    private Long userId;

    public Point() {
    }

    public Point(Integer id, Double x, Double y, Double r, Boolean result, Long executionTime, LocalDateTime checkTime, Long userId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
        this.executionTime = executionTime;
        this.checkTime = checkTime;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public Boolean isResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(LocalDateTime checkTime) {
        this.checkTime = checkTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 