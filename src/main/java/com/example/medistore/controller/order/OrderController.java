package com.example.medistore.controller.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.medistore.dto.order.CreateOrderRequest;
import com.example.medistore.dto.order.OrderResponse;
import com.example.medistore.service.order.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // TẠO ĐƠN HÀNG
    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    //get all
    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    // LẤY ĐƠN HÀNG THEO USER
    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUser(@PathVariable UUID userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PutMapping("/{orderId}/delivered")
    public OrderResponse markAsDelivered(@PathVariable UUID orderId) {
        return orderService.markAsDelivered(orderId);
    }

    @PutMapping("/{orderId}/complete")
    public OrderResponse completeOrder(
            @PathVariable UUID orderId,
            @RequestParam UUID userId
    ) {
        return orderService.completeOrder(orderId, userId);
    }

    @PutMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam UUID userId
    ) {
        return orderService.cancelOrder(orderId, userId);
    }

    @GetMapping("/filter")
    public List<OrderResponse> filterOrders(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        return orderService.getOrdersByDateRange(start, end);
    }

    @GetMapping("/user/{userId}/filter")
public List<OrderResponse> filterOrdersByUser(
        @PathVariable UUID userId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);

    return orderService.getOrdersByUserAndDateRange(userId, start, end);
}
}

