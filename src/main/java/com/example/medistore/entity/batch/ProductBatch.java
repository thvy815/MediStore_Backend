package com.example.medistore.entity.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductUnit;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_batches")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductBatch {
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_unit_id", nullable = false)
    private ProductUnit productUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "law_code", referencedColumnName = "code")
    private Law law;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity_imported", nullable = false)
    private Integer quantityImported;   // số lượng nhập ban đầu (đơn vị nhỏ nhất)

    @Column(name = "quantity_remaining", nullable = false)
    private Integer quantityRemaining;  // số lượng còn tồn (đơn vị nhỏ nhất)

    @Column(nullable = false)
    private BigDecimal importPrice; // giá nhập theo đơn vị nhỏ nhất

    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "valid"; // valid, expired, recalled, out_of_stock

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
