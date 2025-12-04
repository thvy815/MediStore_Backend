package com.example.medistore.dto.product;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductBatchResponse {
    private UUID id;
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private Integer quantity;
    private String supplierName;
    private String status;
}
