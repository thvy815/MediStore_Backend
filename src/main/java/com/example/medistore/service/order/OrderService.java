package com.example.medistore.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import lombok.RequiredArgsConstructor;

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
        private final DeliveryMethodRepository deliveryMethodRepository;
        private final PaymentMethodRepository paymentMethodRepository;
        private final PaymentRepository paymentRepository;
        private final CartItemRepository cartItemRepository;

        private final VoucherService voucherService;
        private final OrderVoucherRepository orderVoucherRepository;

        // ================= CREATE ORDER =================
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

                // ================= TOTAL = PRODUCT + SHIPPING - VOUCHER =================
                BigDecimal shippingFee = order.getShippingFee() != null
                                ? order.getShippingFee()
                                : BigDecimal.ZERO;

                BigDecimal orderAmountBeforeDiscount = productAmount.add(shippingFee);

                BigDecimal discountAmount = BigDecimal.ZERO;
                Voucher appliedVoucher = null;
                System.out.println("===== DEBUG ORDER =====");
                System.out.println("Voucher code: " + request.getVoucherCode());
                System.out.println("Before discount: " + orderAmountBeforeDiscount);

                if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {

                        appliedVoucher = voucherService.validateVoucher(
                                        request.getVoucherCode(),
                                        orderAmountBeforeDiscount,
                                        user.getId());

                        discountAmount = voucherService.calculateDiscount(
                                        appliedVoucher,
                                        productAmount,
                                        shippingFee);
                        System.out.println("Applied voucher: " + appliedVoucher.getCode());
                        System.out.println("Discount amount: " + discountAmount);
                }

                BigDecimal finalAmount = orderAmountBeforeDiscount.subtract(discountAmount);

                if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                        finalAmount = BigDecimal.ZERO;
                }

                // totalAmount lưu số tiền cuối cùng cần trả
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

                System.out.println("===== REMOVE CART ITEMS =====");

                for (CreateOrderRequest.ItemRequest item : request.getItems()) {
                        System.out.println("cartItemId = " + item.getCartItemId());

                        if (item.getCartItemId() != null) {
                                cartItemRepository.deleteById(item.getCartItemId());
                                System.out.println("Deleted cart item: " + item.getCartItemId());
                        }
                }

                return mapToResponse(order);
        }

        // ================= GET ORDERS BY USER =================
        @Transactional(readOnly = true)
        public List<OrderResponse> getOrdersByUser(UUID userId) {

                List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

                return orders.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        // ================= GET ALL ORDERS - ADMIN =================
        @Transactional(readOnly = true)
        public List<OrderResponse> getAllOrders() {
                return orderRepository.findAllByOrderByCreatedAtDesc()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        // ================= ADMIN MARK DELIVERED =================
        public OrderResponse markAsDelivered(UUID orderId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                if (!order.getStatus().equalsIgnoreCase("pending")) {
                        throw new RuntimeException("Only pending orders can be delivered");
                }

                order.setStatus("delivered");
                orderRepository.save(order);

                return mapToResponse(order);
        }

        // ================= CUSTOMER COMPLETE ORDER =================
        public OrderResponse completeOrder(UUID orderId, UUID userId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                if (!order.getUser().getId().equals(userId)) {
                        throw new RuntimeException("You are not allowed to update this order");
                }

                if (!order.getStatus().equalsIgnoreCase("delivered")) {
                        throw new RuntimeException("Only delivered orders can be completed");
                }

                order.setStatus("completed");

                Payment payment = paymentRepository
                                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                                .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setStatus("completed");

                paymentRepository.save(payment);

                orderRepository.save(order);

                return mapToResponse(order);
        }

        // ================= CUSTOMER CANCEL ORDER =================
        public OrderResponse cancelOrder(UUID orderId, UUID userId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                if (!order.getUser().getId().equals(userId)) {
                        throw new RuntimeException("You are not allowed to cancel this order");
                }

                if (!order.getStatus().equalsIgnoreCase("pending")) {
                        throw new RuntimeException("Only pending orders can be cancelled");
                }

                order.setStatus("cancelled");

                Payment payment = paymentRepository
                                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                                .orElseThrow(() -> new RuntimeException("Payment not found"));

                String paymentCode = payment.getPaymentMethod()
                                .getCode();

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

                return mapToResponse(order);
        }

        // ================= MAPPING =================
        private OrderResponse mapToResponse(Order order) {

                return OrderResponse.builder()
                                .orderId(order.getId())
                                .status(order.getStatus())
                                .totalAmount(order.getTotalAmount())
                                .shippingName(order.getShippingName())
                                .shippingPhone(order.getShippingPhone())
                                .shippingAddress(order.getShippingAddress())
                                .createdAt(order.getCreatedAt())
                                .deliveryMethodId(order.getDeliveryMethod() != null ? order.getDeliveryMethod().getId()
                                                : null)
                                .deliveryMethodName(
                                                order.getDeliveryMethod() != null ? order.getDeliveryMethod().getName()
                                                                : null)
                                .shippingFee(order.getShippingFee())
                                .items(
                                                order.getItems().stream().map(item -> OrderResponse.ItemResponse
                                                                .builder()
                                                                .orderItemId(item.getId())
                                                                .productId(item.getProduct().getId())
                                                                .productName(item.getProduct().getName())
                                                                .unitId(item.getProductUnit().getId())
                                                                .unitName(item.getProductUnit().getUnit().getName())
                                                                .quantity(item.getQuantity())
                                                                .unitPrice(item.getUnitPrice())
                                                                .build()).toList())
                                .build();
        }
}