package com.computershop.controller.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.computershop.service.impl.OrderServiceImpl;
import com.computershop.service.impl.VNPayService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller xử lý callback/return URL từ VNPay.
 */
@Controller
@RequestMapping("/payment/vnpay")
public class VNPayController {

    private static final Logger log = LoggerFactory.getLogger(VNPayController.class);

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private OrderServiceImpl orderService;

    /**
     * URL Return: Trình duyệt redirect người dùng về đây sau khi thanh toán xong.
     */
    @GetMapping("/return")
    public String paymentReturn(HttpServletRequest request, Model model) {
        Map<String, String[]> requestParams = request.getParameterMap();
        
        // Xác thực chữ ký
        boolean isValidSignature = vnPayService.verifySignature(requestParams);
        
        // Mã kết quả thanh toán từ VNPay
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");

        log.info("[VNPay Return] vnp_TxnRef={}, vnp_ResponseCode={}", vnp_TxnRef, vnp_ResponseCode);

        // Parse orderId từ TxnRef (định dạng orderId_timestamp)
        Integer originalOrderId = null;
        if (vnp_TxnRef != null && vnp_TxnRef.contains("_")) {
            try {
                originalOrderId = Integer.parseInt(vnp_TxnRef.split("_")[0]);
            } catch (Exception e) {}
        }

        if (!isValidSignature) {
            model.addAttribute("success", false);
            model.addAttribute("error", "Invalid VNPay signature. Transaction rejected.");
            model.addAttribute("orderId", originalOrderId);
            return "payment/vnpay-result";
        }

        if ("00".equals(vnp_ResponseCode)) {
            // Thanh toán thành công
            if (originalOrderId != null) {
                try {
                    orderService.updateOrderStatus(originalOrderId, "confirmed");
                } catch (Exception e) {
                    log.warn("[VNPay Return] Cannot update order status: {}", e.getMessage());
                }
            }
            
            Double amountDisplay = 0.0;
            if (vnp_Amount != null) {
                amountDisplay = Double.parseDouble(vnp_Amount) / 100.0;
            }

            model.addAttribute("success", true);
            model.addAttribute("message", "Payment successful via VNPay!");
            model.addAttribute("orderId", originalOrderId);
            model.addAttribute("transId", vnp_TransactionNo);
            model.addAttribute("amount", amountDisplay);
        } else {
            // Thanh toán thất bại hoặc huỷ
            model.addAttribute("success", false);
            model.addAttribute("message", "Payment unsuccessful. Error code: " + vnp_ResponseCode);
            model.addAttribute("orderId", originalOrderId);
            model.addAttribute("resultCode", vnp_ResponseCode);
        }

        return "payment/vnpay-result";
    }
}
