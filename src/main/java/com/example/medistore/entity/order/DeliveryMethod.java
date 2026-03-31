package com.example.medistore.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "delivery_methods")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryMethod {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name; // standard, express

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_fee", precision = 12, scale = 2, nullable = false)
    private BigDecimal baseFee;

    @Column(name = "estimated_days", nullable = false)
    private Integer estimatedDays;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}