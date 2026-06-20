package com.inventory.dao;

import com.inventory.entities.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CategoryDAO {

    public Category findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Category.class, id);
        } catch (Exception e) {
            System.err.println("Error in CategoryDAO.findById: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c.name ASC", Category.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in CategoryDAO.findAll: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public boolean save(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in CategoryDAO.save: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean update(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(category);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in CategoryDAO.update: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean delete(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in CategoryDAO.delete: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
