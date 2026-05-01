package com.computershop.controller.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private VNPayService vnPayService;

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
    public String checkout(@RequestParam("ids") String ids, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");    
        if (role != null && "admin".equalsIgnoreCase(role)) {
            model.addAttribute("error", "Admin cannot place orders.");
            return "redirect:/";
        }

        try {
            // --- BƯỚC 1: CHUYỂN CHUỖI IDS THÀNH LIST INTEGER ---
            List<Integer> selectedIds = Arrays.stream(ids.split(","))
                                            .map(Integer::parseInt)
                                            .collect(Collectors.toList());

            // --- BƯỚC 2: CHỈ LẤY CÁC SẢN PHẨM ĐƯỢC CHỌN ---
            List<CartItem> cartItems = cartService.getCartItemsByIds(selectedIds);

            if (cartItems.isEmpty()) return "redirect:/cart/view";

            // --- BƯỚC 3: TÍNH TỔNG TIỀN CHO RIÊNG CÁC MÓN ĐÃ CHỌN ---
            double total = cartItems.stream()
                                    .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                                    .sum();

            // Điền địa chỉ mặc định (Giữ nguyên logic của bạn)
            try {
                var userOpt = userService.getUserById(userId);
                if (userOpt.isPresent()) {
                    model.addAttribute("defaultAddress", userOpt.get().getAddress());
                }
            } catch (Exception ignored) {}

            // --- BƯỚC 4: GỬI IDS NGƯỢC LẠI VIEW ĐỂ DÙNG TRONG HIDDEN INPUT ---
            model.addAttribute("selectedIds", ids); 
            
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
            @RequestParam("ids") String ids, // <--- THÊM DÒNG NÀY để nhận ID sản phẩm được chọn
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
            // THAY ĐỔI: Không lấy toàn bộ giỏ, mà chỉ lấy các sản phẩm được chọn
            List<Integer> selectedIds = Arrays.stream(ids.split(","))
                                            .map(Integer::parseInt)
                                            .collect(Collectors.toList());
            
            List<CartItem> cartItems = cartService.getCartItemsByIds(selectedIds); // <--- Cần viết hàm này trong Service
            
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn sản phẩm để thanh toán");
                return "redirect:/cart/view";
            }

            // 1. Lấy User
            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/cart/view";
            }

            // 2. Tạo đơn hàng
            Order order = new Order();
            order.setUser(userOpt.get());
            order.setOrderDate(LocalDateTime.now());
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);
            if (notes != null && !notes.isBlank()) order.setNotes(notes);
            
            // Tính tổng tiền cho các món đã chọn (tránh lấy tổng cả giỏ)
            double totalAmount = cartItems.stream()
                                        .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                                        .sum();
            
            // Set status dựa vào phương thức thanh toán
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                order.setStatus("pending"); // COD - đã xác nhận, đang xử lý đơn hàng
            } else {
                order.setStatus("pending_payment"); // Chờ thanh toán online
            }
            
            Order savedOrder = orderService.createOrder(order);

            // 3. Tạo OrderDetail cho những món đã chọn
            for (CartItem item : cartItems) {
                OrderDetail detail = new OrderDetail(
                    savedOrder,
                    item.getProduct(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
                );
                orderDetailService.createOrderDetail(detail);
            }

            // 4. Xoá những món ĐÃ THANH TOÁN khỏi giỏ hàng (Không xóa sạch cả giỏ)
            cartService.deleteCartItems(selectedIds); // <--- Cần viết hàm xóa theo danh sách ID
            
            // Cập nhật lại số lượng icon giỏ hàng trên header
            int remainingItems = cartService.getCartItemsSafe(userId).size();
            session.setAttribute("cartCount", remainingItems);

            // 5. Xử lý redirect tuỳ theo payment method
            if ("VNPAY".equals(paymentMethod)) {
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

            // Tạo thanh toán qua VNPay Sandbox
            String payUrl = vnPayService.createPaymentUrl(orderId, amount, orderInfo, ipAddr);
            return "redirect:" + payUrl;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not create payment: " + e.getMessage());
            return "redirect:/user/orders";
        }
    }
}
