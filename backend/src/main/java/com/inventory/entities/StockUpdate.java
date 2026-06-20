package com.inventory.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "stock_updates")
public class StockUpdate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Column(name = "change_qty", nullable = false)
    private Integer changeQty;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;

    public StockUpdate() {}

    public StockUpdate(Product product, User updatedBy, Integer changeQty, String note) {
        this.product = product;
        this.updatedBy = updatedBy;
        this.changeQty = changeQty;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(Integer changeQty) {
        this.changeQty = changeQty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "StockUpdate{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", updatedBy=" + (updatedBy != null ? updatedBy.getUsername() : "null") +
                ", changeQty=" + changeQty +
                ", note='" + note + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
