package com.computershop.controller.web;

import com.computershop.service.impl.UserServiceImpl;
import com.computershop.main.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for authentication-related operations.
 * Handles login, logout, registration, password reset, etc.
 */
@Controller
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private com.computershop.service.impl.CartServiceImpl cartService;

    /**
     * Displays the login page.
     *
     * @return login view
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Processes user login.
     *
     * @param usernameOrEmail username or email
     * @param password password
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to home or back to login with error
     */
    @PostMapping("/login")
    public String login(
            @RequestParam(value = "usernameOrEmail", required = false) String usernameOrEmail,
            @RequestParam(value = "username", required = false) String usernameField,
            @RequestParam String password,
            HttpSession session,
            jakarta.servlet.http.HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Support both "usernameOrEmail" and "username" field names
        String loginInput = usernameOrEmail != null ? usernameOrEmail : usernameField;
        if (loginInput == null || loginInput.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please enter your username or email");
            return "redirect:/login";
        }

        try {
            var userOpt = userService.authenticate(loginInput, password);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Invalidate old session and create new one to prevent session fixation
                session.invalidate();
                session = request.getSession(true);

                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());

                try {
                    int mapCount = cartService.getCartItemCount(user.getUserId());
                    session.setAttribute("cartCount", mapCount);
                } catch (Exception ignored) {}

                String role = (user.getRole() != null) ? user.getRole().getRoleName() : "customer";
                session.setAttribute("role", role);

                if ("admin".equalsIgnoreCase(role)) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return "redirect:/login";
            }
        } catch (RuntimeException e) {
            if ("ACCOUNT_LOCKED".equals(e.getMessage())) {
                redirectAttributes.addFlashAttribute("error", "Your account has been locked. Please contact the administrator.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Login failed: " + e.getMessage());
            }
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed: " + e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * Processes user logout.
     *
     * @param session HTTP session
     * @return redirect to home
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }

    /**
     * Displays the registration page.
     *
     * @return registration view
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Processes user registration.
     *
     * @param username username
     * @param email email
     * @param password password
     * @param confirmPassword confirm password
     * @param redirectAttributes redirect attributes
     * @return redirect to login or back to register with error
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String address,
            RedirectAttributes redirectAttributes) {

        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("address", address);
                return "redirect:/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("address", address);
                return "redirect:/register";
            }

            // Validate address
            if (address == null || address.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please enter a default shipping address");
                redirectAttributes.addFlashAttribute("username", username);
                return "redirect:/register";
            }

            // Check if username exists
            if (userService.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                redirectAttributes.addFlashAttribute("address", address);
                return "redirect:/register";
            }

            // Create user
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setAddress(address.trim());
            user.setEnabled(true);

            userService.registerUser(user);

            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("address", address);
            return "redirect:/register";
        }
    }

    /**
     * Displays the forgot password page.
     *
     * @return forgot password view
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    /**
     * Displays the reset password page.
     *
     * @param token password reset token
     * @param model model
     * @return reset password view or redirect to login
     */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }
}
