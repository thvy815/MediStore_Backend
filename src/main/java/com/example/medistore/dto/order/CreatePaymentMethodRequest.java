package com.example.medistore.dto.order;

import lombok.Data;

@Data
public class CreatePaymentMethodRequest {

    private String code;
    private String name;
    private Boolean isActive = true;
}