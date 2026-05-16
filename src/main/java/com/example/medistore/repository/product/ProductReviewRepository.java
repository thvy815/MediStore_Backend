package com.example.medistore.repository.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.medistore.entity.product.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {

    List<ProductReview> findByProductIdOrderByCreatedAtDesc(UUID productId);

    List<ProductReview> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<ProductReview> findByUserIdAndProductId(UUID userId, UUID productId);

    boolean existsByOrderItemId(UUID orderItemId);
}