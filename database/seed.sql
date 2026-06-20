USE inventory_db;

-- Clear existing data (in reverse order of dependencies)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE stock_updates;
TRUNCATE TABLE products;
TRUNCATE TABLE suppliers;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Seed Users (passwords are 'Admin@123' and 'Staff@123' respectively)
INSERT INTO users (id, username, password, full_name, role) VALUES
(1, 'admin', '$2b$10$9OTVur.eiBx/QwU7.1eqiO18KbfHn59KWs4Biv.iZMlNJIfOTRmci', 'System Administrator', 'ADMIN'),
(2, 'staff', '$2b$10$BTPn1pdKob7dA1Dp38uTwOBf/lyDMNmDPZliyq5LmWZZtYE.1uSYS', 'Inventory Staff', 'STAFF');

-- 2. Seed Categories
INSERT INTO categories (id, name, description) VALUES
(1, 'Electronics', 'Devices, gadgets, and computer accessories'),
(2, 'Furniture', 'Office and home furniture items'),
(3, 'Apparel', 'Clothing, footwear, and accessories'),
(4, 'Food & Beverages', 'Packaged food items, drinks, and snacks'),
(5, 'Books', 'Educational, fiction, and non-fiction books');

-- 3. Seed Suppliers
INSERT INTO suppliers (id, name, contact, email, phone) VALUES
(1, 'TechDistributors Inc.', 'John Doe', 'john@techdist.com', '123-456-7890'),
(2, 'ComfortFurnishings Ltd.', 'Jane Smith', 'jane@comfortfurn.com', '234-567-8901'),
(3, 'GlobalApparel Corp.', 'Robert Johnson', 'robert@globalapparel.com', '345-678-9012'),
(4, 'FreshFoods Wholesalers', 'Emily Davis', 'emily@freshfoods.com', '456-789-0123'),
(5, 'ApexPublishing', 'Michael Brown', 'michael@apexpub.com', '567-890-1234');

-- 4. Seed Products (Minimum 20 products)
INSERT INTO products (id, name, sku, category_id, supplier_id, price, quantity, min_quantity) VALUES
(1, 'Wireless Mouse', 'ELEC-MOU-001', 1, 1, 25.50, 50, 10),
(2, 'Mechanical Keyboard', 'ELEC-KEY-002', 1, 1, 85.00, 30, 8),
(3, 'USB-C Cable 1m', 'ELEC-CAB-003', 1, 1, 9.99, 120, 15),
(4, '27-inch 4K Monitor', 'ELEC-MON-004', 1, 1, 349.99, 15, 5),
(5, 'Noise Cancelling Headphones', 'ELEC-HP-005', 1, 1, 199.99, 25, 6),
(6, 'Office Chair', 'FURN-CHA-001', 2, 2, 120.00, 20, 5),
(7, 'Standing Desk', 'FURN-DES-002', 2, 2, 299.99, 10, 3),
(8, 'Bookshelf 5-Tier', 'FURN-BS-003', 2, 2, 79.99, 8, 4),
(9, 'Filing Cabinet', 'FURN-CAB-004', 2, 2, 95.00, 12, 5),
(10, 'Running Shoes', 'APP-SHO-001', 3, 3, 65.00, 40, 10),
(11, 'Cotton T-Shirt (M)', 'APP-TSH-002', 3, 3, 15.99, 80, 15),
(12, 'Denim Jeans (32)', 'APP-JNS-003', 3, 3, 45.50, 35, 10),
(13, 'Winter Jacket (L)', 'APP-JAC-004', 3, 3, 89.99, 18, 5),
(14, 'Organic Green Tea', 'FOOD-TEA-001', 4, 4, 6.50, 100, 20),
(15, 'Chocolate Cookies Pack', 'FOOD-CK-002', 4, 4, 3.75, 150, 30),
(16, 'Potato Chips Barbecue', 'FOOD-CHP-003', 4, 4, 2.20, 4, 15), -- Low stock product
(17, 'Sparkling Water 500ml', 'FOOD-WAT-004', 4, 4, 1.50, 200, 25),
(18, 'Introduction to Java', 'BOOK-JAV-001', 5, 5, 49.99, 30, 8),
(19, 'Database System Concepts', 'BOOK-DB-002', 5, 5, 75.00, 15, 5),
(20, 'Learn React in 24 Hours', 'BOOK-REA-003', 5, 5, 29.99, 3, 6), -- Low stock product
(21, 'Design Patterns Cookbook', 'BOOK-DP-004', 5, 5, 55.00, 22, 5);

-- 5. Seed Stock Updates (Minimum 15 records)
-- (updated_by 1 = Admin, 2 = Staff)
INSERT INTO stock_updates (id, product_id, updated_by, change_qty, note) VALUES
(1, 1, 1, 50, 'Initial inventory load'),
(2, 2, 1, 30, 'Initial inventory load'),
(3, 3, 1, 100, 'Initial inventory load'),
(4, 4, 1, 15, 'Initial inventory load'),
(5, 5, 1, 25, 'Initial inventory load'),
(6, 6, 1, 20, 'Initial inventory load'),
(7, 7, 1, 10, 'Initial inventory load'),
(8, 8, 1, 8, 'Initial inventory load'),
(9, 9, 2, 10, 'Received monthly shipment'),
(10, 10, 2, 40, 'Initial inventory load'),
(11, 11, 2, 80, 'Initial inventory load'),
(12, 12, 2, 35, 'Initial inventory load'),
(13, 13, 2, 18, 'Initial inventory load'),
(14, 14, 2, 100, 'Initial inventory load'),
(15, 15, 2, 150, 'Initial inventory load'),
(16, 16, 2, 4, 'Initial inventory load'),
(17, 3, 2, 20, 'Received restock from supplier'),
(18, 9, 1, 2, 'Added leftover stock from display');
