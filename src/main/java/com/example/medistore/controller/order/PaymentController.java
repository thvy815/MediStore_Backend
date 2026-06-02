package com.example.medistore.controller.order;

import com.example.medistore.dto.order.CreatePaymentRequest;
import com.example.medistore.dto.order.PaymentResponse;
import com.example.medistore.dto.order.TransactionResponse;
import com.example.medistore.service.payment.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    /**
     * VNPay server callback (IPN)
     * Đây là endpoint QUAN TRỌNG để update DB
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIpn(
            @RequestParam Map<String, String> params) {

        try {

            System.out.println("===== VNPAY IPN =====");
            params.forEach((k, v) -> System.out.println(k + " = " + v));

            boolean valid = paymentService.verifyVNPaySignature(params);

            System.out.println("VALID SIGNATURE = " + valid);

            if (!valid) {

                return ResponseEntity.ok(
                        Map.of(
                                "RspCode", "97",
                                "Message", "Invalid signature"));
            }

            String responseCode = params.get("vnp_ResponseCode");

            String txnRef = params.get("vnp_TxnRef");

            System.out.println("txnRef = " + txnRef);
            System.out.println("responseCode = " + responseCode);

            // thanh toán thành công
            if ("00".equals(responseCode)) {

                paymentService.paymentSuccess(txnRef);

                System.out.println(
                        "PAYMENT UPDATED SUCCESS FROM IPN");
            }

            return ResponseEntity.ok(
                    Map.of(
                            "RspCode", "00",
                            "Message", "Confirm Success"));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.ok(
                    Map.of(
                            "RspCode", "99",
                            "Message", e.getMessage()));
        }
    }

    /**
     * User redirect sau khi thanh toán
     * Chỉ redirect FE, KHÔNG nên dùng để update DB
     */
    @GetMapping("/vnpay-return")
    public void vnpayReturn(
            @RequestParam Map<String, String> params,
            HttpServletResponse response)
            throws Exception {

        try {

            System.out.println("===== VNPAY RETURN =====");

            params.forEach((k, v) -> System.out.println(k + " = " + v));

            boolean valid = paymentService.verifyVNPaySignature(params);

            System.out.println("VALID = " + valid);

            String responseCode = params.get("vnp_ResponseCode");

            String txnRef = params.get("vnp_TxnRef");

            System.out.println("txnRef = " + txnRef);
            System.out.println("responseCode = " + responseCode);

            /**
             * Backup update
             * Nếu IPN fail thì return vẫn update
             */
            if (valid && "00".equals(responseCode)) {

                try {

                    paymentService.paymentSuccess(txnRef);

                    System.out.println(
                            "PAYMENT UPDATED SUCCESS FROM RETURN");

                } catch (Exception ex) {

                    System.out.println(
                            "UPDATE FAILED: "
                                    + ex.getMessage());
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        // redirect frontend
        response.sendRedirect(
                "https://medi-store-frontend-two.vercel.app");
    }

    @PostMapping("/success/{txnRef}")
    public ResponseEntity<?> paymentSuccess(
            @PathVariable String txnRef) {

        paymentService.paymentSuccess(txnRef);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/zalopay/create/{orderId}")
    public PaymentResponse createZaloPay(
            @PathVariable UUID orderId)
            throws Exception {

        return paymentService
                .createZaloPayment(orderId);
    }

    @PostMapping("/zalopay/callback")
    public ResponseEntity<?> callback(
            @RequestBody Map<String, Object> body) {

        try {

            System.out.println(
                    "===== ZALOPAY CALLBACK =====");

            System.out.println(body);

            String data = (String) body.get("data");

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> dataMap = mapper.readValue(data, Map.class);

            String appTransId = (String) dataMap.get("app_trans_id");

            System.out.println(
                    "SUCCESS PAYMENT: "
                            + appTransId);

            paymentService.paymentSuccess(
                    appTransId);

            return ResponseEntity.ok(
                    Map.of(
                            "return_code", 1,
                            "return_message", "success"));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.ok(
                    Map.of(
                            "return_code", -1,
                            "return_message", "fail"));
        }
    }

    @GetMapping("/ping")
    public String ping() {

        return "ok";
    }

    @PostMapping("/manual-success/{txnRef}")
    public ResponseEntity<?> manualSuccess(
            @PathVariable String txnRef) {

        try {

            paymentService.paymentSuccess(txnRef);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Payment updated successfully",
                            "updatedAt",
                            LocalDateTime.now()));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }
}