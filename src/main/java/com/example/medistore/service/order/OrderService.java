package com.example.medistore.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.medistore.dto.order.CreateOrderRequest;
import com.example.medistore.dto.order.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByUser(UUID userId);

    OrderResponse markAsDelivered(UUID orderId);

    OrderResponse completeOrder(UUID orderId, UUID userId);

    OrderResponse cancelOrder(UUID orderId, UUID userId);
    List<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

List<OrderResponse> getOrdersByUserAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
}