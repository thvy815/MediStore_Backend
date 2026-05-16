package com.example.medistore.repository.order;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    boolean existsByOrder_User_IdAndProduct_Id(UUID userId, UUID productId);
}
