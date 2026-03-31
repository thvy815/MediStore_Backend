package com.example.medistore.dto.order;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

    private UUID userId;

    private List<ItemRequest> items;

    // Shipping info
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;

    // Delivery
    private UUID deliveryMethodId;

    // Payment
    private UUID paymentMethodId;

    @Data
    public static class ItemRequest {
        private UUID productId;
        private UUID productUnitId; 
        private int quantity;
    }
}

