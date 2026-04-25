-- ============================================
-- JSB ComputerShop - Complete Database Setup (PostgreSQL)
-- ============================================
-- This script includes:
-- 1. Database creation and user setup (Note: requires prior database/user creation)
-- 2. Schema creation
-- 3. Initial data seeding
-- 4. Price update for testing (10,000 - 15,000 VND)
-- 5. User password fix queries
-- ============================================

-- ============================================
-- PART 1: Database and User Setup
-- ============================================
-- Note: In PostgreSQL, database and role creation must be done outside of transaction
-- Run these commands separately as superuser (usually 'postgres'):
-- 
-- DROP DATABASE IF EXISTS computershop;
-- DROP ROLE IF EXISTS hai;
-- CREATE ROLE hai WITH LOGIN PASSWORD 'hai';
-- CREATE DATABASE computershop OWNER hai;
--
-- For this script, we assume the database and user already exist

-- ============================================
-- PART 2: Database Schema
-- ============================================

DROP TABLE IF EXISTS password_reset_tokens CASCADE;
DROP TABLE IF EXISTS order_details CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS images CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- Roles Table
CREATE TABLE roles (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- Users Table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    roleid INT NULL,
    CONSTRAINT FK_users_roles FOREIGN KEY (roleid) REFERENCES roles(role_id)
);

-- Categories Table
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Images Table
CREATE TABLE images (
    image_id SERIAL PRIMARY KEY,
    image_url TEXT NOT NULL
);

-- Products Table
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    price DECIMAL(18,2) NOT NULL,
    stock_quantity INT NOT NULL,
    category_id INT NULL,
    image_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_products_categories FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT FK_products_images FOREIGN KEY (image_id) REFERENCES images(image_id)
);

-- Carts Table
CREATE TABLE carts (
    cart_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_carts_users FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Cart Items Table
CREATE TABLE cart_items (
    cart_item_id SERIAL PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_cart_items_carts FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    CONSTRAINT FK_cart_items_products FOREIGN KEY (product_id) REFERENCES products(product_id),
    CONSTRAINT UQ_cart_product UNIQUE(cart_id, product_id)
);

-- Orders Table
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NULL DEFAULT 'pending',

    shipping_address NVARCHAR(MAX) NULL,
    payment_method VARCHAR(50) NULL,
    notes NVARCHAR(MAX) NULL,
    CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES dbo.users(user_id)

);

