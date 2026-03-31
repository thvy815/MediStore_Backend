package com.example.medistore.dto.order;

import lombok.Data;

@Data
public class UpdatePaymentMethodRequest {

    private String code;
    private String name;
    private Boolean isActive;
}