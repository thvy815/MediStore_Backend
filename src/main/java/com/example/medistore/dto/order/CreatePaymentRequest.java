package com.example.medistore.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePaymentRequest {

    private UUID orderId;

    private UUID paymentMethodId;

    private BigDecimal amount;
}