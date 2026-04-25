package com.computershop.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.computershop.service.impl.MomoSandboxService;
import com.computershop.service.impl.OrderServiceImpl;

import jakarta.servlet.http.HttpSession;

/**
 * Controller xử lý callback/return URL từ MoMo Sandbox.
 *
 * Có 2 loại callback:
 * 1. redirectUrl (/payment/momo/return) - User được redirect về sau khi thanh toán (trình duyệt)
 * 2. ipnUrl     (/payment/momo/notify)  - MoMo gửi IPN để cập nhật trạng thái (server-to-server)
 */
@Controller
@RequestMapping("/payment/momo")
public class MomoPaymentController {

    private static final Logger log = LoggerFactory.getLogger(MomoPaymentController.class);

    @Autowired
    private MomoSandboxService momoSandboxService;

    @Autowired
    private OrderServiceImpl orderService;

    /**
     * Redirect URL - MoMo redirect người dùng về đây sau khi thanh toán.
     * Tham số được gửi dưới dạng query string GET.
     *
     * resultCode = 0  → Thanh toán thành công
     * resultCode != 0 → Thanh toán thất bại hoặc bị huỷ
     */
    @GetMapping("/return")
    public String paymentReturn(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false, defaultValue = "-1") int resultCode,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String transId,
            @RequestParam(required = false) String amount,
            HttpSession session,
            Model model) {

        log.info("[MoMo Return] orderId={}, resultCode={}, message={}, transId={}", 
                  orderId, resultCode, message, transId);

        // Parse original orderId from momo orderId format (partnerCode_orderId_timestamp)
        Integer originalOrderId = null;
        if (orderId != null) {
            originalOrderId = momoSandboxService.parseOriginalOrderId(orderId);
        }

        if (originalOrderId == null) {
            model.addAttribute("error", "Order not found");
            model.addAttribute("resultCode", resultCode);
            return "payment/momo-result";
        }

        if (resultCode == 0) {
            // Thanh toán thành công → cập nhật trạng thái đơn hàng
            try {
                orderService.updateOrderStatus(originalOrderId, "confirmed");
                log.info("[MoMo Return] Order {} updated to 'confirmed'", originalOrderId);
            } catch (Exception e) {
                log.warn("[MoMo Return] Failed to update order status: {}", e.getMessage());
            }

            model.addAttribute("success", true);
            model.addAttribute("message", "Payment successful!");
            model.addAttribute("orderId", originalOrderId);
            model.addAttribute("transId", transId);
            model.addAttribute("amount", amount);
        } else {
            // Thanh toán thất bại / huỷ → giữ trạng thái pending_payment để user thanh toán lại
            log.info("[MoMo Return] Payment failed/cancelled. ResultCode={}, message={}", resultCode, message);
            model.addAttribute("success", false);
            model.addAttribute("message", mapResultCode(resultCode));
            model.addAttribute("orderId", originalOrderId);
            model.addAttribute("resultCode", resultCode);
        }

        return "payment/momo-result";
    }

    /**
     * IPN URL (Instant Payment Notification) - MoMo gửi SERVER-TO-SERVER để xác nhận kết quả.
     * Đây là cách đáng tin cậy nhất để cập nhật trạng thái đơn hàng.
     *
     * Response phải là HTTP 200 với body không quan trọng.
     */
    @PostMapping("/notify")
    @ResponseBody
    public String paymentNotify(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false, defaultValue = "-1") int resultCode,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String transId,
            @RequestParam(required = false) String signature) {

        log.info("[MoMo IPN] orderId={}, resultCode={}, transId={}", orderId, resultCode, transId);

        // Parse original orderId
        Integer originalOrderId = null;
        if (orderId != null) {
            originalOrderId = momoSandboxService.parseOriginalOrderId(orderId);
        }

        if (originalOrderId == null) {
            log.warn("[MoMo IPN] Cannot parse original orderId from: {}", orderId);
            return "OK";
        }

        if (resultCode == 0) {
            try {
                orderService.updateOrderStatus(originalOrderId, "confirmed");
                log.info("[MoMo IPN] Order {} confirmed via IPN", originalOrderId);
            } catch (Exception e) {
                log.error("[MoMo IPN] Error updating order {}: {}", originalOrderId, e.getMessage());
            }
        } else {
            log.info("[MoMo IPN] Payment failed for order {}. Code={}, msg={}", originalOrderId, resultCode, message);
        }

        return "OK"; // MoMo expects HTTP 200 response
    }

    /**
     * Map MoMo result code to Vietnamese message.
     */
    private String mapResultCode(int code) {
        return switch (code) {
            case 1001 -> "Transaction failed: Insufficient funds";
            case 1002 -> "Transaction rejected by payment gateway";
            case 1003, 1004, 1005 -> "Transaction expired or cancelled";
            case 1006 -> "Transaction rejected (cancelled by user)";
            case 1007 -> "MoMo account does not exist";
            case 1010 -> "Two-factor authentication (OTP) failed";
            case 2019 -> "Payment method not supported";
            case 9000 -> "Transaction confirmed successfully";
            default   -> "Payment failed or cancelled (code: " + code + ")";
        };
    }
}
