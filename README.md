🛍️ Shop-Ecommerce
Hệ thống website thương mại điện tử bán phụ kiện điện tử được xây dựng bằng Java Spring Boot.

✨ Tính năng nổi bật
    - Mua sắm: Xem sản phẩm, giỏ hàng, thanh toán trực tuyến qua VNPay.
    - Quản trị (Admin): Quản lý sản phẩm, danh mục, đơn hàng và thống kê doanh thu.
    - Bảo mật: Phân quyền người dùng (Admin/User) rõ ràng.
    - Linh hoạt: Hỗ trợ chạy trên nhiều loại Database (H2, PostgreSQL).

🌐 Truy cập trực tuyến (Demo)
Bạn có thể trải nghiệm nhanh hệ thống đã được triển khai tại:
👉 https://shop-ecommerce-rhry.onrender.com/

🚀 Hướng dẫn khởi chạy (Local)
Cách 1: Chạy nhanh (Dùng Database ảo H2 - Không cần cài đặt)
Mở terminal tại thư mục dự án và chạy lệnh:

Windows: .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"

macOS / Linux: ./mvnw spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"

Truy cập: http://localhost:2345

Cách 2: Chạy với PostgreSQL (Dùng Docker)
Bật Database: docker compose -f docker/docker-compose.single.yml up postgres -d

Chạy ứng dụng: .\mvnw.cmd spring-boot:run

Truy cập: http://localhost:2345

🔑 Tài khoản mặc định
| Role    | Username    | Password  |
|---------|-------------|-----------|
| Admin   | admin       | admin123  |
| User    | user        | user123   |

🛠 Yêu cầu hệ thống
    - Java 17 trở lên
    - Docker (nếu chạy theo Cách 2)

