package com.computershop.controller.web;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
     * URL Return: Trình duyệt redirect người dùng về đây sau khi thanh toán xong từ VNPay.
     */
    @GetMapping("/return")
    public String paymentReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Map<String, String[]> requestParams = request.getParameterMap();
        
        // 1. Xác thực chữ ký bảo mật
        boolean isValidSignature = vnPayService.verifySignature(requestParams);
        // THÊM DÒNG NÀY ĐỂ DEBUG
        log.info("Ket qua kiem tra chu ky: {}", isValidSignature);

        // 2. Lấy các thông số cần thiết
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef"); // Định dạng: orderId_random

        log.info("[VNPay Return] vnp_TxnRef={}, vnp_ResponseCode={}", vnp_TxnRef, vnp_ResponseCode);

        // 3. Parse orderId từ TxnRef
        Integer originalOrderId = null;
        if (vnp_TxnRef != null && vnp_TxnRef.contains("_")) {
            try {
                originalOrderId = Integer.parseInt(vnp_TxnRef.split("_")[0]);
            } catch (Exception e) {
                log.error("Không thể parse OrderId từ TxnRef: {}", vnp_TxnRef);
            }
        }

        // 4. Kiểm tra chữ ký
        if (!isValidSignature) {
            log.error("Chữ ký VNPay không hợp lệ!");
            redirectAttributes.addFlashAttribute("error", "Giao dịch không hợp lệ (Sai chữ ký).");
            return "redirect:/user/orders/" + (originalOrderId != null ? originalOrderId : "");
        }

        // 5. Xử lý kết quả thanh toán
        if ("00".equals(vnp_ResponseCode)) {
            // THANH TOÁN THÀNH CÔNG
            if (originalOrderId != null) {
                try {
                    // Cập nhật trạng thái đơn hàng trong Database
                    orderService.updateOrderStatus(originalOrderId, "confirmed");
                    redirectAttributes.addFlashAttribute("success", "Thanh toán thành công đơn hàng #" + originalOrderId);
                } catch (Exception e) {
                    log.error("Lỗi khi cập nhật trạng thái đơn hàng: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Thanh toán thành công nhưng lỗi cập nhật hệ thống.");
                }
            }
        } else {
            // THANH TOÁN THẤT BẠI HOẶC HỦY
            String errorMessage = translateResponseCode(vnp_ResponseCode);
            redirectAttributes.addFlashAttribute("error", "Thanh toán không thành công: " + errorMessage);
        }

        // 6. REDIRECT: Chuyển hướng người dùng về trang chi tiết đơn hàng cụ thể
        // URL này phải khớp với @GetMapping trang chi tiết đơn hàng của bạn
        if (originalOrderId != null) {
            return "redirect:/user/orders/" + originalOrderId;
        } else {
            return "redirect:/user/orders"; // Quay về danh sách nếu không tìm thấy ID
        }
    }

    /**
     * Hàm phụ trợ dịch mã lỗi VNPay sang tiếng Việt (Tùy chọn)
     */
    private String translateResponseCode(String code) {
        return switch (code) {
            case "24" -> "Giao dịch bị hủy bởi khách hàng.";
            case "11" -> "Giao dịch không thành công do hết hạn chờ.";
            case "09" -> "Thẻ/Tài khoản chưa đăng ký Internet Banking.";
            case "10" -> "Xác thực thông tin thẻ/tài khoản không thành công quá 3 lần.";
            default -> "Mã lỗi: " + code;
        };
    }

    /**
     * URL IPN: VNPay Server gọi trực tiếp đến đây để cập nhật trạng thái đơn hàng ngầm.
     */
    // @GetMapping("/ipn")
    // @ResponseBody // Quan trọng: Trả về JSON/String thay vì trang HTML
    // public Map<String, String> paymentIPN(HttpServletRequest request) {
    //     Map<String, String[]> requestParams = request.getParameterMap();
        
    //     // 1. Xác thực chữ ký
    //     boolean isValidSignature = vnPayService.verifySignature(requestParams);
        
    //     // 2. Chuẩn bị Map phản hồi cho VNPay
    //     Map<String, String> response = new HashMap<>();

    //     if (!isValidSignature) {
    //         response.put("RspCode", "97");
    //         response.put("Message", "Invalid Checksum");
    //         return response;
    //     }

    //     try {
    //         String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
    //         String vnp_TxnRef = request.getParameter("vnp_TxnRef");
    //         String vnp_Amount = request.getParameter("vnp_Amount"); // Số tiền (đã nhân 100)

    //         // Parse ID đơn hàng
    //         Integer originalOrderId = null;
    //         if (vnp_TxnRef != null && vnp_TxnRef.contains("_")) {
    //             originalOrderId = Integer.parseInt(vnp_TxnRef.split("_")[0]);
    //         }

    //         if (originalOrderId == null) {
    //             response.put("RspCode", "01");
    //             response.put("Message", "Order not found");
    //             return response;
    //         }

    //         // 3. Kiểm tra trạng thái đơn hàng trong DB (Tránh update đè nếu đã PAID)
    //         // Giả sử bạn có hàm checkStatus trong orderService
    //         // if (orderService.isAlreadyConfirmed(originalOrderId)) {
    //         //     response.put("RspCode", "02");
    //         //     response.put("Message", "Order already confirmed");
    //         //     return response;
    //         // }

    //         // 4. Xử lý kết quả
    //         if ("00".equals(vnp_ResponseCode)) {
    //             // Thanh toán thành công -> Cập nhật DB
    //             orderService.updateOrderStatus(originalOrderId, "confirmed");
    //             log.info("[IPN] Thanh toán thành công đơn hàng #{}", originalOrderId);
    //         } else {
    //             // Thanh toán thất bại
    //             log.info("[IPN] Giao dịch thất bại mã: {}", vnp_ResponseCode);
    //         }

    //         response.put("RspCode", "00");
    //         response.put("Message", "Confirm Success");

    //     } catch (Exception e) {
    //         log.error("[IPN] Lỗi xử lý: {}", e.getMessage());
    //         response.put("RspCode", "99");
    //         response.put("Message", "Unknown Error");
    //     }

    //     return response;
    // }
}