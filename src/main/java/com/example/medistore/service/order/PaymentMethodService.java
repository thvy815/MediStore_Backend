package com.example.medistore.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medistore.dto.order.CreatePaymentMethodRequest;
import com.example.medistore.dto.order.PaymentMethodResponse;
import com.example.medistore.dto.order.UpdatePaymentMethodRequest;
import com.example.medistore.entity.order.PaymentMethod;
import com.example.medistore.repository.order.PaymentMethodRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    // GET ALL
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        return paymentMethodRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    public PaymentMethodResponse getPaymentMethodById(UUID id) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        return mapToResponse(method);
    }

    // CREATE
    public PaymentMethodResponse createPaymentMethod(CreatePaymentMethodRequest request) {
        PaymentMethod method = PaymentMethod.builder()
                .code(request.getCode())
                .name(request.getName())
                .isActive(request.getIsActive())
                .build();
        method = paymentMethodRepository.save(method);
        return mapToResponse(method);
    }

    // UPDATE
    public PaymentMethodResponse updatePaymentMethod(UUID id, UpdatePaymentMethodRequest request) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        method.setCode(request.getCode());
        method.setName(request.getName());
        method.setIsActive(request.getIsActive());
        method = paymentMethodRepository.save(method);
        return mapToResponse(method);
    }

    // DELETE
    public void deletePaymentMethod(UUID id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new RuntimeException("Payment method not found");
        }
        paymentMethodRepository.deleteById(id);
    }

    // MAPPING
    private PaymentMethodResponse mapToResponse(PaymentMethod method) {
        return PaymentMethodResponse.builder()
                .id(method.getId())
                .code(method.getCode())
                .name(method.getName())
                .isActive(method.getIsActive())
                .build();
    }
}