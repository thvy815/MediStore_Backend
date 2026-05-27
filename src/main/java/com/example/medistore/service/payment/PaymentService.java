package com.example.medistore.service.payment;

import com.example.medistore.dto.order.CreatePaymentRequest;
import com.example.medistore.dto.order.PaymentResponse;
import com.example.medistore.dto.order.TransactionResponse;
import com.example.medistore.entity.order.Order;
import com.example.medistore.entity.order.Payment;
import com.example.medistore.enums.NotificationType;
import com.example.medistore.repository.order.OrderRepository;
import com.example.medistore.repository.order.PaymentRepository;
import com.example.medistore.service.user.NotificationService;
import com.example.medistore.util.OrderCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

        private final PaymentRepository paymentRepository;

        private final NotificationService notificationService;

        private final OrderRepository orderRepository;

        private final VNPayService vnPayService;

        private final ZaloPayService zaloPayService;

        public PaymentResponse createPayment(
                        CreatePaymentRequest request,
                        HttpServletRequest httpRequest) throws Exception {

                Order order = orderRepository
                                .findById(request.getOrderId())
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                Payment payment = paymentRepository
                                .findTopByOrderIdOrderByCreatedAtDesc(order.getId())
                                .orElseThrow(() -> new RuntimeException("Payment not found"));

                String txnRef = UUID.randomUUID().toString();

                payment.setTransactionRef(txnRef);

                payment.setAmount(
                                BigDecimal.valueOf(order.getTotalAmount()));

                payment.setStatus("pending");

                String paymentUrl = vnPayService.createPaymentUrl(
                                payment,
                                httpRequest);

                payment.setPaymentUrl(paymentUrl);

                payment = paymentRepository.save(payment);

                return map(payment);
        }

        public void paymentSuccess(
                        String transactionRef) {

                System.out.println("txnRef = " + transactionRef);
                Payment payment = paymentRepository
                                .findByTransactionRef(transactionRef)
                                .orElseThrow(() -> new RuntimeException("NOT FOUND"));

                payment.setStatus("success");

                payment.setPaidAt(LocalDateTime.now());

                Order order = payment.getOrder();

                order.setStatus("confirmed"); // confirmed -> (preparing) --> delivered

                orderRepository.save(order);

                notificationService.sendNotification(
                                order.getUser().getId(),
                                "Payment Successful",
                                "Your payment for order " + OrderCode.generate(order.getId())
                                                + " was completed successfully.",
                                NotificationType.PAYMENT);

                paymentRepository.save(payment);
        }

        public List<PaymentResponse> history(
                        UUID orderId) {

                return paymentRepository
                                .findByOrderId(orderId)
                                .stream()
                                .map(this::map)
                                .toList();
        }

        public List<TransactionResponse> getAllHistory() {

                return paymentRepository
                                .findAll()
                                .stream()
                                .map(this::mapTransaction)
                                .toList();
        }

        private PaymentResponse map(
                        Payment payment) {

                return PaymentResponse.builder()
                                .id(payment.getId())
                                .amount(payment.getAmount())
                                .status(payment.getStatus())
                                .paymentUrl(payment.getPaymentUrl())
                                .transactionRef(payment.getTransactionRef())
                                .paymentMethod(
                                                payment.getPaymentMethod().getName())
                                .createdAt(payment.getCreatedAt())
                                .build();
        }

        private TransactionResponse mapTransaction(
                        Payment payment) {

                return TransactionResponse.builder()
                                .id(payment.getId())
                                .customerName(
                                                payment.getOrder()
                                                                .getUser()
                                                                .getFullName())
                                .amount(payment.getAmount())
                                .status(payment.getStatus())
                                .transactionRef(payment.getTransactionRef())
                                .paymentMethod(payment.getPaymentMethod().getName())
                                .createdAt(payment.getCreatedAt())
                                .build();
        }

        public PaymentResponse createZaloPayment(
                        UUID orderId)
                        throws Exception {

                Order order = orderRepository
                                .findById(orderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Order not found"));

                Payment payment = paymentRepository
                                .findTopByOrderIdOrderByCreatedAtDesc(
                                                order.getId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Payment not found"));

                String txnRef = new java.text.SimpleDateFormat("yyMMdd")
                                .format(new java.util.Date())
                                + "_"
                                + System.currentTimeMillis();

                payment.setTransactionRef(
                                txnRef);

                payment.setAmount(
                                BigDecimal.valueOf(
                                                order.getTotalAmount()));

                payment.setStatus(
                                "pending");

                String paymentUrl = zaloPayService
                                .createPaymentUrl(
                                                payment);

                payment.setPaymentUrl(
                                paymentUrl);

                payment = paymentRepository
                                .save(payment);

                return map(payment);
        }
}