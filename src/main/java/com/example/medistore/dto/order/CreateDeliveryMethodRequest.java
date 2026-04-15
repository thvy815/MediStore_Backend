package com.example.medistore.dto.order;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateDeliveryMethodRequest {

    private String name;
    private String description;
    private BigDecimal baseFee;
    private Integer estimatedDays;
    private Boolean isActive = true;
}