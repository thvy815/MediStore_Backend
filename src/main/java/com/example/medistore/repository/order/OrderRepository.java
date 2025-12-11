package com.example.medistore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.order.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    
}
