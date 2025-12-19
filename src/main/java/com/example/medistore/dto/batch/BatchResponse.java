package com.example.medistore.dto.batch;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class BatchResponse {

    private UUID id;
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private int quantity; // theo đơn vị nhỏ nhất
    private String status;
}
