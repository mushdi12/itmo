package com.example.lab1.services;

import com.example.lab1.entities.MagicCity;
import com.example.lab1.repositories.MagicCityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class MagicCityService {

    @Inject
    private MagicCityRepository magicCityrepository;

    @Transactional
    public MagicCity create(MagicCity city) {
        return magicCityrepository.create(city);
    }

    @Transactional
    public MagicCity update(Long id, MagicCity city) {
        return magicCityrepository.update(id, city);
    }

    @Transactional
    public boolean delete(Long id) {
        return magicCityrepository.delete(id);
    }


    public List<MagicCity> findAllMagicCities() {
        return magicCityrepository.findAllMagicCities();
    }

    public MagicCity findMagicCity(Long id) {
        return magicCityrepository.findMagicCity(id);
    }

    @Transactional
    public int deleteElfCities() {
        return magicCityrepository.deleteElfCities();
    }
}
