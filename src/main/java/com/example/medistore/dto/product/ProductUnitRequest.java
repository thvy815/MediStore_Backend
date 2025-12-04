package com.example.medistore.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductUnitRequest {
    private UUID unitId;          // ID của bảng units
    private Integer conversionFactor;
    private BigDecimal price;
    private Boolean isDefault;
}

