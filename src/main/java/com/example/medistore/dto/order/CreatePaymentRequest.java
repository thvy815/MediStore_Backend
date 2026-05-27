package com.example.medistore.dto.order;

import lombok.Data;

import java.util.UUID;

@Data
public class CreatePaymentRequest {

    private UUID orderId;

}