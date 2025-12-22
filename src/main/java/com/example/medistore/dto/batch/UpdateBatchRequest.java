package com.example.medistore.dto.batch;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class UpdateBatchRequest {

    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;

    private UUID productUnitId; // đơn vị nhập lại
    private Integer quantity;   // số lượng theo đơn vị này

    private String lawCode; // mã văn bản pháp luật áp dụng
}

