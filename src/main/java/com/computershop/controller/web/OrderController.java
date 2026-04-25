package com.computershop.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.computershop.main.entities.Order;
import com.computershop.service.impl.CartServiceImpl;
import com.computershop.service.impl.OrderDetailServiceImpl;
import com.computershop.service.impl.OrderServiceImpl;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for user order-related operations.
 * Handles viewing orders, order details, etc.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    /**
     * Displays the user's orders page.
     *
     * @param session HTTP session
     * @param model model
     * @return orders view or redirect to login
     */
    @GetMapping
    public String myOrders(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            model.addAttribute("orders", orders);

            return "user/orders";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load orders: " + e.getMessage());
            return "user/orders";
        }
    }

    /**
     * Displays order detail page.
     *
     * @param orderId order ID
     * @param session HTTP session
     * @param model model
     * @return order detail view or redirect to orders
     */
    @GetMapping("/{id}")
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

                // Check if order belongs to user
                if (!order.getUser().getUserId().equals(userId)) {
                    return "redirect:/orders";
                }

                model.addAttribute("order", order);
                return "user/order-detail";
            } else {
                return "redirect:/orders";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load order: " + e.getMessage());
            return "redirect:/orders";
        }
    }

    /**
     * Creates a new order from cart.
     *
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to orders or cart
     */
    @PostMapping("/create")
    public String createOrder(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        // Check if user is admin - admin cannot create order
        String role = (String) session.getAttribute("role");
        if (role != null && "admin".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("error", "Admin cannot place orders.");
            return "redirect:/";
        }

        try {
            // Get cart items
            var cartItems = cartService.getCartItemsSafe(userId);

            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cart is empty");
                return "redirect:/cart/view";
            }

            // TODO: Complete order creation logic
            // 1. Create order
            // 2. Add order details from cart items
            // 3. Decrease product stock
            // 4. Clear cart

            redirectAttributes.addFlashAttribute("success", "Order created successfully");
            return "redirect:/orders";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create order: " + e.getMessage());
            return "redirect:/cart/view";
        }
    }
}
