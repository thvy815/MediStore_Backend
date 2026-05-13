package com.example.medistore.service.payment;

import com.example.medistore.dto.order.CreatePaymentRequest;
import com.example.medistore.dto.order.PaymentResponse;
import com.example.medistore.entity.order.Order;
import com.example.medistore.entity.order.Payment;
import com.example.medistore.entity.order.PaymentMethod;
import com.example.medistore.repository.order.OrderRepository;
import com.example.medistore.repository.order.PaymentMethodRepository;
import com.example.medistore.repository.order.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMethodRepository paymentMethodRepository;

    private final OrderRepository orderRepository;

    private final VNPayService vnPayService;

    public PaymentResponse createPayment(
            CreatePaymentRequest request,
            HttpServletRequest httpRequest) throws Exception {

        PaymentMethod method = paymentMethodRepository
                .findById(request.getPaymentMethodId())
                .orElseThrow();

        Order order = orderRepository
                .findById(request.getOrderId())
                .orElseThrow();

        // ONE transaction ref ONLY
        String txnRef = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .status("pending")
                .transactionRef(txnRef)
                .paymentMethod(method)
                .order(order)
                .build();

        payment = paymentRepository.save(payment);

        String paymentUrl = vnPayService.createPaymentUrl(
                payment,
                httpRequest);

        payment.setPaymentUrl(paymentUrl);

        payment = paymentRepository.save(payment);

        return map(payment);
    }

    public void paymentSuccess(
            String transactionRef) {

        Payment payment = paymentRepository
                .findByTransactionRef(transactionRef)
                .orElseThrow();

        payment.setStatus("success");

        payment.setPaidAt(LocalDateTime.now());

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
}