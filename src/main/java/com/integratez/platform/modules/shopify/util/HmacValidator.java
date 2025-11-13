package com.integratez.platform.modules.shopify.util;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class HmacValidator {

    public static boolean isValidHmac(Map<String, String> queryParams, String secret) {
        try {
            String hmac = queryParams.get("hmac");
            Map<String, String> sortedParams = new TreeMap<>();
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (!entry.getKey().equals("hmac") && !entry.getKey().equals("signature")) {
                    sortedParams.put(entry.getKey(), entry.getValue());
                }
            }

            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (data.length() > 0) data.append("&");
                data.append(entry.getKey()).append("=").append(entry.getValue());
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(data.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();
            for (byte b : rawHmac) hash.append(String.format("%02x", b));
            return hmac.equalsIgnoreCase(hash.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

