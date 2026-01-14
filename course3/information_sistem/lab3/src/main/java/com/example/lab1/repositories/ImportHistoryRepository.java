package com.example.lab1.repositories;

import com.example.lab1.entities.ImportHistory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ImportHistoryRepository {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Transactional
    public ImportHistory create(ImportHistory history) {
        em.getTransaction().begin();
        em.persist(history);
        em.getTransaction().commit();
        return history;
    }

    public List<ImportHistory> findAll() {
        return em.createQuery("SELECT h FROM ImportHistory h ORDER BY h.createdAt DESC", ImportHistory.class)
                .getResultList();
    }

    public ImportHistory findById(Long id) {
        return em.find(ImportHistory.class, id);
    }
}

