package com.example.medistore.dto.order;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateDeliveryMethodRequest {

    private String name;
    private String description;
    private BigDecimal baseFee;
    private Integer estimatedDays;
    private Boolean isActive;
}