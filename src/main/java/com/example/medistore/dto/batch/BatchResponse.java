package com.example.medistore.dto.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class BatchResponse {

    private UUID id;

    // Product
    private UUID productId;
    private String productName;

    // Supplier
    private UUID supplierId;
    private String supplierName;

    // Batch info
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    
    // Inventory
    private int quantity; // theo đơn vị nhỏ nhất
    private String smallestUnitName; 
    private String status;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
