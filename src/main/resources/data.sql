-- ============================================
-- Seed Data for H2 Local Development
-- ============================================

-- Insert roles
INSERT INTO roles (role_id, role_name) VALUES (1, 'admin');
INSERT INTO roles (role_id, role_name) VALUES (2, 'customer');
INSERT INTO roles (role_id, role_name) VALUES (3, 'staff');
INSERT INTO roles (role_id, role_name) VALUES (4, 'supplier');

-- Insert users
-- Passwords stored as plain + '_hashed' for compatibility with verifyPassword()
-- admin / admin123,  user / user123,  customer1 / 15000,  customer2 / 15000
INSERT INTO users (username, password_hash, roleid, enabled) VALUES
('admin', 'admin123_hashed', 1, TRUE),
('user', 'user123_hashed', 2, TRUE),
('customer1', '15000_hashed', 2, TRUE),
('customer2', '15000_hashed', 2, TRUE),
('staff1', '15000_hashed', 3, TRUE),
('supplier1', '15000_hashed', 4, TRUE);

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
('https://product.hstatic.net/20000/product/thumbchuot_6ed5e43202c9498aacde369cb95573b3_0859ba8bea17000e77b7e6d0c7f0_master.gif'),
('https://owlgaming.vn/wp-content/uploads/2024/06/ARCTIS-7P-1.jpg'),
('https://www.dxracer-europe.com/bilder/artiklar/32125.jpg?m=15000'),
('https://bizweb.dktcdn.net/100/329/122/files/amd-5700g-02.jpg?v=15000'),
('https://dlcdnwebimgs.asus.com/gain/72C16A36-4EE3-4AC4-A58A-35F6B8A2FB6F/w717/h525/fwebp'),
('https://bizweb.dktcdn.net/thumb/grande/100/487/147/products/loa-logitech-speaker-system-z623-eu-246c339d-c1e4-4b1d-9700-1ee364c0ec0d.jpg?v=15000'),
('https://cdn2.cellphones.com.vn/x/media/catalog/product/_/0/_0000_43020_keyboard_corsair_k70_rgb_m.jpg'),
('https://product.hstatic.net/20000/product/g-pro-x-superlight-wireless-black-666_10000ce2e486f9108dbbb17c29159_1450bb4a9bd34dcb92fc77f627eb600d.jpg'),
('https://row.hyperx.com/cdn/shop/products/hyperx_cloud_alpha_s_blackblue_1_main.jpg?v=15000&width=1920'),
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

-- Insert products
INSERT INTO products (product_name, description, price, stock_quantity, category_id, image_id) VALUES
('Logitech G915 Mechanical Keyboard', 'Premium wireless mechanical keyboard with tactile switches, RGB lighting', 18000.00, 50, 1, 1),
('Razer DeathAdder V3 Gaming Mouse', 'Gaming mouse with Focus Pro 30K sensor, 8000Hz polling rate', 15000.00, 75, 2, 2),
('SteelSeries Arctis 7P Headset', 'Wireless gaming headset with 7.1 audio, ClearCast microphone', 12000.00, 30, 3, 3),
('DXRacer Formula Series Gaming Chair', 'Ergonomic gaming chair with memory foam padding, multi-directional adjustment', 10000.00, 15, 4, 4),
('AMD Ryzen 7 5800X CPU', '8-core 16-thread processor, 3.8GHz base clock, 4.7GHz boost', 17000.00, 25, 5, 5),
('ASUS ROG Swift PG279QM Monitor', '27 QHD 240Hz IPS gaming monitor with G-Sync technology', 15000.00, 20, 6, 6),
('Logitech Z623 2.1 Speakers', '2.1 speaker system with 200W power output, THX certified', 20000.00, 40, 7, 7),
('Corsair K70 RGB MK.2 Keyboard', 'Mechanical keyboard with Cherry MX Red switches, aluminum frame', 12000.00, 35, 1, 8),
('Logitech G Pro X Superlight Mouse', 'Ultra-lightweight 63g gaming mouse with HERO 25K sensor', 20000.00, 45, 2, 9),
('HyperX Cloud Alpha S Headset', 'Gaming headset with 50mm drivers, 7.1 surround sound', 20000.00, 60, 3, 10),
('Razer BlackWidow V3 Mechanical Keyboard', 'Gaming mechanical keyboard with Razer Green switches, RGB Chroma', 12000.00, 25, 1, 1),
('Corsair M65 RGB ELITE Gaming Mouse', 'FPS gaming mouse, aluminum frame, adjustable weight', 15000.00, 22, 2, 3),
('SteelSeries Arctis 7 Headset', 'Wireless gaming headset, DTS Headphone:X 2.0', 12000.00, 15, 3, 5),
('HyperX Cloud II Gaming Headset', 'Gaming headset with microphone, virtual 7.1 audio', 15000.00, 30, 3, 6),
('Audio-Technica ATH-M50xBT2 Headphones', 'Bluetooth studio headphones, high-quality audio', 18000.00, 8, 3, 5),
('Noblechairs EPIC Gaming Chair', 'Premium gaming chair, real leather, sturdy steel frame', 15000.00, 3, 4, 7),
('Samsung 980 PRO 1TB SSD', 'NVMe PCIe 4.0 SSD, 7000MB/s read speed', 20000.00, 45, 5, 8),
('Corsair Vengeance LPX 16GB RAM', 'DDR4 3200MHz RAM, 2x8GB kit, aluminum heat spreader', 15000.00, 38, 5, 9),
('MSI RTX 4070 Ti SUPER Graphics Card', 'High-end gaming graphics card, 16GB GDDR6X', 20000.00, 8, 5, 10),
('Kingston NV2 500GB SSD', 'Budget NVMe SSD, good performance for office use', 11000.00, 52, 5, 8),
('G.Skill Ripjaws V 32GB RAM', 'DDR4 3600MHz RAM, 2x16GB kit, high performance', 12000.00, 15, 5, 9),
('Logitech G502 HERO Gaming Mouse', 'Wired gaming mouse, HERO 25K sensor, 11 programmable buttons', 15000.00, 35, 2, 3);
