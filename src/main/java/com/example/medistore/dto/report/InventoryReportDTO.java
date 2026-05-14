package com.example.medistore.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryReportDTO {

    private String productName;
    private Long remainingQuantity;
}