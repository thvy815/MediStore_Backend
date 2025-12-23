package com.example.medistore.dto.batch;

import java.math.BigDecimal;
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

    // Law
    private String lawCode;
    private String lawTitle;

    // Batch info
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    
    // Inventory
    private int quantityImported;   // số lượng lúc nhập
    private int quantityRemaining;  // số lượng tồn
    private String smallestUnitName; 
    private BigDecimal importPrice; // giá nhập theo đơn vị nhỏ nhất
    
    private String status;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
