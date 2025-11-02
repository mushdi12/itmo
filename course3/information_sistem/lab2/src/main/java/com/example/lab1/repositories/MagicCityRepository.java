package com.example.lab1.repositories;

import com.example.lab1.entities.MagicCity;
import com.example.lab1.dto.BookCreatureType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class MagicCityRepository {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Transactional
    public MagicCity create(MagicCity city) {
        if (city.getId() != null) {
            throw new IllegalArgumentException("city уже существует в базе");
        }
        em.getTransaction().begin();
        em.persist(city);
        em.getTransaction().commit();
        return city;
    }

    @Transactional
    public MagicCity update(Long id, MagicCity city) {
        MagicCity existing = findMagicCity(id);
        if (existing == null) return null;
        em.getTransaction().begin();
        em.merge(city);
        em.getTransaction().commit();
        return em.merge(city);
    }

    @Transactional
    public boolean delete(Long id) {
        em.getTransaction().begin();
        MagicCity city = findMagicCity(id);
        if (city == null) return false;
        try {
            em.remove(city);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public MagicCity findMagicCity(Long id) {
        return em.find(MagicCity.class, id);
    }

    public List<MagicCity> findAllMagicCities() {
        return em.createQuery("SELECT c FROM MagicCity c", MagicCity.class).getResultList();
    }


    // ---- Special operations ----
    @Transactional
    public int deleteElfCities() {
        var cities = em.createQuery("SELECT c FROM MagicCity c WHERE c.governor = :gov", MagicCity.class)
                .setParameter("gov", BookCreatureType.ELF)
                .getResultList();
        if (cities.isEmpty()) return 0;

        em.getTransaction().begin();
        int count = 0;
        try {
            // Сначала удаляем существ, живущих в этих городах, чтобы не нарушать FK
            for (MagicCity city : cities) {
                var creatures = em.createQuery(
                        "SELECT bc FROM BookCreature bc WHERE bc.creatureLocation.id = :cityId",
                        com.example.lab1.entities.BookCreature.class)
                    .setParameter("cityId", city.getId())
                    .getResultList();
                for (var bc : creatures) {
                    em.remove(bc);
                }
            }

            // Затем удаляем сами города
            for (MagicCity c : cities) {
                em.remove(c);
                count++;
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            try { em.getTransaction().rollback(); } catch (Exception ignore) {}
            throw e;
        }

        return count;
    }
}
