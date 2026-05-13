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

    @Value("${vnpay.ipn-url}")
    private String ipnUrl;

    public String createPaymentUrl(
            Payment payment,
            HttpServletRequest request) throws Exception {

        // Mã giao dịch
        String txnRef = String.valueOf(System.currentTimeMillis());

        // Timezone VN
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        String createDate = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(calendar.getTime());

        calendar.add(Calendar.MINUTE, 15);

        String expireDate = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(calendar.getTime());

        // Params
        Map<String, String> params = new TreeMap<>();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");

        params.put("vnp_TmnCode", tmnCode);

        // amount * 100
        String amount = payment.getAmount()
                .multiply(new BigDecimal(100))
                .toBigInteger()
                .toString();

        params.put("vnp_Amount", amount);

        params.put("vnp_CurrCode", "VND");

        params.put("vnp_TxnRef", txnRef);

        params.put("vnp_OrderInfo", "Thanh toan don hang");

        params.put("vnp_OrderType", "other");

        params.put("vnp_Locale", "vn");

        params.put("vnp_ReturnUrl", returnUrl);

        // IP thật
        params.put("vnp_IpAddr", getIpAddress(request));

        params.put("vnp_CreateDate", createDate);

        params.put("vnp_ExpireDate", expireDate);

        // Build query + hashData
        List<String> fieldNames = new ArrayList<>(params.keySet());

        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {

            String fieldName = fieldNames.get(i);

            String fieldValue = params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {

                String encodedValue = URLEncoder.encode(
                        fieldValue,
                        StandardCharsets.US_ASCII);

                // HASH DATA
                hashData.append(fieldName)
                        .append("=")
                        .append(encodedValue);

                // QUERY
                query.append(fieldName)
                        .append("=")
                        .append(encodedValue);

                if (i < fieldNames.size() - 1) {
                    query.append("&");
                    hashData.append("&");
                }
            }
        }

        // Create secure hash
        String secureHash = hmacSHA512(
                hashSecret,
                hashData.toString());

        // Add hash type
        query.append("&vnp_SecureHashType=HmacSHA512");

        // Add secure hash
        query.append("&vnp_SecureHash=")
                .append(secureHash);

        String paymentUrl = payUrl + "?" + query;

        // DEBUG
        System.out.println("=========== VNPAY DEBUG ===========");
        System.out.println("HASH DATA: " + hashData);
        System.out.println("SECURE HASH: " + secureHash);
        System.out.println("PAYMENT URL: " + paymentUrl);
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