-- Order Details Table
CREATE TABLE order_details (
    order_detail_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT FK_order_details_products FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Password Reset Tokens Table
CREATE TABLE password_reset_tokens (
    token_id SERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_password_reset_tokens_users FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- PART 3: Initial Data Seeding
-- ============================================

-- Insert roles
INSERT INTO roles (role_id, role_name) VALUES
(1, 'admin'),
(2, 'customer'),
(3, 'staff'),
(4, 'supplier');

-- Insert users (password format: plain_password + '_hashed')
-- Login credentials:
-- admin / admin123
-- user / user123
-- customer1 / 123456
-- customer2 / 123456
INSERT INTO users (username, password_hash, email, roleid) VALUES
('admin', 'admin123_hashed', 'admin@computershop.com', 1),
('user', 'user123_hashed', 'user@computershop.com', 2),
('customer1', '123456_hashed', 'customer1@example.com', 2),
('customer2', '123456_hashed', 'customer2@example.com', 2),
('staff1', '123456_hashed', 'staff1@example.com', 3),
('supplier1', '123456_hashed', 'supplier1@example.com', 4);

-- Insert categories
INSERT INTO categories (category_name, description) VALUES
('Keyboards', 'Computer keyboards, mechanical keyboards, gaming keyboards'),
('Mice', 'Computer mice, gaming mice, wireless mice'),
('Headsets', 'Gaming headsets, audio headphones, communication headsets'),
('Gaming Chairs', 'Gaming chairs, office chairs, ergonomic seating'),
('Components', 'CPU, RAM, GPU, motherboard and computer components'),
('Monitors', 'Computer monitors, gaming displays, LCD, LED screens'),
('Speakers', 'Computer speakers, gaming audio, sound systems');

-- Insert images
INSERT INTO images (image_url) VALUES
('https://www.logitechg.com/content/dam/gaming/en/products/g915/g915-gallery-2.png'),
('https://product.hstatic.net/200000722513/product/thumbchuot_6ed5e43202c9498aacde369cb95573b3_0859ba8bea744152819e77b7e6d0c7f0_master.gif'),
('https://owlgaming.vn/wp-content/uploads/2024/06/ARCTIS-7P-1.jpg'),
('https://www.dxracer-europe.com/bilder/artiklar/32125.jpg?m=1747120248'),
('https://bizweb.dktcdn.net/100/329/122/files/amd-5700g-02.jpg?v=1633579298069'),
('https://dlcdnwebimgs.asus.com/gain/72C16A36-4EE3-4AC4-A58A-35F6B8A2FB6F/w717/h525/fwebp'),
('https://bizweb.dktcdn.net/thumb/grande/100/487/147/products/loa-logitech-speaker-system-z623-eu-246c339d-c1e4-4b1d-9700-1ee364c0ec0d.jpg?v=1691140134430'),
('https://cdn2.cellphones.com.vn/x/media/catalog/product/_/0/_0000_43020_keyboard_corsair_k70_rgb_m.jpg'),
('https://product.hstatic.net/200000722513/product/g-pro-x-superlight-wireless-black-666_83650815ce2e486f9108dbbb17c29159_1450bb4a9bd34dcb92fc77f627eb600d.jpg'),
('https://row.hyperx.com/cdn/shop/products/hyperx_cloud_alpha_s_blackblue_1_main.jpg?v=1662567757&width=1920'),
('/Images/keyboard1.jpg'),
('/Images/keyboard2.jpg'),
('/Images/mouse1.jpg'),
('/Images/mouse2.jpg'),
('/Images/headset1.jpg'),
('/Images/headset2.jpg'),
('/Images/chair1.jpg'),
('/Images/ssd1.jpg'),
('/Images/ram1.jpg'),
('/Images/gpu1.jpg');

-- Insert products (initial prices - will be updated later for testing)
INSERT INTO products (product_name, description, price, stock_quantity, category_id, image_id) VALUES
('Logitech G915 Mechanical Keyboard', 'Premium wireless mechanical keyboard with tactile switches, RGB lighting', 4200000, 50, 1, 1),
('Razer DeathAdder V3 Gaming Mouse', 'Gaming mouse with Focus Pro 30K sensor, 8000Hz polling rate', 1890000, 75, 2, 2),
('SteelSeries Arctis 7P Headset', 'Wireless gaming headset with 7.1 audio, ClearCast microphone', 3200000, 30, 3, 3),
('DXRacer Formula Series Gaming Chair', 'Ergonomic gaming chair with memory foam padding, multi-directional adjustment', 8500000, 15, 4, 4),
('AMD Ryzen 7 5800X CPU', '8-core 16-thread processor, 3.8GHz base clock, 4.7GHz boost', 7200000, 25, 5, 5),
('ASUS ROG Swift PG279QM Monitor', '27" QHD 240Hz IPS gaming monitor with G-Sync technology', 15800000, 20, 6, 6),
('Logitech Z623 2.1 Speakers', '2.1 speaker system with 200W power output, THX certified', 2800000, 40, 7, 7),
('Corsair K70 RGB MK.2 Keyboard', 'Mechanical keyboard with Cherry MX Red switches, aluminum frame', 3100000, 35, 1, 8),
('Logitech G Pro X Superlight Mouse', 'Ultra-lightweight 63g gaming mouse with HERO 25K sensor', 2650000, 45, 2, 9),
('HyperX Cloud Alpha S Headset', 'Gaming headset with 50mm drivers, 7.1 surround sound', 2400000, 60, 3, 10),
('Razer BlackWidow V3 Mechanical Keyboard', 'Gaming mechanical keyboard with Razer Green switches, RGB Chroma', 3200000, 25, 1, 1),
('Corsair K70 RGB MK.2 Keyboard', 'Mechanical keyboard with Cherry MX Red switches, aluminum frame', 2850000, 18, 1, 2),
('Logitech G915 TKL Keyboard', 'Wireless mechanical keyboard, GL Tactile switches, ultra-slim design', 4200000, 12, 1, 1),
('Logitech G502 HERO Gaming Mouse', 'Wired gaming mouse, HERO 25K sensor, 11 programmable buttons', 1450000, 35, 2, 3),
('Razer DeathAdder V3 Gaming Mouse', 'Ergonomic gaming mouse, Focus Pro 30K sensor', 1650000, 28, 2, 4),
('Corsair M65 RGB ELITE Gaming Mouse', 'FPS gaming mouse, aluminum frame, adjustable weight', 1380000, 22, 2, 3),
('SteelSeries Arctis 7 Headset', 'Wireless gaming headset, DTS Headphone:X 2.0', 3800000, 15, 3, 5),
('HyperX Cloud II Gaming Headset', 'Gaming headset with microphone, virtual 7.1 audio', 1950000, 30, 3, 6),
('Audio-Technica ATH-M50xBT2 Headphones', 'Bluetooth studio headphones, high-quality audio', 4200000, 8, 3, 5),
('DXRacer Formula Series Gaming Chair', 'Ergonomic gaming chair, premium PU leather, multi-directional adjustment', 8500000, 6, 4, 7),
('Noblechairs EPIC Gaming Chair', 'Premium gaming chair, real leather, sturdy steel frame', 15200000, 3, 4, 7),
('Samsung 980 PRO 1TB SSD', 'NVMe PCIe 4.0 SSD, 7000MB/s read speed', 2850000, 45, 5, 8),
('Corsair Vengeance LPX 16GB RAM', 'DDR4 3200MHz RAM, 2x8GB kit, aluminum heat spreader', 1650000, 38, 5, 9),
('MSI RTX 4070 Ti SUPER Graphics Card', 'High-end gaming graphics card, 16GB GDDR6X', 25500000, 8, 5, 10),
('Kingston NV2 500GB SSD', 'Budget NVMe SSD, good performance for office use', 950000, 52, 5, 8),
('G.Skill Ripjaws V 32GB RAM', 'DDR4 3600MHz RAM, 2x16GB kit, high performance', 3200000, 15, 5, 9);

-- Update some products to have low stock for testing
UPDATE products SET stock_quantity = 2 WHERE product_name LIKE '%Noblechairs%';
UPDATE products SET stock_quantity = 4 WHERE product_name LIKE '%Audio-Technica%';
UPDATE products SET stock_quantity = 0 WHERE product_name LIKE '%G915 TKL%'; -- Out of stock for testing

-- ============================================
-- PART 4: Update Product Prices for Testing
-- ============================================
-- Update all products with prices between 10,000 - 15,000 VND for testing payment functionality
-- Using formula: 10000 + (product_id % 6) * 1000 to get prices: 10000, 11000, 12000, 13000, 14000, 15000
UPDATE products 
SET price = CAST(10000 + (product_id % 6) * 1000 AS DECIMAL(18,2))
WHERE product_id IS NOT NULL;

-- ============================================
-- PART 5: User Password Fix Queries
-- ============================================
-- Fix password for users with password not hashed (doesn't have '_hashed')
-- Only update users with password not in correct format
UPDATE users 
SET password_hash = password_hash || '_hashed'
WHERE password_hash NOT LIKE '%_hashed' AND password_hash NOT LIKE '%_hashed_hashed';

-- ============================================
-- PART 6: Verification Queries
-- ============================================

-- Verify users and their roles
SELECT u.user_id, u.username, u.email, r.role_name 
FROM users u 
LEFT JOIN roles r ON u.roleid = r.role_id
ORDER BY u.user_id;

-- Verify products with new prices
SELECT 
    product_id, 
    product_name, 
    price,
    stock_quantity,
    CASE 
        WHEN price BETWEEN 10000 AND 15000 THEN 'OK'
        ELSE 'CHECK'
    END AS price_status
FROM products 
ORDER BY product_id;

-- Show price distribution summary
SELECT 
    COUNT(*) AS total_products,
    MIN(price) AS min_price,
    MAX(price) AS max_price,
    AVG(price) AS avg_price
FROM products;

-- Verify categories
SELECT category_id, category_name, COUNT(p.product_id) AS product_count
FROM categories c
LEFT JOIN products p ON c.category_id = p.category_id
GROUP BY c.category_id, c.category_name
ORDER BY c.category_id;

-- Database setup completed successfully!
-- Default login credentials:
--   Admin: admin / admin123
--   User: user / user123
