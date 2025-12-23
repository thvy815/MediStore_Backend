package com.example.medistore.dto.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class CreateBatchRequest {

    private UUID productId;
    private UUID supplierId;
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    
    private UUID productUnitId; // đơn vị nhập kho
    private int quantity; // số lượng theo product unit
    private BigDecimal importPrice; // GIÁ NHẬP theo unit nhập
    
    private String lawCode; // mã văn bản pháp luật áp dụng
}
