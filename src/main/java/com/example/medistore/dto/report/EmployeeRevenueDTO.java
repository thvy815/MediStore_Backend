package com.example.medistore.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class EmployeeRevenueDTO {

    private String employeeName;
    private BigDecimal revenue;
    private BigDecimal commission;
}