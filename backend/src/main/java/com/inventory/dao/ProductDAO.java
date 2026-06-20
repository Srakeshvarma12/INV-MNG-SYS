package com.inventory.dao;

import com.inventory.entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ProductDAO {

    public Product findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Product.class, id);
        } catch (Exception e) {
            System.err.println("Error in ProductDAO.findById: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p ORDER BY p.id ASC", Product.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in ProductDAO.findAll: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    // Required SQL JOIN 1: Products with category name and supplier name (via JOIN FETCH)
    public List<Product> findAllWithCategory() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.supplier ORDER BY p.id ASC", 
                Product.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in ProductDAO.findAllWithCategory: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    // Required SQL JOIN 3: Low-stock products (quantity below min_quantity)
    public List<Product> findLowStock() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.quantity <= p.minQuantity ORDER BY p.quantity ASC", 
                Product.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in ProductDAO.findLowStock: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public boolean save(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Since category and supplier are fetched from the database, we need to merge/find them first if detached
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                product.setCategory(em.find(product.getCategory().getClass(), product.getCategory().getId()));
            }
            if (product.getSupplier() != null && product.getSupplier().getId() != null) {
                product.setSupplier(em.find(product.getSupplier().getClass(), product.getSupplier().getId()));
            }
            em.persist(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in ProductDAO.save: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean update(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                product.setCategory(em.find(product.getCategory().getClass(), product.getCategory().getId()));
            }
            if (product.getSupplier() != null && product.getSupplier().getId() != null) {
                product.setSupplier(em.find(product.getSupplier().getClass(), product.getSupplier().getId()));
            } else {
                product.setSupplier(null);
            }
            em.merge(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in ProductDAO.update: " + e.getMessage());
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
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error in ProductDAO.delete: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
