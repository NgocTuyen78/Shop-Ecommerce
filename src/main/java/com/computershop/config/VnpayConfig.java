// VnpayConfig.java: Chứa cấu hình thông tin kết nối VNPay (tmnCode, hashSecret, url, returnUrl) lấy từ file cấu hình ứng dụng.
package com.computershop.config;

import java.util.Random;

import org.springframework.context.annotation.Configuration;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class VnpayConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    // public static String vnp_ReturnUrl = "http://localhost:2345/payment/vnpay/return";
    public static String vnp_ReturnUrl = System.getenv("VNPAY_RETURN_URL") != null 
    ? System.getenv("VNPAY_RETURN_URL") 
    : "http://localhost:2345/payment/vnpay/return";
    public static String vnp_TmnCode = "SEJJN2NR";
	public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String secretKey = "AFDSAMQDJ6MW97WPNW37NOI2K140WNGI";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    
    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
