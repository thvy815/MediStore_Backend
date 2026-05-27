package com.example.medistore.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {

    private UUID id;

    private String customerName; // thêm

    private BigDecimal amount;

    private String status;

    private String paymentUrl;

    private String transactionRef;

    private String paymentMethod;

    private LocalDateTime createdAt;
}