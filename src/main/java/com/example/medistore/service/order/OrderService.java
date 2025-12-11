package com.example.medistore.service.order;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.order.CreateOrderRequest;
import com.example.medistore.dto.order.OrderResponse;
import com.example.medistore.entity.order.Order;
import com.example.medistore.entity.order.OrderItem;
import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.entity.user.User;
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
    private final OrderItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final UserRepository userRepository;

    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = Order.builder()
                .user(user)
                .status("pending")
                .items(new ArrayList<>())
                .build();

        order = orderRepository.save(order);

        double total = 0.0;

        for (CreateOrderRequest.ItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductUnit productUnit = productUnitRepository.findById(itemReq.getUnitId())
                    .orElseThrow(() -> new RuntimeException("Product unit not found"));

            double unitPrice = productUnit.getPrice().doubleValue();
            int quantity = itemReq.getQuantity();

            total += unitPrice * quantity;

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .batch(null)  // TODO: chọn batch sau này
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .productUnit(productUnit)
                    .build();

            order.getItems().add(item);   
            itemRepository.save(item);
        }

        order.setTotalAmount(total);
        orderRepository.save(order);

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(
                        order.getItems().stream().map(i ->
                                OrderResponse.ItemResponse.builder()
                                        .productId(i.getProduct().getId())
                                        .productName(i.getProduct().getName())
                                        .unitId(i.getProductUnit().getId())                       
                                        .unitName(i.getProductUnit().getUnit().getName())        
                                        .quantity(i.getQuantity())
                                        .unitPrice(i.getUnitPrice())
                                        .build()
                        ).toList()
                )
                .build();
    }
}

