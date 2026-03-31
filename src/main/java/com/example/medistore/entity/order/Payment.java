package com.example.medistore.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount; // = total_price + shipping_fee - discount

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Builder.Default
    @Column(name = "status", length = 50, nullable = false)
    private String status = "pending"; // pending, success, failed

    @Column(name = "transaction_ref", length = 255)
    private String transactionRef; // mã tham chiếu giao dịch

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}