package com.example.medistore.dto.product;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReviewResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private UUID productId;
    private String productName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}