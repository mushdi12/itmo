// Примеры улучшенных методов с использованием Pessimistic Locking
// Это примеры для справки, реальные изменения нужно вносить в соответствующие классы

package com.example.lab1.repositories;

import com.example.lab1.entities.BookCreature;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


public class ImprovedBookCreatureRepository {

    @PersistenceContext(name = "bookPU")
    private EntityManager em;

    @Transactional
    public BookCreature update(Integer id, BookCreature bookCreature) {
        em.getTransaction().begin();
        try {

            BookCreature existing = em.find(BookCreature.class, id, LockModeType.PESSIMISTIC_WRITE);
            
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(bookCreature.getName());
            existing.setAge(bookCreature.getAge());
            existing.setAttackLevel(bookCreature.getAttackLevel());
            existing.setDefenseLevel(bookCreature.getDefenseLevel());
            existing.setCreatureType(bookCreature.getCreatureType());

            em.getTransaction().commit();
            return existing;
        } catch (jakarta.persistence.LockTimeoutException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Превышено время ожидания блокировки", e);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Ошибка при обновлении объекта", e);
        }
    }


    @Transactional
    public boolean delete(Integer id) {
        em.getTransaction().begin();
        try {

            BookCreature bc = em.find(BookCreature.class, id, LockModeType.PESSIMISTIC_WRITE);
            
            if (bc == null) {
                em.getTransaction().rollback();
                return false; 
            }


            bc.setCreatureLocation(null);
            em.merge(bc);
            em.remove(bc);
            
            em.getTransaction().commit();
            return true;
        } catch (jakarta.persistence.LockTimeoutException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Превышено время ожидания блокировки", e);
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        }
    }


    @Transactional
    public BookCreature create(BookCreature bookCreature) {
        if (bookCreature.getId() != null) {
            throw new IllegalArgumentException("bookCreature уже существует в базе");
        }
        
        em.getTransaction().begin();
        try {

            
            em.persist(bookCreature);
            em.getTransaction().commit();
            return bookCreature;
        } catch (jakarta.persistence.EntityExistsException e) {
            em.getTransaction().rollback();
            throw new IllegalArgumentException("Объект уже существует", e);
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            em.getTransaction().rollback();
            throw new IllegalArgumentException("Нарушение ограничения уникальности", e);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Ошибка при создании объекта", e);
        }
    }

    @Transactional
    public boolean swapRings(Integer id1, Integer id2) {
        if (id1 == null || id2 == null || id1.equals(id2)) return false;
        
        em.getTransaction().begin();
        try {
            Integer firstId = id1 < id2 ? id1 : id2;
            Integer secondId = id1 < id2 ? id2 : id1;
            
            BookCreature a = em.find(BookCreature.class, firstId, LockModeType.PESSIMISTIC_WRITE);
            BookCreature b = em.find(BookCreature.class, secondId, LockModeType.PESSIMISTIC_WRITE);
            
            if (a == null || b == null) {
                em.getTransaction().rollback();
                return false;
            }
            
            // Обмен кольцами
            var ringA = a.getRing();
            a.setRing(b.getRing());
            b.setRing(ringA);
            
            em.merge(a);
            em.merge(b);
            
            em.getTransaction().commit();
            return true;
        } catch (jakarta.persistence.LockTimeoutException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Превышено время ожидания блокировки", e);
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        }
    }
}

