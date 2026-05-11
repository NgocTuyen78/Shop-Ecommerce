package com.computershop.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.computershop.dto.ReportDTO;
import com.computershop.main.entities.Category;
import com.computershop.main.entities.Order;
import com.computershop.main.entities.Product;
import com.computershop.main.entities.User;
import com.computershop.main.repositories.ImageRepository;
import com.computershop.service.impl.CategoryServiceImpl;
import com.computershop.service.impl.OrderServiceImpl;
import com.computershop.service.impl.ProductServiceImpl;
import com.computershop.service.impl.UserServiceImpl;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for admin dashboard and management.
 * Handles admin-only operations for users, products, categories, and orders.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Checks if the current user is an admin.
     *
     * @param session HTTP session
     * @return true if user is admin
     */
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "admin".equals(role);
    }

    /**
     * Redirects to admin dashboard.
     *
     * @param session HTTP session
     * @return redirect to dashboard
     */
    @GetMapping
    public String adminRoot(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        return "redirect:/admin/dashboard";
    }

    /**
     * Displays the admin dashboard.
     *
     * @param session HTTP session
     * @param model model
     * @return dashboard view
     */
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("totalUsers", userService.getTotalUsers());
            model.addAttribute("totalProducts", productService.getTotalProducts());
            model.addAttribute("totalCategories", categoryService.getAllCategories().size());
            model.addAttribute("totalOrders", orderService.getTotalOrders());
            model.addAttribute("totalRevenue", orderService.getTotalRevenue());

            model.addAttribute("recentOrders", orderService.getRecentOrders(10));
            model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
            model.addAttribute("recentUsers", userService.getRecentUsers(5));

            return "admin/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    /**
     * Displays the user management page.
     *
     * @param session HTTP session
     * @param model model
     * @return users view
     */
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            List<User> users = userService.getAllUsers();
            long adminCount = users.stream()
                .filter(u -> u.getRole() != null && "admin".equalsIgnoreCase(u.getRole().getRoleName()))
                .count();
            long customerCount = users.stream()
                .filter(u -> u.getRole() != null && "customer".equalsIgnoreCase(u.getRole().getRoleName()))
                .count();
            model.addAttribute("users", users);
            model.addAttribute("adminCount", adminCount);
            model.addAttribute("customerCount", customerCount);
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("users", List.of());
            return "admin/users";
        }
    }

    /**
     * Add a new user (admin panel)
     */
    @PostMapping("/users/add")
    public String addUser(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("role") String roleName,
                        @RequestParam("phone") String phone,
                        @RequestParam("address") String address,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        
        // 1. Kiểm tra quyền Admin (Giữ nguyên logic của bạn)
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            // --- BẮT ĐẦU CÁC RÀNG BUỘC KIỂM TRA (VALIDATION) ---

            // Kiểm tra để trống hoặc chỉ chứa khoảng trắng (.trim())
            if (username == null || username.trim().isEmpty()) {
                throw new Exception("Tên đăng nhập không được để trống.");
            }
            if (username.length() < 4) {
                throw new Exception("Tên đăng nhập phải có ít nhất 4 ký tự.");
            }

            if (password == null || password.length() < 6) {
                throw new Exception("Mật khẩu phải có ít nhất 6 ký tự.");
            }

            if (phone == null || !phone.matches("^(0|\\+84)(\\d{9})$")) {
                throw new Exception("Số điện thoại không hợp lệ (phải có 10 số và bắt đầu bằng 0 hoặc +84).");
            }

            if (address == null || address.trim().length() < 10) {
                throw new Exception("Địa chỉ quá ngắn, vui lòng nhập chi tiết hơn.");
            }

            // Kiểm tra logic nghiệp vụ: Tên đăng nhập đã tồn tại chưa?
            // Nếu tìm thấy (isPresent == true) thì văng lỗi
            if (userService.getUserByUsername(username).isPresent()) {
                throw new Exception("Tên đăng nhập này đã được sử dụng.");
            }

            //Kiểm tra logic nghiệp vụ: Số điện thoại đã tồn tại chưa?
            if (userService.getUserByPhone(phone).isPresent()) {
                throw new Exception("Số điện thoại này đã được sử dụng.");
            }
            // --- KẾT THÚC CÁC RÀNG BUỘC ---

            // Nếu vượt qua tất cả các kiểm tra trên, tiến hành tạo User
            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(password); // Lưu ý: Nên mã hóa password trước khi lưu
            user.setPhone(phone.trim());
            user.setAddress(address.trim());

            if ("admin".equalsIgnoreCase(roleName)) {
                user.setRole(userService.getRoleService().getAdminRole());
            } else {
                user.setRole(userService.getRoleService().getCustomerRole());
            }

            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Các tên này phải khớp chính xác với biến trong th:value="${...}"
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldPhone", phone);
            redirectAttributes.addFlashAttribute("oldAddress", address);
            redirectAttributes.addFlashAttribute("oldRole", roleName);
            redirectAttributes.addFlashAttribute("oldPassword", password);
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Basic user search by username (admin panel)
     */
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam("q") String query,
                             HttpSession session,
                             Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            List<User> users;
            if (query == null || query.trim().isEmpty()) {
                users = userService.getAllUsers();
            } else {
                users = userService.getAllUsers().stream()
                        .filter(u -> u.getUsername().toLowerCase().contains(query.trim().toLowerCase()))
                        .toList();
            }
            long adminCount = users.stream()
                .filter(u -> u.getRole() != null && "admin".equalsIgnoreCase(u.getRole().getRoleName()))
                .count();
            long customerCount = users.stream()
                .filter(u -> u.getRole() != null && "customer".equalsIgnoreCase(u.getRole().getRoleName()))
                .count();
            model.addAttribute("users", users);
            model.addAttribute("adminCount", adminCount);
            model.addAttribute("customerCount", customerCount);
            model.addAttribute("param", new Object() { public String q = query; });
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("users", List.of());
            return "admin/users";
        }
    }

    /**
     * Toggles user active status.
     *
     * @param userId user ID
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to users page
     */
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Integer userId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            userService.toggleUserStatus(userId);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    /**
     * Resets user password to default.
     */
    @PostMapping("/users/{id}/reset-password")
    public String resetUserPassword(@PathVariable("id") Integer userId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        try {
            var userOpt = userService.getUserById(userId);
            
            if (userOpt.isPresent()) {
                String username = userOpt.get().getUsername();
        
                userService.resetUserPassword(userId, "123456");
                
                redirectAttributes.addFlashAttribute("success", 
                    "Password for user '" + username + "' has been successfully reset to '123456'");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Displays the category management page.
     *
     * @param session HTTP session
     * @param model model
     * @return categories view
     */
    @GetMapping("/categories")
    public String manageCategories(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            List<Category> categories = categoryService.getAllCategoriesOrderedByName();
            model.addAttribute("categories", categories);
            model.addAttribute("newCategory", new Category());
            return "admin/categories";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("categories", List.of());
            return "admin/categories";
        }
    }

    /**
     * Creates a new category.
     *
     * @param category category to create
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to categories page
     */
    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute("newCategory") Category category,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Updates a category.
     *
     * @param categoryId category ID
     * @param category updated category
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to categories page
     */
    @PostMapping("/categories/{id}/update")
    public String updateCategory(@PathVariable("id") Integer categoryId,
                               @ModelAttribute("category") Category category,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            categoryService.updateCategory(categoryId, category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Deletes a category.
     *
     * @param categoryId category ID
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to categories page
     */
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable("id") Integer categoryId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            categoryService.deleteCategory(categoryId);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Displays the product management page.
     *
     * @param session HTTP session
     * @param model model
     * @return products view
     */
    @GetMapping("/products")
public String manageProducts(
        @RequestParam(name = "keyword", required = false) String keyword, 
        HttpSession session, 
        Model model) {
    
    // 1. Kiểm tra quyền Admin
    if (!isAdmin(session)) {
        return "redirect:/login";
    }

    try {
        List<Product> products;
        
        // 2. Xử lý logic tìm kiếm
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm sản phẩm theo tên (sử dụng hàm ContainingIgnoreCase trong Service/Repository)
            products = productService.searchProductsByName(keyword);
            model.addAttribute("keyword", keyword); 
        } else {
            // Nếu không có từ khóa, lấy toàn bộ danh sách
            products = productService.getAllProducts();
        }

        // 3. Lấy danh sách Category để đổ vào các dropdown (nếu có)
        List<Category> categories = categoryService.getAllCategoriesOrderedByName();

        // 4. Tính toán thống kê ĐỒNG BỘ với ngưỡng hiển thị trong HTML (Mốc 20)
        // Tổng sản phẩm đang có sẵn (Số lượng > 20)
        long inStockCount = products.stream()
                .filter(p -> p.getStockQuantity() > 20)
                .count();
                
        // Sản phẩm sắp hết hàng (Số lượng từ 1 đến 20)
        long lowStockCount = products.stream()
                .filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() <= 20)
                .count();
                
        // Sản phẩm đã hết hàng (Số lượng <= 0)
        long outOfStockCount = products.stream()
                .filter(p -> p.getStockQuantity() <= 0)
                .count();

        // 5. Gửi dữ liệu sang View
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("newProduct", new Product()); // Dùng cho form thêm mới nếu cần
        model.addAttribute("inStockCount", inStockCount);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("outOfStockCount", outOfStockCount);

        return "admin/products";

    } catch (Exception e) {
        model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        model.addAttribute("products", List.of());
        return "admin/products";
    }
}

    /**
     * Displays the add product page.
     *
     * @param session HTTP session
     * @param model model
     * @return add product view
     */
    @GetMapping("/products/add")
    public String addProductPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            List<Category> categories = categoryService.getAllCategoriesOrderedByName();
            model.addAttribute("categories", categories);
            model.addAttribute("newProduct", new Product());

            return "admin/add-product";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("categories", List.of());
            return "admin/add-product";
        }
    }

    /**
     * Creates a new product.
     *
     * @param product product to create
     * @param categoryId category ID
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to products page
     */
    @PostMapping("/products/create")
    public String createProduct(@ModelAttribute("newProduct") Product product,
                              @RequestParam("categoryId") Integer categoryId,
                              @RequestParam(value = "imageUrl", required = false) String imageUrl,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Product name is required");
                return "redirect:/admin/products";
            }

            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Price must be greater than 0");
                return "redirect:/admin/products";
            }

            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            }

            // Save image if URL provided
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                com.computershop.main.entities.Image image = new com.computershop.main.entities.Image(imageUrl.trim());
                image = imageRepository.save(image);
                product.setImage(image);
            }

            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product created successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    /**
     * Displays the edit product page.
     *
     * @param productId product ID
     * @param session HTTP session
     * @param model model
     * @return edit product view
     */
    @GetMapping("/products/{id}/edit")
    public String editProductPage(@PathVariable("id") Integer productId,
                                HttpSession session,
                                Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                return "redirect:/admin/products";
            }

            List<Category> categories = categoryService.getAllCategoriesOrderedByName();

            model.addAttribute("product", productOpt.get());
            model.addAttribute("categories", categories);

            return "admin/edit-product";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    /**
     * Updates a product.
     *
     * @param productId product ID
     * @param product updated product
     * @param categoryId category ID
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to products page
     */
    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable("id") Integer productId,
                              @ModelAttribute("product") Product product,
                              @RequestParam("categoryId") Integer categoryId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            }

            productService.updateProduct(productId, product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    /**
     * Deletes a product.
     *
     * @param productId product ID
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to products page
     */
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable("id") Integer productId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    /**
     * Displays the order management page.
     *
     * @param session HTTP session
     * @param model model
     * @return orders view
     */
    @GetMapping("/orders")
public String manageOrders(
        @RequestParam(required = false) Integer orderId,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        HttpSession session, 
        Model model) {
    
    if (!isAdmin(session)) {
        return "redirect:/login";
    }

    try {
        // 1. Áp dụng logic mặc định như bên Report: 
        // Nếu không chọn ngày thì lấy dữ liệu trong vòng 30 ngày gần nhất
        LocalDate start = (from == null) ? LocalDate.now().minusDays(30) : from;
        LocalDate end = (to == null) ? LocalDate.now() : to;

        // 2. Lấy danh sách đơn hàng trong khoảng thời gian từ Database (Tối ưu hiệu năng)
        List<Order> orders = orderService.getOrdersByPeriod(start, end);

        // 3. Kết hợp với các bộ lọc hiện có (ID, CustomerName, Status) bằng Stream API
        List<Order> filteredOrders = orders.stream()
            .filter(o -> (orderId == null || o.getOrderId().equals(orderId)))
            .filter(o -> (status == null || status.isEmpty() || o.getStatus().equalsIgnoreCase(status)))
            .filter(o -> (customerName == null || customerName.isEmpty() || 
                        (o.getUser() != null && o.getUser().getUsername().toLowerCase().contains(customerName.toLowerCase()))))
            .sorted((o1, o2) -> {
                // Sắp xếp đơn hàng mới nhất lên đầu dựa trên OrderDate
                if (o1.getOrderDate() != null && o2.getOrderDate() != null) {
                    return o2.getOrderDate().compareTo(o1.getOrderDate());
                }
                return o2.getOrderId().compareTo(o1.getOrderId());
            })
            .collect(Collectors.toList());

        // 4. Đưa dữ liệu ra giao diện
        model.addAttribute("orders", filteredOrders);
        
        // Gửi lại giá trị 'from' và 'to' để hiển thị trên 2 ô input date ở HTML
        model.addAttribute("from", start);
        model.addAttribute("to", end);
        
        return "admin/orders";

    } catch (Exception e) {
        model.addAttribute("error", "Error: " + e.getMessage());
        model.addAttribute("orders", List.of());
        return "admin/orders";
    }
}

    /**
     * Displays order detail page.
     *
     * @param orderId order ID
     * @param session HTTP session
     * @param model model
     * @return order detail view
     */
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable("id") Integer orderId,
                            HttpSession session,
                            Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
                return "admin/order-detail";
            } else {
                return "redirect:/admin/orders";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    /**
     * Updates order status.
     *
     * @param orderId order ID
     * @param status new status
     * @param session HTTP session
     * @param redirectAttributes redirect attributes
     * @return redirect to order detail page
     */
    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Integer orderId,
                                  @RequestParam("status") String status,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(status);
                orderService.updateOrder(orderId, order);
                redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Order not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + orderId;
    }

    @GetMapping("/report")
    public String showReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        LocalDate start = (from == null) ? LocalDate.now().minusDays(30) : from;
        LocalDate end = (to == null) ? LocalDate.now() : to;

        List<Order> orders = orderService.getOrdersByPeriod(start, end);

        // Tính toán doanh thu bằng Stream API
        // 1. Tổng doanh thu phát sinh (Total Sales)
        double totalSales = orders.stream()
                .mapToDouble(Order::getTotalAmount) 
                .sum();

        // 2. Tiền đã thanh toán thành công (Total Revenue)
        double totalPaid = orders.stream()
                .filter(o -> "completed".equalsIgnoreCase(o.getStatus()) || "confirmed".equalsIgnoreCase(o.getStatus()) || "shipping".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 3. Tiền đang đợi thanh toán (Pending Amount)
        double totalPending = orders.stream()
                .filter(o -> "pending".equalsIgnoreCase(o.getStatus()) || "pending_payment".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 4. Giá trị đơn đã hủy (Cancelled Value)
        double totalCancelled = orders.stream()
                .filter(o -> "cancelled".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // Đưa vào DTO để gửi sang HTML
        ReportDTO report = new ReportDTO(totalSales, totalPaid, totalPending, totalCancelled);
        model.addAttribute("report", report);

        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("pendingCount", count(orders, "pending"));
        model.addAttribute("pendingPaymentCount", count(orders, "pending_payment"));
        model.addAttribute("confirmedCount", count(orders, "confirmed"));
        model.addAttribute("shippingCount", count(orders, "shipping"));
        model.addAttribute("completedCount", count(orders, "completed"));
        model.addAttribute("cancelledCount", count(orders, "cancelled"));
        
        model.addAttribute("from", start);
        model.addAttribute("to", end);

        return "admin/report";
    }

    private long count(List<Order> orders, String status) {
        return orders.stream().filter(o -> status.equalsIgnoreCase(o.getStatus())).count();
    }
}
