package com.example.lab1.repositories;

import com.example.lab1.entities.Ring;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class RingRepository {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Transactional
    public Ring create(Ring ring) {
        if (ring.getId() != null) {
            throw new IllegalArgumentException("ring уже существует в базе");
        }
        em.getTransaction().begin();
        em.persist(ring);
        em.getTransaction().commit();
        return ring;
    }

    @Transactional
    public Ring update(Long id, Ring ring) {
        em.getTransaction().begin();
        Ring existing = em.find(Ring.class, id);
        if (existing == null) {
            em.getTransaction().rollback();
            return null;
        }

        existing.setName(ring.getName());
        existing.setPower(ring.getPower());

        em.getTransaction().commit();
        return em.merge(ring);
    }

    @Transactional
    public boolean delete(Long id) {
        em.getTransaction().begin();
        Ring r = findRing(id);
        if (r == null) return false;
        try {
            em.remove(r);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
                return false;
            }
    }

    public Ring findRing(Long id) {
        return em.find(Ring.class, id);
    }

    public List<Ring> findAllRings() {
        return em.createQuery("SELECT r FROM Ring r", Ring.class).getResultList();
    }

}


