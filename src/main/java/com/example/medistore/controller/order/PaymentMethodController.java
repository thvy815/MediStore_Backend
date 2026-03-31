package com.example.medistore.controller.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.medistore.dto.order.CreatePaymentMethodRequest;
import com.example.medistore.dto.order.PaymentMethodResponse;
import com.example.medistore.dto.order.UpdatePaymentMethodRequest;
import com.example.medistore.service.order.PaymentMethodService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // GET ALL
    @GetMapping
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public PaymentMethodResponse getPaymentMethodById(@PathVariable UUID id) {
        return paymentMethodService.getPaymentMethodById(id);
    }

    // CREATE
    @PostMapping
    public PaymentMethodResponse createPaymentMethod(@RequestBody CreatePaymentMethodRequest request) {
        return paymentMethodService.createPaymentMethod(request);
    }

    // UPDATE
    @PutMapping("/{id}")
    public PaymentMethodResponse updatePaymentMethod(@PathVariable UUID id, @RequestBody UpdatePaymentMethodRequest request) {
        return paymentMethodService.updatePaymentMethod(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deletePaymentMethod(@PathVariable UUID id) {
        paymentMethodService.deletePaymentMethod(id);
    }
}