package com.example.medistore.controller.product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.medistore.dto.product.CreateProductReviewRequest;
import com.example.medistore.dto.product.ProductReviewResponse;
import com.example.medistore.dto.product.UpdateProductReviewRequest;
import com.example.medistore.service.product.ProductReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping
    public ResponseEntity<ProductReviewResponse> createReview(
            @RequestBody CreateProductReviewRequest req
    ) {
        return ResponseEntity.ok(productReviewService.createReview(req));
    }

    @GetMapping
    public ResponseEntity<List<ProductReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(productReviewService.getAllReviews());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReviewResponse>> getReviewsByProduct(
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(productReviewService.getReviewsByProduct(productId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductReviewResponse>> getReviewsByUser(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(productReviewService.getReviewsByUser(userId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @RequestBody UpdateProductReviewRequest req
    ) {
        return ResponseEntity.ok(productReviewService.updateReview(reviewId, req));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        productReviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}