package com.example.medistore.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueReportDTO {

    private String period;
    private BigDecimal revenue;
}