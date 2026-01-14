package com.example.lab1.repositories;

import com.example.lab1.entities.BookCreature;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BookCreatureRepository {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Transactional
    public BookCreature create(BookCreature bookCreature) {
        if (bookCreature.getId() != null) {
            throw new IllegalArgumentException("bookCreature уже существует в базе");
        }
        em.getTransaction().begin();
        em.persist(bookCreature);
        em.getTransaction().commit();
        return bookCreature;
    }

    @Transactional
    public BookCreature update(Integer id, BookCreature bookCreature) {
        BookCreature existing = em.find(BookCreature.class, id);
        if (existing == null) return null;
        em.getTransaction().begin();
        em.merge(bookCreature);
        em.getTransaction().commit();
        return em.merge(bookCreature);
    }

    @Transactional
    public boolean delete(Integer id) {
        em.getTransaction().begin();
        BookCreature bc = em.find(BookCreature.class, id);
        if (bc == null) return false;
        try {
            bc.setCreatureLocation(null);
            em.merge(bc);
            em.remove(bc);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<BookCreature> findAllBookCreatures() {
        return em.createQuery("SELECT bc FROM BookCreature bc", BookCreature.class).getResultList();
    }

    public BookCreature findBookCreatures(Integer id) {
        return em.find(BookCreature.class, id);
    }

    // ---- Special operations ----
    @Transactional
    public int deleteByAttackLevel(Double attackLevel) {
        List<BookCreature> list = em.createQuery(
                "SELECT bc FROM BookCreature bc WHERE bc.attackLevel = :val", BookCreature.class)
            .setParameter("val", attackLevel)
            .getResultList();
        em.getTransaction().begin();
        int count = 0;
        for (BookCreature bc : list) {
            try {
                bc.setCreatureLocation(null);
                em.merge(bc);
                em.remove(bc);
                count++;
            } catch (Exception ignored) {}
        }
        em.getTransaction().commit();
        return count;
    }

    public List<BookCreature> findByNameContains(String substring) {
        return em.createQuery(
                "SELECT bc FROM BookCreature bc WHERE LOWER(bc.name) LIKE :q",
                BookCreature.class)
            .setParameter("q", "%" + substring.toLowerCase() + "%")
            .getResultList();
    }

    public List<Double> findDistinctAttackLevels() {
        return em.createQuery(
                "SELECT DISTINCT bc.attackLevel FROM BookCreature bc ORDER BY bc.attackLevel",
                Double.class)
            .getResultList();
    }

    @Transactional
    public boolean swapRings(Integer id1, Integer id2) {
        if (id1 == null || id2 == null || id1.equals(id2)) return false;
        BookCreature a = em.find(BookCreature.class, id1);
        BookCreature b = em.find(BookCreature.class, id2);
        if (a == null || b == null) return false;
        em.getTransaction().begin();
        var ringA = a.getRing();
        a.setRing(b.getRing());
        b.setRing(ringA);
        em.merge(a);
        em.merge(b);
        em.getTransaction().commit();
        return true;
    }
}
