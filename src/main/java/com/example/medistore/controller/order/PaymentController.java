package com.example.medistore.controller.order;

import com.example.medistore.dto.order.CreatePaymentRequest;
import com.example.medistore.dto.order.PaymentResponse;
import com.example.medistore.dto.order.TransactionResponse;
import com.example.medistore.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public PaymentResponse createPayment(
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest) throws Exception {

        return paymentService.createPayment(
                request,
                httpRequest);
    }

    @GetMapping("/history/{orderId}")
    public List<PaymentResponse> history(
            @PathVariable UUID orderId) {

        return paymentService.history(orderId);
    }

    @GetMapping("/all-history")
    public List<TransactionResponse> getAllHistory() {

        return paymentService.getAllHistory();
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIpn(
            @RequestParam Map<String, String> params) {

        String responseCode = params.get("vnp_ResponseCode");

        String txnRef = params.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {

            paymentService.paymentSuccess(txnRef);
        }

        return ResponseEntity.ok(
                Map.of(
                        "RspCode", "00",
                        "Message", "Confirm Success"));
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn() {

        return "Payment success";
    }

    @PostMapping("/success/{txnRef}")
    public ResponseEntity<?> paymentSuccess(
            @PathVariable String txnRef) {

        paymentService.paymentSuccess(txnRef);

        return ResponseEntity.ok().build();
    }
}