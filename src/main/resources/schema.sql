-- ============================================
-- H2 Schema for Local Development
-- computershop database - all entities in one DB
-- ============================================

-- Roles Table
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS images CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS order_details CASCADE;
DROP TABLE IF EXISTS payment_transactions CASCADE;
DROP TABLE IF EXISTS password_reset_tokens CASCADE;

CREATE TABLE roles (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    user_id INT IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    roleid INT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    address VARCHAR(500),
    CONSTRAINT FK_users_roles FOREIGN KEY (roleid) REFERENCES roles(role_id)
);

CREATE TABLE categories (
    category_id INT IDENTITY PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(MAX),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE images (
    image_id INT IDENTITY PRIMARY KEY,
    image_url VARCHAR(MAX) NOT NULL
);

CREATE TABLE products (
    product_id INT IDENTITY PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description VARCHAR(MAX),
    price DECIMAL(18,2) NOT NULL,
    stock_quantity INT NOT NULL,
    category_id INT,
    image_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_products_categories FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT FK_products_images FOREIGN KEY (image_id) REFERENCES images(image_id)
);

CREATE TABLE carts (
    cart_id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_carts_users FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE cart_items (
    cart_item_id INT IDENTITY PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_cart_items_carts FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    CONSTRAINT FK_cart_items_products FOREIGN KEY (product_id) REFERENCES products(product_id),
    CONSTRAINT UQ_cart_product UNIQUE(cart_id, product_id)
);

CREATE TABLE orders (
    order_id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'pending',
    shipping_address VARCHAR(MAX),
    payment_method VARCHAR(50),
    notes VARCHAR(MAX),
    CONSTRAINT FK_orders_users FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_details (
    order_detail_id INT IDENTITY PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT FK_order_details_products FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE payment_transactions (
    payment_transaction_id BIGINT IDENTITY PRIMARY KEY,
    provider VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    receiver_account VARCHAR(64) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    payment_code VARCHAR(2048) NOT NULL,
    note VARCHAR(512),
    order_order_id INT NOT NULL,
    user_user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_payment_orders FOREIGN KEY (order_order_id) REFERENCES orders(order_id),
    CONSTRAINT FK_payment_users FOREIGN KEY (user_user_id) REFERENCES users(user_id)
);

CREATE TABLE password_reset_tokens (
    token_id INT IDENTITY PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    user_user_id INT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_reset_users FOREIGN KEY (user_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
