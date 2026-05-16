package com.example.medistore.dto.product;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateProductReviewRequest {
    private UUID userId;
    private UUID productId;
    private UUID orderItemId;
    private Integer rating;
    private String comment;
}