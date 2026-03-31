package com.example.medistore.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "payment_methods")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentMethod {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code; // cod, momo, vnpay

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}