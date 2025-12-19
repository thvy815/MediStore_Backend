package com.example.medistore.service.order;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.order.CreateOrderRequest;
import com.example.medistore.dto.order.OrderResponse;
import com.example.medistore.entity.batch.ProductBatch;
import com.example.medistore.entity.order.Order;
import com.example.medistore.entity.order.OrderItem;
import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.entity.user.User;
import com.example.medistore.repository.batch.BatchRepository;
import com.example.medistore.repository.order.OrderItemRepository;
import com.example.medistore.repository.order.OrderRepository;
import com.example.medistore.repository.product.ProductRepository;
import com.example.medistore.repository.product.ProductUnitRepository;
import com.example.medistore.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;

    // ================= CREATE ORDER =================
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = Order.builder()
                .user(user)
                .status("pending")
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        double totalAmount = 0.0;

        for (CreateOrderRequest.ItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductUnit productUnit = productUnitRepository.findById(itemReq.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Product unit not found"));

            int quantityInSmallestUnit =
                    itemReq.getQuantity() * productUnit.getConversionFactor();

            List<ProductBatch> batches = batchRepository
                    .findAvailableBatches(product.getId(), LocalDate.now());

            int remaining = quantityInSmallestUnit;

            for (ProductBatch batch : batches) {
                if (remaining <= 0) break;

                int deduct = Math.min(batch.getQuantity(), remaining);

                // TRỪ KHO
                batch.setQuantity(batch.getQuantity() - deduct);
                remaining -= deduct;

                if (batch.getQuantity() == 0) {
                    batch.setStatus("out_of_stock");
                }

                batchRepository.save(batch);

                // TẠO ORDER ITEM (gắn batch)
                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .productUnit(productUnit)
                        .batch(batch)
                        .quantity(deduct)
                        .unitPrice(productUnit.getPrice().doubleValue())
                        .build();

                order.getItems().add(orderItem);
                orderItemRepository.save(orderItem);

                totalAmount += deduct * productUnit.getPrice().doubleValue();
            }

            if (remaining > 0) {
                throw new RuntimeException(
                        "Not enough stock for product: " + product.getName());
            }
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        return mapToResponse(order);
    }

    // ================= GET ORDERS BY USER =================
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(UUID userId) {

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= MAPPING =================
    private OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(
                        order.getItems().stream().map(item ->
                                OrderResponse.ItemResponse.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .unitId(item.getProductUnit().getId())
                                        .unitName(item.getProductUnit().getUnit().getName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .build()
                        ).toList()
                )
                .build();
    }
}
