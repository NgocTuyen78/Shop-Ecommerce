-- ============================================
-- Schema for Orders Database (computershop_orders)
-- Contains: Orders, OrderDetails, Carts, CartItems, Payments
-- PostgreSQL Version
-- ============================================

-- ============================================
-- Orders Table
-- ============================================
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NULL DEFAULT 'pending',
    shipping_address TEXT NULL,
    payment_method VARCHAR(50) NULL,
    notes TEXT NULL
);

-- ============================================
-- Order Details Table
-- ============================================
DROP TABLE IF EXISTS order_details CASCADE;
CREATE TABLE order_details (
    order_detail_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Note: product_id references products in main database

-- ============================================
-- Carts Table
-- ============================================
DROP TABLE IF EXISTS carts CASCADE;
CREATE TABLE carts (
    cart_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Cart Items Table
-- ============================================
DROP TABLE IF EXISTS cart_items CASCADE;
CREATE TABLE cart_items (
    cart_item_id SERIAL PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_cart_items_carts FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE
);

-- Note: product_id references products in main database

-- ============================================
-- Payment Transactions Table
-- ============================================
DROP TABLE IF EXISTS payment_transactions CASCADE;
CREATE TABLE payment_transactions (
    transaction_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_ref VARCHAR(255) NULL,
    CONSTRAINT FK_payment_transactions_orders FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- ============================================
-- Password Reset Tokens Table
-- ============================================
DROP TABLE IF EXISTS password_reset_tokens CASCADE;
CREATE TABLE password_reset_tokens (
    token_id SERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Orders database schema created successfully!
