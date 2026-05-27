package com.example.medistore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherHistoryResponse {

    private UUID userId;

    private String customerName;

    private UUID orderId;

    private String voucherCode;

    private BigDecimal discountAmount;

    private LocalDateTime usedAt;
}