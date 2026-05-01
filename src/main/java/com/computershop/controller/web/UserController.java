package com.computershop.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.computershop.main.entities.Order;
import com.computershop.main.entities.User;
import com.computershop.service.impl.OrderServiceImpl;
import com.computershop.service.impl.UserServiceImpl;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for user-related pages.
 * Handles profile, orders, etc.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private OrderServiceImpl orderService;

    /**
     * Displays the user profile page.
     */
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.getUserById(userId).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "Account information not found");
            }
            return "user/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Could not load account information: " + e.getMessage());
            return "user/profile";
        }
    }

    /**
     * Updates user profile.
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            User user = userService.getUserById(userId).orElse(null);
            if (user != null) {
                user.setUsername(username.trim());

                // Hàm xử lý chuẩn hóa: Nếu là trống hoặc "N/A" thì biến thành null
                user.setPhone(formatInput(phone));
                user.setAddress(formatInput(address));

                userService.updateUser(userId, user);
                redirectAttributes.addFlashAttribute("success", "Profile updated!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed!");
        }
        return "redirect:/user/profile";
    }

    // Hàm phụ trợ để làm sạch dữ liệu đầu vào
    private String formatInput(String input) {
        if (input == null || input.trim().isEmpty() || input.trim().equalsIgnoreCase("N/A")) {
            return null; // Database sẽ lưu giá trị NULL chuẩn
        }
        return input.trim();
    }

    /**
     * Changes user password.
     */
    @PostMapping("/change-password")
    public String changePassword(
            HttpSession session,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters");
                return "redirect:/user/profile";
            }
            
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/user/profile";
            }

            if (currentPassword.equals(newPassword)) {
                redirectAttributes.addFlashAttribute("error", "New password cannot be the same as the current password");
                return "redirect:/user/profile";
            }

            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!userService.verifyPassword(currentPassword, user.getPasswordHash())) {
                redirectAttributes.addFlashAttribute("error", "Incorrect current password");
                return "redirect:/user/profile";
            }

            userService.changePassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Password change failed: " + e.getMessage());
        }

        return "redirect:/user/profile";
    }

    /**
     * Displays the user orders history page.
     */
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersWithDetailsForUser(userId);
        model.addAttribute("orders", orders);

        return "user/orders";
    }

    /**
     * Displays order detail page for the logged-in user.
     */
    @GetMapping("/orders/{id}")
    @Transactional(readOnly = true)
    public String orderDetail(@PathVariable("id") Integer orderId,
                              HttpSession session,
                              Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // Security: ensure order belongs to the logged-in user
                if (!order.getUser().getUserId().equals(userId)) {
                    return "redirect:/user/orders";
                }
                model.addAttribute("order", order);
                return "user/order-detail";
            } else {
                model.addAttribute("error", "Order not found");
                return "redirect:/user/orders";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Could not load order details: " + e.getMessage());
            return "redirect:/user/orders";
        }
    }

    /**
     * Displays all products the user has ever ordered.
     */
    @GetMapping("/ordered-products")
    @Transactional(readOnly = true)
    public String orderedProducts(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            List<Object[]> orderedProducts = orderService.getOrderedProductsByUserId(userId);
            model.addAttribute("orderedProducts", orderedProducts);
            model.addAttribute("totalProducts", orderedProducts.size());
        } catch (Exception e) {
            model.addAttribute("error", "Could not load ordered products: " + e.getMessage());
            model.addAttribute("orderedProducts", List.of());
            model.addAttribute("totalProducts", 0);
        }

        return "user/ordered-products";
    }

    /**
     * Displays the user dashboard page.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            List<Order> recentOrders = orderService.getRecentOrdersByUserId(userId, 5);
            model.addAttribute("recentOrders", recentOrders);
        } catch (Exception e) {
            model.addAttribute("recentOrders", List.of());
        }

        model.addAttribute("userId", userId);
        return "user/dashboard";
    }
}
