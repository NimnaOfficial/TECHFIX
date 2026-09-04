-- SEED DATA FOR TECHFIX D1 DATABASE
-- Pre-configured data for testing by Member 2 and Member 3

-- 1. Insert Default Admins and Managers
-- Password for all seed users is 'Password123!' (Pre-hashed with a dummy salt 'seed_salt' for testing purposes, but here we can just use the exact hash if known, or we just rely on creating via the API. However, for direct SQL insert, let's provide basic access. Actually, to test, we can use the API or provide a known hash.)
-- 'seed_salt:seed_hash' -> We'll use a placeholder. To actually login, users might need to register. But we will insert dummy users.
-- For simplicity in the seed, we assume the system will let them register or we'll provide valid hashes if needed.
-- Let's just insert some branches and categories which don't require passwords.

-- 2. Insert Device Categories
INSERT OR IGNORE INTO device_categories (id, name, description, is_active) VALUES
('cat_1', 'Smartphone', 'Mobile phones and smartphones', 1),
('cat_2', 'Laptop', 'Windows and Mac laptops', 1),
('cat_3', 'Tablet', 'iPads and Android tablets', 1),
('cat_4', 'Desktop PC', 'Custom builds and pre-built PCs', 1),
('cat_5', 'Smartwatch', 'Wearable smart devices', 1);

-- 3. Insert Base Services
INSERT OR IGNORE INTO services (id, name, description, category_id, base_price, estimated_duration, is_active) VALUES
('srv_1', 'Screen Replacement (Smartphone)', 'Replace cracked or broken screen', 'cat_1', 4500.00, 60, 1),
('srv_2', 'Battery Replacement (Smartphone)', 'Replace old or degraded battery', 'cat_1', 2500.00, 30, 1),
('srv_3', 'OS Formatting & Reinstall', 'Clean install of Windows/macOS', 'cat_2', 3000.00, 120, 1),
('srv_4', 'Laptop Keyboard Replacement', 'Fix broken keys or damaged keyboard', 'cat_2', 5500.00, 90, 1),
('srv_5', 'Water Damage Diagnostic', 'Full cleaning and diagnostic for water damage', 'cat_1', 1500.00, 120, 1);

-- 4. Insert Branches
INSERT OR IGNORE INTO branches (id, name, address, city, phone, email, latitude, longitude, opening_time, closing_time, is_active) VALUES
('br_1', 'Colombo Main Branch', '123 Galle Road', 'Colombo', '0112345678', 'colombo@techfix.com', 6.9271, 79.8612, '09:00', '18:00', 1),
('br_2', 'Kandy City Center', '45 Dalada Vidiya', 'Kandy', '0812345678', 'kandy@techfix.com', 7.2906, 80.6337, '09:00', '17:00', 1),
('br_3', 'Galle Fort Repair', '12 Fort Road', 'Galle', '0912345678', 'galle@techfix.com', 6.0328, 80.2150, '10:00', '18:00', 1);

-- 5. Insert Spare Parts
INSERT OR IGNORE INTO spare_parts (id, name, part_number, description, unit_price, minimum_stock, is_active) VALUES
('part_1', 'iPhone 13 OLED Screen', 'IP13-SCR-01', 'Original quality OLED replacement', 25000.00, 10, 1),
('part_2', 'Samsung S22 Battery', 'SAM-S22-BAT', 'OEM 3700mAh Battery', 8000.00, 15, 1),
('part_3', '16GB DDR4 RAM', 'RAM-16-D4', '3200MHz DDR4 SODIMM', 12000.00, 20, 1),
('part_4', '512GB NVMe SSD', 'SSD-512-NV', 'Gen 3 NVMe SSD', 15000.00, 20, 1);

-- 6. Link Services to Spare Parts
INSERT OR IGNORE INTO service_parts (service_id, part_id, quantity_required) VALUES
('srv_1', 'part_1', 1),
('srv_2', 'part_2', 1);

-- 7. Branch Spare Part Inventory
INSERT OR IGNORE INTO branch_spare_parts (branch_id, part_id, quantity) VALUES
('br_1', 'part_1', 5),
('br_1', 'part_2', 10),
('br_1', 'part_3', 8),
('br_1', 'part_4', 15),
('br_2', 'part_1', 2),
('br_2', 'part_2', 5);

