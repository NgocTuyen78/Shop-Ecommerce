package com.computershop;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class MomoTest {
    public static void main(String[] args) throws Exception {
        String partnerCode = "MOMOBKUN20180529";
        String accessKey   = "klm05TvNBzhg7h7j";
        String secretKey   = "at67qH6mk8w5Y1nAyMoTkAmdZvfkApbm";
        String endpoint    = "https://test-payment.momo.vn/v2/gateway/api/create";
        
        long amount = 150000;
        String orderId = partnerCode + "_" + System.currentTimeMillis();
        String orderInfo = "Test order info";
        String redirectUrl = "http://localhost:2345/return";
        String ipnUrl = "http://localhost:2345/notify";
        String extraData = "";
        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";
        
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;
                
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        String signature = sb.toString();
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new java.util.LinkedHashMap<String, Object>() {{
            put("partnerCode", partnerCode);
            put("accessKey", accessKey);
            put("requestId", requestId);
            put("amount", amount);
            put("orderId", orderId);
            put("orderInfo", orderInfo);
            put("redirectUrl", redirectUrl);
            put("ipnUrl", ipnUrl);
            put("extraData", extraData);
            put("requestType", requestType);
            put("signature", signature);
            put("lang", "vi");
        }});
        
        System.out.println("Raw Signature: " + rawSignature);
        System.out.println("JSON Request: " + json);
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
                
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response: " + response.body());
    }
}
