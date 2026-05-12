package com.example.medistore.dto.order;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ApplyVoucherResponse {
    private String voucherCode;
    private String discountType;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String message;
}
