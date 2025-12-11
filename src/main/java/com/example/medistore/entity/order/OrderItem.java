package com.example.medistore.entity.order;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import com.example.medistore.entity.batch.ProductBatch;
import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductUnit;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private ProductBatch batch;

    @ManyToOne
    @JoinColumn(name = "productUnit_id")
    private ProductUnit productUnit;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_price")
    private double unitPrice;
}
