package com.example.medistore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherResponse {

    private UUID id;
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

    // tracking
    private Integer usedCount;
    private Integer remainingTurns;

    private LocalDateTime createdAt;
}