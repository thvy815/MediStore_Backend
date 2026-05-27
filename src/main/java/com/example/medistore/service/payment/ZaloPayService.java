package com.example.medistore.service.payment;

import com.example.medistore.entity.order.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ZaloPayService {

    @Value("${zalopay.app-id}")
    private String appId;

    @Value("${zalopay.key1}")
    private String key1;

    @Value("${zalopay.create-order-url}")
    private String createOrderUrl;

    public String createPaymentUrl(
            Payment payment) throws Exception {

        String appTransId = payment.getTransactionRef();

        payment.setTransactionRef(appTransId);

        String appUser = payment.getOrder()
                .getUser()
                .getId()
                .toString();

        long appTime = System.currentTimeMillis();

        long amount = payment.getAmount()
                .longValue();

        String embedData = "{}";

        String item = "[]";

        String description = "Thanh toan don hang "
                + payment.getOrder().getId();

        String bankCode = "";

        // IMPORTANT: đúng thứ tự ký của ZaloPay
        String data = appId + "|"
                + appTransId + "|"
                + appUser + "|"
                + amount + "|"
                + appTime + "|"
                + embedData + "|"
                + item;

        String mac = HmacUtils.hmacSha256Hex(
                key1,
                data);

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("app_id",
                Integer.parseInt(appId));

        body.put("app_user",
                appUser);

        body.put("app_time",
                appTime);

        body.put("amount",
                amount);

        body.put("app_trans_id",
                appTransId);

        body.put("embed_data",
                embedData);

        body.put("item",
                item);

        body.put("description",
                description);

        body.put("bank_code",
                bankCode);

        body.put(
                "callback_url",
                "https://medistore-backend-i0de.onrender.com/api/payments/zalopay/callback");

        body.put(
                "redirect_url",
                "https://medi-store-frontend-two.vercel.app");

        body.put("mac",
                mac);

        ObjectMapper mapper = new ObjectMapper();

        URL url = new URL(createOrderUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty(
                "Content-Type",
                "application/json");

        conn.setDoOutput(true);

        String jsonBody = mapper.writeValueAsString(body);

        System.out.println(
                "======= REQUEST BODY =======");
        System.out.println(jsonBody);
        System.out.println(
                "======= STRING TO SIGN =======");
        System.out.println(data);
        System.out.println(
                "======= MAC =======");
        System.out.println(mac);

        try (OutputStream os = conn.getOutputStream()) {

            os.write(jsonBody.getBytes());
        }

        Map<String, Object> response = mapper.readValue(
                conn.getInputStream(),
                Map.class);

        System.out.println(
                "=========== ZALOPAY DEBUG ===========");
        System.out.println(response);
        System.out.println(
                "=====================================");

        Integer returnCode = (Integer) response.get(
                "return_code");

        if (returnCode == null
                || returnCode != 1) {

            throw new RuntimeException(
                    "ZaloPay Error: "
                            + response.get(
                                    "return_message"));
        }

        return (String) response.get("order_url");
    }
}