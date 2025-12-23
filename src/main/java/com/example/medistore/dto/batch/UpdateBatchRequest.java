package com.example.medistore.dto.batch;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateBatchRequest {

    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private String lawCode; // mã văn bản pháp luật áp dụng
}

