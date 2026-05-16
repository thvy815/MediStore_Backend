package com.example.medistore.service.product;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.product.CreateProductReviewRequest;
import com.example.medistore.dto.product.ProductReviewResponse;
import com.example.medistore.dto.product.UpdateProductReviewRequest;
import com.example.medistore.entity.order.OrderItem;
import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductReview;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.order.OrderItemRepository;
import com.example.medistore.repository.product.ProductRepository;
import com.example.medistore.repository.product.ProductReviewRepository;
import com.example.medistore.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductReviewResponse createReview(CreateProductReviewRequest req) {

        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        boolean hasBought = orderItemRepository
                .existsByOrder_User_IdAndProduct_Id(req.getUserId(), req.getProductId());

        if (!hasBought) {
            throw new RuntimeException("You can only review products you have purchased");
        }

        if (productReviewRepository.existsByOrderItemId(req.getOrderItemId())) {
    throw new RuntimeException("This order item has already been reviewed");
}

OrderItem orderItem = orderItemRepository.findById(req.getOrderItemId())
        .orElseThrow(() -> new RuntimeException("Order item not found"));

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductReview review = ProductReview.builder()
                .user(user)
                .product(product)
                .orderItem(orderItem)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        productReviewRepository.save(review);

        return mapToResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ProductReviewResponse> getAllReviews() {
        return productReviewRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductReviewResponse> getReviewsByProduct(UUID productId) {
        return productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductReviewResponse> getReviewsByUser(UUID userId) {
        return productReviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductReviewResponse updateReview(UUID reviewId, UpdateProductReviewRequest req) {

        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (req.getRating() != null) {
            if (req.getRating() < 1 || req.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }
            review.setRating(req.getRating());
        }

        if (req.getComment() != null) {
            review.setComment(req.getComment());
        }

        productReviewRepository.save(review);

        return mapToResponse(review);
    }

    public void deleteReview(UUID reviewId) {
        productReviewRepository.deleteById(reviewId);
    }

    private ProductReviewResponse mapToResponse(ProductReview review) {
        return ProductReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}