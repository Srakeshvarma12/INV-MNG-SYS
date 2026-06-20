package com.inventory.dao;

import com.inventory.entities.Supplier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class SupplierDAO {

    public Supplier findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Supplier.class, id);
        } catch (Exception e) {
            System.err.println("Error in SupplierDAO.findById: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public List<Supplier> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Supplier> query = em.createQuery("SELECT s FROM Supplier s ORDER BY s.name ASC", Supplier.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in SupplierDAO.findAll: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public boolean save(Supplier supplier) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(supplier);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in SupplierDAO.save: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean update(Supplier supplier) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(supplier);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in SupplierDAO.update: " + e.getMessage());
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
            Supplier supplier = em.find(Supplier.class, id);
            if (supplier != null) {
                em.remove(supplier);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in SupplierDAO.delete: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
