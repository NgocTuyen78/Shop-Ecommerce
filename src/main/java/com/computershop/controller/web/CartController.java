package com.computershop.controller.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.computershop.main.entities.CartItem;
import com.computershop.main.entities.Order;
import com.computershop.main.entities.OrderDetail;
import com.computershop.main.entities.Product;
import com.computershop.service.impl.CartServiceImpl;
import com.computershop.service.impl.MomoSandboxService;
import com.computershop.service.impl.MomoService;
import com.computershop.service.impl.VNPayService;
import com.computershop.service.impl.OrderDetailServiceImpl;
import com.computershop.service.impl.OrderServiceImpl;
import com.computershop.service.impl.ProductServiceImpl;
import com.computershop.service.impl.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MomoService momoService;

    @Autowired
    private MomoSandboxService momoSandboxService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private VNPayController vnPayController;

    // ─── Xem giỏ hàng ────────────────────────────────────────────────────────

    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            var cartItems = cartService.getCartItemsSafe(userId);
            double total  = cartService.getCartTotal(userId);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartTotal", total);
            return "cart/view";
        } catch (Exception e) {
            model.addAttribute("error", "Could not load cart: " + e.getMessage());
            return "cart/view";
        }
    }

    // ─── Thêm vào giỏ (AJAX / JSON) ──────────────────────────────────────────

    @PostMapping("/add-ajax")
    @ResponseBody
    public Map<String, Object> addToCartAjax(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("success", false, "message", "Please login to add to cart.");
        }

        // Check if user is admin - admin cannot add to cart
        String role = (String) session.getAttribute("role");
        if (role != null && "admin".equalsIgnoreCase(role)) {
            return Map.of("success", false, "message", "Admin cannot add products to cart.");
        }

        try {
            var productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                return Map.of("success", false, "message", "Product does not exist.");
            }
            cartService.addToCart(userId, productId, quantity);
            int count = cartService.getCartItemCount(userId);
            session.setAttribute("cartCount", count);
            return Map.of("success", true, "message", "Added to cart!", "count", count);
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage() != null ? e.getMessage() : "An error occurred.");
        }
    }

    // ─── Thêm vào giỏ ────────────────────────────────────────────────────────

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Check if user is admin - admin cannot add to cart
        String role = (String) session.getAttribute("role");
        if (role != null && "admin".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("error", "Admin cannot add products to cart.");
            return "redirect:/products";
        }

        try {
            var productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Product does not exist");
                return "redirect:/products";
            }
            cartService.addToCart(userId, productId, quantity);
            int count = cartService.getCartItemCount(userId);
            session.setAttribute("cartCount", count);
            redirectAttributes.addFlashAttribute("success", "Added to cart");
            return "redirect:/cart/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/product/" + productId;
        }
    }

    // ─── Cập nhật số lượng ────────────────────────────────────────────────────

    @PostMapping("/update")
    public String updateCartItem(
            @RequestParam Integer cartItemId,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            if (quantity <= 0) {
                cartService.removeFromCart(cartItemId);
                redirectAttributes.addFlashAttribute("success", "Product removed");
            } else {
                cartService.updateCartItemQuantity(cartItemId, quantity);
                redirectAttributes.addFlashAttribute("success", "Cart updated");
            }
            int count = cartService.getCartItemCount(userId);
            session.setAttribute("cartCount", count);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/view";
    }

    // ─── Xoá 1 sản phẩm ──────────────────────────────────────────────────────

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam Integer cartItemId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            cartService.removeFromCart(cartItemId);
            int count = cartService.getCartItemCount(userId);
            session.setAttribute("cartCount", count);
            redirectAttributes.addFlashAttribute("success", "Product removed");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/view";
    }

    // ─── Xoá toàn bộ giỏ hàng ────────────────────────────────────────────────

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            cartService.clearCart(userId);
            session.setAttribute("cartCount", 0);
            redirectAttributes.addFlashAttribute("success", "Cart cleared");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/view";
    }

    // ─── Số lượng sản phẩm trong giỏ (API) ───────────────────────────────────

    @GetMapping("/count")
    @ResponseBody
    public Map<String, Object> getCartCount(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return Map.of("count", 0);

        try {
            return Map.of("count", cartService.getCartItemCount(userId));
        } catch (Exception ignored) {
            return Map.of("count", 0);
        }
    }

    // ─── Trang Checkout (GET) ─────────────────────────────────────────────────

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Check if user is admin - admin cannot checkout
        String role = (String) session.getAttribute("role");
        if (role != null && "admin".equalsIgnoreCase(role)) {
            model.addAttribute("error", "Admin cannot place orders.");
            return "redirect:/";
        }

        try {
            List<CartItem> cartItems = cartService.getCartItemsSafe(userId);
            double total             = cartService.getCartTotal(userId);

            if (cartItems.isEmpty()) return "redirect:/cart/view";

            // Pre-fill user's default address
            try {
                var userOpt = userService.getUserById(userId);
                if (userOpt.isPresent()) {
                    model.addAttribute("defaultAddress", userOpt.get().getAddress());
                }
            } catch (Exception ignored) {}

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartTotal", total);
            return "cart/checkout";
        } catch (Exception e) {
            model.addAttribute("error", "Could not load checkout page: " + e.getMessage());
            return "redirect:/cart/view";
        }
    }

    // ─── Xử lý đặt hàng (POST) ───────────────────────────────────────────────

    @PostMapping("/checkout")
    public String processCheckout(
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Check if user is admin - admin cannot create order
        String role = (String) session.getAttribute("role");
        if (role != null && "admin".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("error", "Admin cannot place orders.");
            return "redirect:/";
        }

        try {
            List<CartItem> cartItems = cartService.getCartItemsSafe(userId);
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cart is empty");
                return "redirect:/cart/view";
            }

            // 1. Lấy User
            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/cart/view";
            }

            Order order = new Order();
            order.setUser(userOpt.get());
            order.setOrderDate(LocalDateTime.now());
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);
            if (notes != null && !notes.isBlank()) order.setNotes(notes);
            
            // Set status dựa vào phương thức thanh toán
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                order.setStatus("shipping"); // COD - đã xác nhận, đang giao hàng
            } else {
                order.setStatus("pending_payment"); // Chờ thanh toán online
            }
            
            Order savedOrder = orderService.createOrder(order);

            double totalAmount = 0.0;
            // 3. Tạo OrderDetail
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                double itemPrice = product.getPrice().doubleValue();
                totalAmount += itemPrice * item.getQuantity();
                
                OrderDetail detail = new OrderDetail(
                    savedOrder,
                    product,
                    item.getQuantity(),
                    BigDecimal.valueOf(itemPrice)
                );
                orderDetailService.createOrderDetail(detail);
            }

            // 4. Xoá giỏ hàng
            cartService.clearCart(userId);
            session.setAttribute("cartCount", 0);

            // 5. Xử lý redirect tuỳ theo payment method
            if ("MOMO".equals(paymentMethod)) {
                String orderInfo = "Payment for order " + savedOrder.getOrderId() + " at ComputerShop";
                var result = momoSandboxService.createPayment(savedOrder.getOrderId(), Math.round(totalAmount), orderInfo);
                if (result.isSuccess()) {
                    return "redirect:" + result.getPayUrl();
                } else {
                    redirectAttributes.addFlashAttribute("error", "MoMo payment creation error: " + result.getErrorMessage());
                    return "redirect:/user/orders";
                }
            } else if ("VNPAY".equals(paymentMethod)) {
                long amount = Math.round(totalAmount);
                //String orderInfo = "Payment for order " + savedOrder.getOrderId() + " at ComputerShop";
                String orderInfo = "ThanhToan";
                String ipAddr = request.getRemoteAddr();
                
                // Go to VNPay Sandbox
                String payUrl = vnPayService.createPaymentUrl(
                        savedOrder.getOrderId(), amount, orderInfo, ipAddr);
                        
                return "redirect:" + payUrl;
            } else {
                // COD - direct to orders page with success message
                redirectAttributes.addFlashAttribute("success", "Order placed successfully! We will deliver to: " + shippingAddress);
                return "redirect:/user/orders";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Order failed: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    // ─── Trang thanh toán online (cho các đơn chưa thanh toán) ───────────────

    @GetMapping("/payment/{orderId}")
    public String paymentPage(@PathVariable Integer orderId,
                              HttpServletRequest request,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            var orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isEmpty() || !orderOpt.get().getUser().getUserId().equals(userId)) {
                return "redirect:/user/orders";
            }

            Order order  = orderOpt.get();
            
            // Nếu đơn hàng đã được thanh toán hoặc huỷ bỏ thì không cho thanh toán lại
            if (!"pending_payment".equals(order.getStatus())) {
                redirectAttributes.addFlashAttribute("error", "This order cannot be paid.");
                return "redirect:/user/orders";
            }
            
            long amount  = Math.round(order.getTotalAmount());
            //String orderInfo = "Payment for order " + orderId + " at ComputerShop";
            String orderInfo = "ThanhToan";
            String ipAddr = request.getRemoteAddr();

            // Tạo thanh toán qua MoMo Sandbox hoặc VNPay Sandbox
            if ("MOMO".equals(order.getPaymentMethod())) {
                var result = momoSandboxService.createPayment(orderId, amount, orderInfo);
                if (result.isSuccess()) {
                    return "redirect:" + result.getPayUrl();
                } else {
                    redirectAttributes.addFlashAttribute("error", "MoMo payment creation error: " + result.getErrorMessage());
                    return "redirect:/user/orders";
                }
            } else {
                String payUrl = vnPayService.createPaymentUrl(orderId, amount, orderInfo, ipAddr);
                return "redirect:" + payUrl;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not create payment: " + e.getMessage());
            return "redirect:/user/orders";
        }
    }
}
