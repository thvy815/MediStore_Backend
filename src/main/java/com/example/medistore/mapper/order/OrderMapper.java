package com.example.medistore.mapper.order;

import org.springframework.stereotype.Component;

import com.example.medistore.dto.order.OrderResponse;
import com.example.medistore.entity.order.Order;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .deliveryMethodId(
                        order.getDeliveryMethod() != null
                                ? order.getDeliveryMethod().getId()
                                : null
                )
                .deliveryMethodName(
                        order.getDeliveryMethod() != null
                                ? order.getDeliveryMethod().getName()
                                : null
                )
                .shippingFee(order.getShippingFee())
                .items(
                        order.getItems().stream()
                                .map(item -> OrderResponse.ItemResponse.builder()
                                        .orderItemId(item.getId())
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .unitId(item.getProductUnit().getId())
                                        .unitName(item.getProductUnit().getUnit().getName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .build())
                                .toList()
                )
                .build();
    }
}