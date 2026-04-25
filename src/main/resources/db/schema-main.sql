-- ============================================
-- Schema for Main Database (computershop_main)
-- Contains: Roles, Users, Categories, Images, Products
-- PostgreSQL Version
-- ============================================

-- ============================================
-- Roles Table
-- ============================================
DROP TABLE IF EXISTS roles CASCADE;
CREATE TABLE roles (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================
-- Users Table
-- ============================================
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NULL UNIQUE,
    roleid INT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_users_roles FOREIGN KEY (roleid) REFERENCES roles(role_id)
);

-- ============================================
-- Categories Table
-- ============================================
DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Images Table
-- ============================================
DROP TABLE IF EXISTS images CASCADE;
CREATE TABLE images (
    image_id SERIAL PRIMARY KEY,
    image_url TEXT NOT NULL
);

-- ============================================
-- Products Table
-- ============================================
DROP TABLE IF EXISTS products CASCADE;
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

-- ============================================
-- Initial Data Seeding
-- ============================================

-- Insert roles
INSERT INTO roles (role_id, role_name) VALUES
(1, 'admin'),
(2, 'customer'),
(3, 'staff'),
(4, 'supplier');

-- Main database schema created successfully!
