package com.example.medistore.dto.cart;

import java.util.UUID;

import lombok.Data;

@Data
public class AddToCartRequest {
    private UUID productId;
    private UUID productUnitId;
    private int quantity;
}