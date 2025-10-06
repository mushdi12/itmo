package com.example.lab1.services;

import com.example.lab1.entities.Ring;
import com.example.lab1.repositories.RingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class RingService {

    @Inject
    private RingRepository ringRepository;

    @Transactional
    public Ring create(Ring ring) {
        return ringRepository.create(ring);
    }

    @Transactional
    public Ring update(Long id, Ring ring) {
        return ringRepository.update(id, ring);
    }

    @Transactional
    public boolean delete(Long id) {
        return ringRepository.delete(id);
    }

    public Ring findRing(Long id) {
        return ringRepository.findRing(id);
    }

    public List<Ring> findAllRings() {
        return ringRepository.findAllRings();
    }
}
