# 🛍️ Shop-Ecommerce

Hệ thống website thương mại điện tử bán phụ kiện điện tử được xây dựng trên nền tảng **Java Spring Boot**.

---

## ✨ Tính năng nổi bật
*   **Mua sắm:** Xem sản phẩm, giỏ hàng, và tích hợp thanh toán trực tuyến qua **VNPay**.
*   **Quản trị (Admin):** Quản lý sản phẩm, danh mục, đơn hàng và xem báo cáo thống kê doanh thu.
*   **Bảo mật:** Hệ thống phân quyền người dùng (Admin/User) chặt chẽ.
*   **Linh hoạt:** Hỗ trợ chạy trên nhiều loại Database khác nhau (H2, PostgreSQL).

---

## 🌐 Truy cập trực tuyến
Bạn có thể trải nghiệm nhanh hệ thống đã được triển khai tại:  
👉 [https://shop-ecommerce-rhry.onrender.com/](https://shop-ecommerce-rhry.onrender.com/)

---

## 🚀 Hướng dẫn khởi chạy (Local)

### Cách 1: Chạy nhanh (Dùng Database ảo H2 - Không cần cài đặt)
Mở terminal tại thư mục gốc của dự án và chạy lệnh tương ứng:

*   **Windows:**
    ```bash
    .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"

*   **macOS / Linux:**
    ```bash
    ./mvnw spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"

*   **Truy cập:** [http://localhost:2345](http://localhost:2345)

### Cách 2: Chạy với PostgreSQL (Dùng Docker)
1.  **Bật Database:**
    ```bash
    docker compose -f docker/docker-compose.single.yml up postgres -d
    ```
2.  **Chạy ứng dụng:**
    ```bash
    .\mvnw.cmd spring-boot:run

*   **Truy cập:** http://localhost:2345

---

## 🔑 Tài khoản đăng nhập mặc định

| Vai trò | Tên đăng nhập | Mật khẩu |
| :--- | :--- | :--- |
| **Quản trị viên (Admin)** | admin | admin123 |
| **Khách hàng (User)** | user | user123 |

---

## 🛠 Yêu cầu hệ thống
*   **Java 17** trở lên.
*   **Docker** (nếu chọn khởi chạy theo Cách 2).
*   **Trình duyệt:** Chrome, Edge, Firefox hoặc Safari.

