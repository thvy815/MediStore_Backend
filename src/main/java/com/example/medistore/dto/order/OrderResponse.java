package com.example.medistore.dto.order;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID orderId;
    private String status;
    private double totalAmount;

    private List<ItemResponse> items;

    @Data
    @Builder
    public static class ItemResponse {
        private UUID productId;
        private UUID unitId;
        private String productName;
        private String unitName;
        private int quantity;
        private double unitPrice;
    }
}

