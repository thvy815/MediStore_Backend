package com.example.medistore.dto.batch;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class CreateBatchRequest {

    private UUID productId;
    private UUID supplierId;
    private UUID productUnitId; // đơn vị nhập kho

    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;

    private int quantity; // số lượng theo product unit
}
