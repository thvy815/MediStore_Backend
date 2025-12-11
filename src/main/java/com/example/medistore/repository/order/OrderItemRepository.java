package com.example.medistore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.order.OrderItem;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    
}
