package com.example.medistore.util;

import java.util.UUID;

public class OrderCode {

    private OrderCode() {}

    public static String generate(UUID orderId) {

        String id = orderId.toString();

        String last8Chars = id.substring(id.length() - 8);

        return "#" + last8Chars;
    }
}
