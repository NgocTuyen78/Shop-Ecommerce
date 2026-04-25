package com.computershop.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/**
 * VNPay Sandbox Payment Service.
 * 
 * Sử dụng VNPay Sandbox để thanh toán thử nghiệm.
 * Các thông tin cài đặt mặc định bên dưới là public test credentials.
 */
@Service
public class VNPayService {

    // Credentials mặc định của VNPay Sandbox (Public Sandbox)
    private static final String VNP_TMN_CODE = "CGXZLS0Z";
    private static final String VNP_HASH_SECRET = "XNBCJFAKAZQSGTARRLGCHVGHGFNUDZVJ";
    private static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURN_URL = "http://localhost:2345/payment/vnpay/return";

    /**
     * Helper encode URL theo chuẩn RFC 3986 (VNPay yêu cầu dấu cách = %20, không phải +)
     */
    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (Exception ex) {
            return "";
        }
    }

    public String createPaymentUrl(Integer orderId, long amount, String orderInfo, String ipAddr) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = String.valueOf(orderId) + "_" + System.currentTimeMillis();

        if (ipAddr == null || ipAddr.contains(":")) {
            ipAddr = "127.0.0.1"; // Default to IPv4 if IPv6 or null
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNP_TMN_CODE);
        // Số tiền vnpay yêu cầu nhân với 100
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNP_RETURN_URL);
        vnp_Params.put("vnp_IpAddr", ipAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build data to hash and query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(encodeValue(fieldValue));
                // Build query
                query.append(encodeValue(fieldName));
                query.append('=');
                query.append(encodeValue(fieldValue));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        
        return VNP_PAY_URL + "?" + queryUrl;
    }

    public boolean verifySignature(Map<String, String[]> requestParams) {
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue()[0];
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = fields.get("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        // Tạo lại signature từ tham số nhận về
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(encodeValue(fieldValue));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String signValue = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
        return signValue.equals(vnp_SecureHash);
    }

    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
