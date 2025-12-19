package com.example.medistore.entity.product;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_units")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductUnit {
    @Id @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "conversion_factor", nullable = false)
    private Integer conversionFactor; // quy đổi ra đơn vị nhỏ nhất, >=1

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
