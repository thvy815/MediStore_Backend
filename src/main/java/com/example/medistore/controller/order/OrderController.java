package com.example.medistore.controller.order;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.order.CreateOrderRequest;
import com.example.medistore.dto.order.OrderResponse;
import com.example.medistore.service.order.OrderService;

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
}

