package com.example.medistore.dto.product;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductResponse {
    private UUID id;
    private String code;
    private String name;
    private String brandName;
    private String categoryName;
    private String description;
    private String ingredients;
    private String imageUrl;
    private Boolean prescriptionRequired;
    private Boolean isActive;
    private List<ProductUnitResponse> units;
}
