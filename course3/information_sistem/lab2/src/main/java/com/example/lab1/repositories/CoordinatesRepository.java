package com.example.lab1.repositories;

import com.example.lab1.entities.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CoordinatesRepository {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Transactional
    public Coordinates create(Coordinates coordinates) {
        em.getTransaction().begin();
        em.persist(coordinates);
        em.getTransaction().commit();
        return coordinates;
    }


    @Transactional
    public Coordinates update(Long id, Coordinates coordinates) {
        Coordinates existing = findCoords(id);
        if (existing == null) return null;
        em.getTransaction().begin();
        em.merge(coordinates);
        em.getTransaction().commit();
        return em.merge(coordinates);
    }

    @Transactional
    public boolean delete(Long id) {
        em.getTransaction().begin();
        Coordinates coordinates = findCoords(id);
        if (coordinates == null) return false;
        try {
            em.remove(coordinates);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Coordinates> findAllCoords() {
        return em.createQuery("SELECT c FROM Coordinates c", Coordinates.class).getResultList();
    }

    public Coordinates findCoords(Long id) {
        return em.find(Coordinates.class, id);
    }

}

