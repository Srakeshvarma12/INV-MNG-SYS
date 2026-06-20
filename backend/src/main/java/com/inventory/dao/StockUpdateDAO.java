package com.inventory.dao;

import com.inventory.entities.StockUpdate;
import com.inventory.entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class StockUpdateDAO {

    public StockUpdate findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(StockUpdate.class, id);
        } catch (Exception e) {
            System.err.println("Error in StockUpdateDAO.findById: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public List<StockUpdate> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<StockUpdate> query = em.createQuery("SELECT su FROM StockUpdate su ORDER BY su.id DESC", StockUpdate.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in StockUpdateDAO.findAll: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    // Required SQL JOIN 2: Stock history with product name and updater username
    public List<StockUpdate> findAllWithDetails() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<StockUpdate> query = em.createQuery(
                "SELECT su FROM StockUpdate su JOIN FETCH su.product JOIN FETCH su.updatedBy ORDER BY su.updatedAt DESC", 
                StockUpdate.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in StockUpdateDAO.findAllWithDetails: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public boolean save(StockUpdate stockUpdate) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Fetch managed product and user
            if (stockUpdate.getProduct() != null && stockUpdate.getProduct().getId() != null) {
                Product managedProduct = em.find(Product.class, stockUpdate.getProduct().getId());
                // Update product quantity in the same transaction
                managedProduct.setQuantity(managedProduct.getQuantity() + stockUpdate.getChangeQty());
                stockUpdate.setProduct(managedProduct);
            }
            if (stockUpdate.getUpdatedBy() != null && stockUpdate.getUpdatedBy().getId() != null) {
                stockUpdate.setUpdatedBy(em.find(stockUpdate.getUpdatedBy().getClass(), stockUpdate.getUpdatedBy().getId()));
            }
            em.persist(stockUpdate);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in StockUpdateDAO.save: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean update(StockUpdate stockUpdate) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (stockUpdate.getProduct() != null && stockUpdate.getProduct().getId() != null) {
                stockUpdate.setProduct(em.find(Product.class, stockUpdate.getProduct().getId()));
            }
            if (stockUpdate.getUpdatedBy() != null && stockUpdate.getUpdatedBy().getId() != null) {
                stockUpdate.setUpdatedBy(em.find(stockUpdate.getUpdatedBy().getClass(), stockUpdate.getUpdatedBy().getId()));
            }
            em.merge(stockUpdate);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in StockUpdateDAO.update: " + e.getMessage());
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
            StockUpdate stockUpdate = em.find(StockUpdate.class, id);
            if (stockUpdate != null) {
                em.remove(stockUpdate);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in StockUpdateDAO.delete: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
