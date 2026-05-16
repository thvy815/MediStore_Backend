package com.example.medistore.dto.product;

import lombok.Data;

@Data
public class UpdateProductReviewRequest {
    private Integer rating;
    private String comment;
}