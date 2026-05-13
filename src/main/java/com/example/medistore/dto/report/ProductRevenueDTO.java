package com.example.medistore.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductRevenueDTO {

    private String productName;
    private Long totalSold;
    private BigDecimal revenue;
}