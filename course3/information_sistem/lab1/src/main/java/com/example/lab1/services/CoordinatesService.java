package com.example.lab1.services;

import com.example.lab1.repositories.CoordinatesRepository;
import com.example.lab1.entities.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CoordinatesService {

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Transactional
    public Coordinates create(Coordinates coordinates) {
        return coordinatesRepository.create(coordinates);
    }

    @Transactional
    public Coordinates update(Long id,Coordinates coordinates) {
        return coordinatesRepository.update(id,coordinates);
    }

    @Transactional
    public boolean delete(Long id) {
        return coordinatesRepository.delete(id);
    }

    public List<Coordinates> findAllCoords() {
        return coordinatesRepository.findAllCoords();
    }

    public Coordinates findCoords(Long id) {
        return coordinatesRepository.findCoords(id);
    }
}

