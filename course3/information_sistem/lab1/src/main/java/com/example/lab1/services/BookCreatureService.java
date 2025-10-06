package com.example.lab1.services;

import com.example.lab1.entities.BookCreature;
import com.example.lab1.repositories.BookCreatureRepository;
import com.example.lab1.repositories.MagicCityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BookCreatureService {

    @Inject
    private BookCreatureRepository repository;

    @Inject
    private MagicCityRepository magicCityRepository;

    @Transactional
    public BookCreature create(BookCreature bc) {
        Long id = bc.getCreatureLocation().getId();
        if (id != null) {
            bc.setCreatureLocation(magicCityRepository.findMagicCity(id));
        }
        return repository.create(bc);
    }

    @Transactional
    public BookCreature update(Integer id, BookCreature bc) {
        return repository.update(id, bc);
    }

    @Transactional
    public boolean delete(Integer id) {
        return repository.delete(id);
    }

    public List<BookCreature> findAllBookCreatures() {
        return repository.findAllBookCreatures();
    }

    public BookCreature findBookCreatures(Integer id) {
        return repository.findBookCreatures(id);
    }

    // special operations
    @Transactional
    public int deleteByAttackLevel(Double attackLevel) {
        return repository.deleteByAttackLevel(attackLevel);
    }

    public List<BookCreature> findByNameContains(String substring) {
        return repository.findByNameContains(substring);
    }

    public List<Double> findDistinctAttackLevels() {
        return repository.findDistinctAttackLevels();
    }

    @Transactional
    public boolean swapRings(Integer id1, Integer id2) {
        return repository.swapRings(id1, id2);
    }
}
