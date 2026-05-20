-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DOUBLE NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sample data
INSERT INTO products (name, description, price, category) VALUES
('Laptop', 'High Performance', 999.99, 'Electronics'),
('Mouse', 'Wireless', 29.99, 'Electronics'),
('Desk', 'Wooden', 299.99, 'Furniture');