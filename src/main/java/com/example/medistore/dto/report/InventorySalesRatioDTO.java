package com.example.medistore.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventorySalesRatioDTO {

    private String productName;
    private Double ratio;
}