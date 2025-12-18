package com.example.medistore.dto.product;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductRequest {
    private String code;
    private String name;
    private UUID brandId;
    private UUID categoryId;
    private String description;
    private String ingredients;
    private String imageUrl;
    private Boolean prescriptionRequired;
    private Boolean isActive;
    private List<ProductUnitRequest> units; // thêm list đơn vị
}
