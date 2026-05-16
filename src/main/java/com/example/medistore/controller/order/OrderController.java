package com.example.medistore.controller.order;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

    // LẤY ĐƠN HÀNG THEO USER
    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUser(@PathVariable UUID userId) {
        return orderService.getOrdersByUser(userId);
    }

    // Update trạng thái đơn hàng
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(
            @PathVariable UUID orderId,
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(orderService.completeOrder(orderId, userId));
    }
}

