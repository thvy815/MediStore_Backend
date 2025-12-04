package com.example.medistore.entity.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.medistore.entity.product.Product;

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

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // theo đơn vị nhỏ nhất

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "valid"; // valid, expired, recalled

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
