package com.example.medistore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class VoucherRequest {
    private String code;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    private Integer usagePerUser;
    private String status;
}
