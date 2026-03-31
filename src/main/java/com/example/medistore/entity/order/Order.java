package com.example.medistore.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.example.medistore.entity.user.User;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column(name = "status", length = 50)
    private String status = "pending";  // pending, confirmed, preparing, shipping, delivered, completed, cancelled, refunded

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "shipping_name", length = 100)
    private String shippingName;

    @Column(name = "shipping_phone", length = 20)
    private String shippingPhone;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @ManyToOne
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

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
