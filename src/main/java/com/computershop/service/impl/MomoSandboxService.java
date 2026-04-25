package com.computershop.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MoMo Sandbox Payment Service.
 *
 * Sử dụng MoMo Test Environment (sandbox) để mô phỏng thanh toán trực tuyến.
 * Endpoint: https://test-payment.momo.vn/v2/gateway/api/create
 *
 * Credentials mặc định là của môi trường TEST. Thay bằng credentials thực khi deploy.
 *
 * Docs: https://developers.momo.vn/v3/
 */
@Service
public class MomoSandboxService {

    private static final Logger log = LoggerFactory.getLogger(MomoSandboxService.class);

    // ── MoMo Sandbox credentials (public test credentials) ───────────────────────
    @Value("${momo.sandbox.partner-code:MOMOBKUN20180529}")
    private String partnerCode;

    @Value("${momo.sandbox.access-key:klm05TvNBzhg7h7j}")
    private String accessKey;

    @Value("${momo.sandbox.secret-key:at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa}")
    private String secretKey;

    @Value("${momo.sandbox.api-endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String apiEndpoint;

    @Value("${momo.sandbox.redirect-url:http://localhost:2345/payment/momo/return}")
    private String redirectUrl;

    @Value("${momo.sandbox.ipn-url:http://localhost:2345/payment/momo/notify}")
    private String ipnUrl;

    @Value("${server.port:2345}")
    private String serverPort;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Tạo request thanh toán MoMo Sandbox và trả về payUrl để redirect người dùng.
     *
     * @param orderId     ID đơn hàng
     * @param amount      Số tiền VND
     * @param orderInfo   Mô tả đơn hàng
     * @return MomoPaymentResult chứa payUrl và orderId của MoMo
     */
    public MomoPaymentResult createPayment(Integer orderId, long amount, String orderInfo) {
        try {
            String momoOrderId = partnerCode + "_" + orderId + "_" + System.currentTimeMillis();
            String requestId   = UUID.randomUUID().toString();
            String extraData   = "";
            String requestType = "captureWallet";

            // Build HMAC-SHA256 signature
            // Thứ tự các field BẮT BUỘC theo tài liệu MoMo
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + momoOrderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(rawSignature, secretKey);

            // Build JSON request body
            String requestBody = objectMapper.writeValueAsString(new java.util.LinkedHashMap<String, Object>() {{
                put("partnerCode", partnerCode);
                put("accessKey", accessKey);
                put("requestId", requestId);
                put("amount", amount);
                put("orderId", momoOrderId);
                put("orderInfo", orderInfo);
                put("redirectUrl", redirectUrl);
                put("ipnUrl", ipnUrl);
                put("extraData", extraData);
                put("requestType", requestType);
                put("signature", signature);
                put("lang", "vi");
            }});

            log.info("[MoMo] Creating payment for orderId={}, amount={}", momoOrderId, amount);
            log.debug("[MoMo] Request: {}", requestBody);

            // Call MoMo API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            String responseBody = response.body();
            log.info("[MoMo] Response status={}, body={}", response.statusCode(), responseBody);

            JsonNode json = objectMapper.readTree(responseBody);

            int resultCode = json.path("resultCode").asInt(-1);
            String message = json.path("message").asText("Unknown error");

            if (resultCode == 0) {
                String payUrl = json.path("payUrl").asText();
                String deeplink = json.path("deeplink").asText("");
                log.info("[MoMo] Payment created successfully. payUrl={}", payUrl);
                return MomoPaymentResult.success(payUrl, deeplink, momoOrderId);
            } else {
                log.warn("[MoMo] Payment creation failed. resultCode={}, message={}", resultCode, message);
                return MomoPaymentResult.failure("MoMo error " + resultCode + ": " + message);
            }

        } catch (Exception e) {
            log.error("[MoMo] Error calling MoMo API", e);
            return MomoPaymentResult.failure("MoMo connection error: " + e.getMessage());
        }
    }

    /**
     * Xác thực chữ ký IPN/callback từ MoMo.
     *
     * @param rawSignatureData dữ liệu cần ký (build từ các tham số callback)
     * @param receivedSignature chữ ký từ MoMo gửi về
     * @return true nếu chữ ký hợp lệ
     */
    public boolean verifySignature(String rawSignatureData, String receivedSignature) {
        try {
            String computed = hmacSHA256(rawSignatureData, secretKey);
            return computed.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            log.error("[MoMo] Signature verification error", e);
            return false;
        }
    }

    /**
     * Parse orderId gốc từ MoMo orderId string (format: partnerCode_orderId_timestamp).
     */
    public Integer parseOriginalOrderId(String momoOrderId) {
        try {
            // Format: MOMOBKUN20180529_123_1700000000000
            String[] parts = momoOrderId.split("_");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            log.warn("[MoMo] Cannot parse orderId from: {}", momoOrderId);
        }
        return null;
    }

    // ── HMAC-SHA256 helper ────────────────────────────────────────────────────────

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // ── Inner result class ────────────────────────────────────────────────────────

    public static class MomoPaymentResult {
        private final boolean success;
        private final String payUrl;
        private final String deeplink;
        private final String momoOrderId;
        private final String errorMessage;

        private MomoPaymentResult(boolean success, String payUrl, String deeplink,
                                   String momoOrderId, String errorMessage) {
            this.success = success;
            this.payUrl = payUrl;
            this.deeplink = deeplink;
            this.momoOrderId = momoOrderId;
            this.errorMessage = errorMessage;
        }

        public static MomoPaymentResult success(String payUrl, String deeplink, String momoOrderId) {
            return new MomoPaymentResult(true, payUrl, deeplink, momoOrderId, null);
        }

        public static MomoPaymentResult failure(String errorMessage) {
            return new MomoPaymentResult(false, null, null, null, errorMessage);
        }

        public boolean isSuccess()      { return success; }
        public String getPayUrl()       { return payUrl; }
        public String getDeeplink()     { return deeplink; }
        public String getMomoOrderId()  { return momoOrderId; }
        public String getErrorMessage() { return errorMessage; }
    }

    // ── Legacy compat getters (used by old templates) ─────────────────────────────
    public String getAccountNumber() { return "Test Sandbox - Do not use real money"; }
    public String getAccountName()   { return "MoMo Sandbox Environment"; }
    public String getBankName()      { return "MoMo Test"; }
}
