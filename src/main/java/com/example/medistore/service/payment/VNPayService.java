package com.example.medistore.service.payment;

import com.example.medistore.entity.order.Payment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

        @Value("${vnpay.tmn-code}")
        private String tmnCode;

        @Value("${vnpay.hash-secret}")
        private String hashSecret;

        @Value("${vnpay.pay-url}")
        private String payUrl;

        @Value("${vnpay.return-url}")
        private String returnUrl;

        public String createPaymentUrl(
                        Payment payment,
                        HttpServletRequest request) throws Exception {

                // Mã giao dịch unique
                String txnRef = payment.getTransactionRef();

                // Timezone Việt Nam
                Calendar calendar = Calendar.getInstance(
                                TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

                // Ngày tạo
                String createDate = new SimpleDateFormat("yyyyMMddHHmmss")
                                .format(calendar.getTime());

                // Hết hạn sau 15 phút
                calendar.add(Calendar.MINUTE, 15);

                String expireDate = new SimpleDateFormat("yyyyMMddHHmmss")
                                .format(calendar.getTime());

                // Amount * 100
                String amount = payment.getAmount()
                                .multiply(BigDecimal.valueOf(100))
                                .toBigInteger()
                                .toString();

                // Params VNPay
                Map<String, String> params = new TreeMap<>();

                params.put("vnp_Version", "2.1.0");
                params.put("vnp_Command", "pay");

                params.put("vnp_TmnCode", tmnCode);

                params.put("vnp_Amount", amount);

                params.put("vnp_CurrCode", "VND");

                params.put("vnp_TxnRef", txnRef);

                params.put("vnp_OrderInfo", "Thanh toan don hang");

                params.put("vnp_OrderType", "other");

                params.put("vnp_Locale", "vn");

                params.put("vnp_ReturnUrl", returnUrl);

                params.put("vnp_IpAddr", getIpAddress(request));

                params.put("vnp_CreateDate", createDate);

                params.put("vnp_ExpireDate", expireDate);

                // Build hashData + query
                StringBuilder hashData = new StringBuilder();

                StringBuilder query = new StringBuilder();

                Iterator<Map.Entry<String, String>> itr = params.entrySet().iterator();

                while (itr.hasNext()) {

                        Map.Entry<String, String> entry = itr.next();

                        String fieldName = entry.getKey();

                        String fieldValue = entry.getValue();

                        if (fieldValue != null && !fieldValue.isEmpty()) {

                                String encodedValue = URLEncoder.encode(
                                                fieldValue,
                                                StandardCharsets.US_ASCII.toString());

                                // HASH DATA
                                hashData.append(fieldName)
                                                .append("=")
                                                .append(encodedValue);

                                // QUERY
                                query.append(fieldName)
                                                .append("=")
                                                .append(encodedValue);

                                if (itr.hasNext()) {
                                        hashData.append("&");
                                        query.append("&");
                                }
                        }
                }

                // Tạo secure hash
                String secureHash = hmacSHA512(
                                hashSecret,
                                hashData.toString());

                // Add secure hash
                query.append("&vnp_SecureHash=")
                                .append(secureHash);

                // URL thanh toán
                String paymentUrl = payUrl + "?" + query;

                // DEBUG
                System.out.println("=========== VNPAY DEBUG ===========");
                System.out.println("HASH DATA:");
                System.out.println(hashData);

                System.out.println("SECURE HASH:");
                System.out.println(secureHash);

                System.out.println("PAYMENT URL:");
                System.out.println(paymentUrl);

                System.out.println("===================================");

                return paymentUrl;
        }

        private String hmacSHA512(
                        String key,
                        String data) throws Exception {

                Mac hmac512 = Mac.getInstance("HmacSHA512");

                SecretKeySpec secretKeySpec = new SecretKeySpec(
                                key.getBytes(StandardCharsets.UTF_8),
                                "HmacSHA512");

                hmac512.init(secretKeySpec);

                byte[] bytes = hmac512.doFinal(
                                data.getBytes(StandardCharsets.UTF_8));

                StringBuilder hash = new StringBuilder();

                for (byte b : bytes) {
                        hash.append(String.format("%02x", b));
                }

                return hash.toString();
        }

        private String getIpAddress(HttpServletRequest request) {

                String ipAddress = request.getHeader("X-FORWARDED-FOR");

                if (ipAddress == null || ipAddress.isEmpty()) {
                        ipAddress = request.getRemoteAddr();
                }

                if (ipAddress == null || ipAddress.isEmpty()) {
                        ipAddress = "127.0.0.1";
                }

                return ipAddress;
        }
}