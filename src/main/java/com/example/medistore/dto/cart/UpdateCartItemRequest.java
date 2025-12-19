package com.example.medistore.dto.cart;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateCartItemRequest {
    private int quantity;
    private boolean isSelected;
    private UUID productUnitId; 
}
