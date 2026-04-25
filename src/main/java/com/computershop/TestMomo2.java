import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TestMomo2 {
    public static void main(String[] args) throws Exception {
        String partnerCode = "MOMO";
        String accessKey = "F8BBA842ECF85";
        String secretKey = "K951B6PE1waPeJi46XkR";
        String apiEndpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        String redirectUrl = "http://localhost:2345/payment/momo/return";
        String ipnUrl = "http://localhost:2345/payment/momo/notify";
        
        long amount = 17000;
        String momoOrderId = partnerCode + "_" + 124 + "_" + System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        String extraData = "";
        String requestType = "captureWallet";
        String orderInfo = "Payment for order test";
        
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

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        String signature = sb.toString();

        String requestBody = "{\"partnerCode\":\"" + partnerCode + "\",\"accessKey\":\"" + accessKey + "\",\"requestId\":\"" + requestId + "\",\"amount\":" + amount + ",\"orderId\":\"" + momoOrderId + "\",\"orderInfo\":\"" + orderInfo + "\",\"redirectUrl\":\"" + redirectUrl + "\",\"ipnUrl\":\"" + ipnUrl + "\",\"extraData\":\"" + extraData + "\",\"requestType\":\"" + requestType + "\",\"signature\":\"" + signature + "\",\"lang\":\"vi\"}";
        
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response: " + response.body());
    }
}
