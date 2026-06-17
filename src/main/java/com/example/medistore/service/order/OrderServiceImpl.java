package com.example.medistore.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.order.CreateOrderRequest;
import com.example.medistore.dto.order.OrderResponse;
import com.example.medistore.entity.batch.ProductBatch;
import com.example.medistore.entity.order.DeliveryMethod;
import com.example.medistore.entity.order.Order;
import com.example.medistore.entity.order.OrderItem;
import com.example.medistore.entity.order.OrderVoucher;
import com.example.medistore.entity.order.Payment;
import com.example.medistore.entity.order.PaymentMethod;
import com.example.medistore.entity.order.Voucher;
import com.example.medistore.entity.product.Product;
import com.example.medistore.entity.product.ProductUnit;
import com.example.medistore.entity.user.User;
import com.example.medistore.enums.NotificationType;
import com.example.medistore.mapper.order.OrderMapper;
import com.example.medistore.repository.batch.BatchRepository;
import com.example.medistore.repository.cart.CartItemRepository;
import com.example.medistore.repository.order.DeliveryMethodRepository;
import com.example.medistore.repository.order.OrderItemRepository;
import com.example.medistore.repository.order.OrderRepository;
import com.example.medistore.repository.order.OrderVoucherRepository;
import com.example.medistore.repository.order.PaymentMethodRepository;
import com.example.medistore.repository.order.PaymentRepository;
import com.example.medistore.repository.product.ProductRepository;
import com.example.medistore.repository.product.ProductUnitRepository;
import com.example.medistore.repository.user.UserRepository;
import com.example.medistore.service.user.NotificationService;
import com.example.medistore.util.OrderCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final NotificationService notificationService;
    private final VoucherService voucherService;
    private final OrderVoucherRepository orderVoucherRepository;
    private final OrderMapper orderMapper;

    // ================= CREATE ORDER =================
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(request.getDeliveryMethodId())
                .orElseThrow(() -> new RuntimeException("Delivery method not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        String shippingName = request.getShippingName();
        String shippingPhone = request.getShippingPhone();
        String shippingAddress = request.getShippingAddress();

        if (shippingName == null || shippingName.isEmpty()) {
            shippingName = user.getFullName();
        }

        if (shippingPhone == null || shippingPhone.isEmpty()) {
            shippingPhone = user.getPhone();
        }

        if (shippingAddress == null || shippingAddress.isEmpty()) {
            shippingAddress = user.getAddress();
        }

        Order order = Order.builder()
                .user(user)
                .status("pending")
                .items(new ArrayList<>())
                .shippingName(shippingName)
                .shippingPhone(shippingPhone)
                .shippingAddress(shippingAddress)
                .deliveryMethod(deliveryMethod)
                .shippingFee(deliveryMethod.getBaseFee())
                .build();

        if (paymentMethod.getCode().equalsIgnoreCase("cod")) {
            order.setStatus("confirmed");
        }

        order = orderRepository.save(order);

        BigDecimal productAmount = BigDecimal.ZERO;

        for (CreateOrderRequest.ItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductUnit productUnit = productUnitRepository.findById(itemReq.getProductUnitId())
                    .orElseThrow(() -> new RuntimeException("Product unit not found"));

            int quantityInSmallestUnit = itemReq.getQuantity() * productUnit.getConversionFactor();

            List<ProductBatch> batches = batchRepository
                    .findAvailableBatches(product.getId(), LocalDate.now());

            int remaining = quantityInSmallestUnit;

            for (ProductBatch batch : batches) {

                if (remaining <= 0)
                    break;

                int available = batch.getQuantityRemaining();

                if (available <= 0)
                    continue;

                int deduct = Math.min(available, remaining);

                batch.setQuantityRemaining(available - deduct);

                remaining -= deduct;

                if (batch.getQuantityRemaining() == 0) {
                    batch.setStatus("out_of_stock");
                }

                batchRepository.save(batch);

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .productUnit(productUnit)
                        .batch(batch)
                        .quantity(itemReq.getQuantity())
                        .unitPrice(productUnit.getPrice().doubleValue())
                        .build();

                order.getItems().add(orderItem);

                orderItemRepository.save(orderItem);
            }

            if (remaining > 0) {
                throw new RuntimeException(
                        "Not enough stock for product: " + product.getName());
            }

            BigDecimal itemAmount = productUnit.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            productAmount = productAmount.add(itemAmount);
        }

        // ================= TOTAL =================

        BigDecimal shippingFee = order.getShippingFee() != null
                ? order.getShippingFee()
                : BigDecimal.ZERO;

        BigDecimal orderAmountBeforeDiscount = productAmount.add(shippingFee);

        BigDecimal discountAmount = BigDecimal.ZERO;

        Voucher appliedVoucher = null;

        if (request.getVoucherCode() != null
                && !request.getVoucherCode().isBlank()) {

            appliedVoucher = voucherService.validateVoucher(
                    request.getVoucherCode(),
                    orderAmountBeforeDiscount,
                    user.getId());

            discountAmount = voucherService.calculateDiscount(
                    appliedVoucher,
                    productAmount,
                    shippingFee);
        }

        BigDecimal finalAmount = orderAmountBeforeDiscount
                .subtract(discountAmount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        order.setTotalAmount(finalAmount.doubleValue());

        orderRepository.save(order);

        if (appliedVoucher != null) {

            OrderVoucher orderVoucher = OrderVoucher.builder()
                    .order(order)
                    .voucher(appliedVoucher)
                    .discountAmount(discountAmount)
                    .build();

            orderVoucherRepository.save(orderVoucher);
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(finalAmount)
                .paymentMethod(paymentMethod)
                .status("pending")
                .build();

        paymentRepository.save(payment);

        for (CreateOrderRequest.ItemRequest item : request.getItems()) {

            if (item.getCartItemId() != null) {
                cartItemRepository.deleteById(item.getCartItemId());
            }
        }

        notificationService.sendNotification(
                user.getId(),
                "Order Placed Successfully",
                "Your order " + OrderCode.generate(order.getId())
                        + " has been placed successfully.",
                NotificationType.ORDER
        );

        return orderMapper.toResponse(order);
    }

    // ================= GET ORDERS BY USER =================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(UUID userId) {

        List<Order> orders = orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    // ================= GET ALL ORDERS =================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    // ================= MARK DELIVERED =================

    @Override
    public OrderResponse markAsDelivered(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equalsIgnoreCase("confirmed")) {
            throw new RuntimeException(
                    "Only confirmed orders can be delivered");
        }

        order.setStatus("delivered");

        orderRepository.save(order);

        notificationService.sendNotification(
                order.getUser().getId(),
                "Order Is On The Way",
                "Your order " + OrderCode.generate(order.getId())
                        + " is being delivered.",
                NotificationType.ORDER
        );

        return orderMapper.toResponse(order);
    }

    // ================= COMPLETE ORDER =================

    @Override
    public OrderResponse completeOrder(UUID orderId, UUID userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not allowed to update this order");
        }

        if (!order.getStatus().equalsIgnoreCase("delivered")) {
            throw new RuntimeException(
                    "Only delivered orders can be completed");
        }

        order.setStatus("completed");

        Payment payment = paymentRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("completed");

        paymentRepository.save(payment);

        orderRepository.save(order);

        notificationService.sendNotification(
                userId,
                "Order Completed",
                "Your order " + OrderCode.generate(order.getId())
                        + " has been completed successfully.",
                NotificationType.ORDER
        );

        return orderMapper.toResponse(order);
    }

    

    // ================= CANCEL ORDER =================

    @Override
    public OrderResponse cancelOrder(UUID orderId, UUID userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not allowed to cancel this order");
        }

        if (!order.getStatus().equalsIgnoreCase("pending")) {
            throw new RuntimeException(
                    "Only pending orders can be cancelled");
        }

        order.setStatus("cancelled");

        Payment payment = paymentRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String paymentCode = payment.getPaymentMethod().getCode();

        if (paymentCode == null) {
            throw new RuntimeException("Payment method code is null");
        }

        paymentCode = paymentCode.toLowerCase();

        /*
         * COD
         */
        if (paymentCode.equals("cod")) {

            payment.setStatus("cancelled");
        }

        /*
         * VNPAY
         */
        else if (paymentCode.equals("vnpay")) {

            /*
             * Đã thanh toán
             */
            if (payment.getStatus().equalsIgnoreCase("success")) {

                // TODO:
                // call VNPay refund API

                payment.setStatus("refunded");
            }

            /*
             * Chưa thanh toán
             */
            else {

                payment.setStatus("cancelled");
            }
        }

        paymentRepository.save(payment);

        orderRepository.save(order);

        notificationService.sendNotification(
                userId,
                "Order Cancelled",
                "Your order " + OrderCode.generate(order.getId())
                        + " has been cancelled.",
                NotificationType.ORDER
        );

        return orderMapper.toResponse(order);
    }

    @Override
@Transactional(readOnly = true)
public List<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

    return orderRepository
            .findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate)
            .stream()
            .map(orderMapper::toResponse)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<OrderResponse> getOrdersByUserAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {

    return orderRepository
            .findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate)
            .stream()
            .map(orderMapper::toResponse)
            .toList();
}
}