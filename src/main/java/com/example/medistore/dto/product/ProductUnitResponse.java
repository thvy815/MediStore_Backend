package com.example.medistore.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductUnitResponse {
    private UUID id;
    private String unitName;
    private Integer conversionFactor;
    private BigDecimal price;
    private Boolean isDefault;
}
