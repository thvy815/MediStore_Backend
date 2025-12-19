package com.example.medistore.dto.cart;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private UUID productUnitId;
    private String unitName;
    private int quantity;
    private BigDecimal unitPrice;
    private boolean isSelected;
}
