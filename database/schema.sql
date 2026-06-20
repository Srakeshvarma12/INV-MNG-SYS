CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

-- Drop all old and new tables in reverse order of foreign key dependencies to avoid constraint errors
DROP TABLE IF EXISTS stock_updates;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- 1. users
CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,       -- BCrypt hashed
    full_name   VARCHAR(100) NOT NULL,
    role        ENUM('ADMIN', 'STAFF') NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. categories
CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 3. suppliers
CREATE TABLE suppliers (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    contact     VARCHAR(100),
    email       VARCHAR(100),
    phone       VARCHAR(20)
);

-- 4. products
CREATE TABLE products (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    sku          VARCHAR(50)  NOT NULL UNIQUE,
    category_id  INT NOT NULL,
    supplier_id  INT,
    price        DECIMAL(10,2) NOT NULL,
    quantity     INT DEFAULT 0,
    min_quantity INT DEFAULT 5,             -- low-stock threshold
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

-- 5. stock_updates
CREATE TABLE stock_updates (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    product_id    INT NOT NULL,
    updated_by    INT NOT NULL,             -- FK to users.id
    change_qty    INT NOT NULL,             -- positive = add, negative = remove
    note          TEXT,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE CASCADE
);
