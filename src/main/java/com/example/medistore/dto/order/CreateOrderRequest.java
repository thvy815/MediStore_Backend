package com.example.medistore.dto.order;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

    private UUID userId;

    private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        private UUID productId;
        private UUID unitId; 
        private int quantity;
    }
}

