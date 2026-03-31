package com.example.medistore.dto.order;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class PaymentMethodResponse {

    private UUID id;
    private String code;
    private String name;
    private Boolean isActive;
}