package com.example.medistore.repository.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate,
        LocalDateTime endDate
);

List<Order> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        UUID userId,
        LocalDateTime startDate,
        LocalDateTime endDate
);
}
