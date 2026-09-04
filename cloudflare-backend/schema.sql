-- TECHFIX D1 Database Schema
-- Maintained by Member 1

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('CUSTOMER', 'TECHNICIAN', 'MANAGER', 'ADMIN')),
    profile_image_url TEXT,
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1)),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. DEVICE CATEGORIES
CREATE TABLE IF NOT EXISTS device_categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1))
);

-- 3. DEVICES
CREATE TABLE IF NOT EXISTS devices (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    category_id TEXT NOT NULL,
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    serial_number TEXT,
    purchase_year INTEGER,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(category_id) REFERENCES device_categories(id)
);

-- 4. SERVICES
CREATE TABLE IF NOT EXISTS services (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    category_id TEXT NOT NULL,
    base_price REAL NOT NULL,
    estimated_duration INTEGER, -- in minutes
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1)),
    FOREIGN KEY(category_id) REFERENCES device_categories(id)
);

-- 5. BRANCHES
CREATE TABLE IF NOT EXISTS branches (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    address TEXT,
    city TEXT,
    phone TEXT,
    email TEXT,
    latitude REAL,
    longitude REAL,
    opening_time TEXT,
    closing_time TEXT,
    manager_id TEXT,
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1)),
    FOREIGN KEY(manager_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 6. TECHNICIANS
CREATE TABLE IF NOT EXISTS technicians (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL UNIQUE,
    employee_code TEXT NOT NULL UNIQUE,
    specialization TEXT,
    branch_id TEXT NOT NULL,
    availability_status TEXT DEFAULT 'AVAILABLE' CHECK(availability_status IN ('AVAILABLE', 'BUSY', 'OFF_DUTY', 'ON_LEAVE')),
    hire_date DATE,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(branch_id) REFERENCES branches(id)
);

-- 7. TECHNICIAN SERVICES
CREATE TABLE IF NOT EXISTS technician_services (
    technician_id TEXT NOT NULL,
    service_id TEXT NOT NULL,
    PRIMARY KEY(technician_id, service_id),
    FOREIGN KEY(technician_id) REFERENCES technicians(id) ON DELETE CASCADE,
    FOREIGN KEY(service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- 8. APPOINTMENTS
CREATE TABLE IF NOT EXISTS appointments (
    id TEXT PRIMARY KEY,
    appointment_number TEXT NOT NULL UNIQUE,
    customer_id TEXT NOT NULL,
    device_id TEXT NOT NULL,
    service_id TEXT NOT NULL,
    branch_id TEXT NOT NULL,
    technician_id TEXT,
    appointment_date DATE NOT NULL,
    appointment_time TEXT,
    problem_description TEXT,
    status TEXT DEFAULT 'REQUESTED' CHECK(status IN ('REQUESTED', 'ASSIGNED', 'DIAGNOSING', 'REPAIRING', 'TESTING', 'COMPLETED', 'CANCELLED')),
    total_cost REAL,
    is_paid INTEGER DEFAULT 0 CHECK(is_paid IN (0, 1)),
    final_price REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(customer_id) REFERENCES users(id),
    FOREIGN KEY(device_id) REFERENCES devices(id),
    FOREIGN KEY(service_id) REFERENCES services(id),
    FOREIGN KEY(branch_id) REFERENCES branches(id),
    FOREIGN KEY(technician_id) REFERENCES technicians(id) ON DELETE SET NULL
);

-- 9. REPAIR STATUS HISTORY
CREATE TABLE IF NOT EXISTS repair_status_history (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    status TEXT NOT NULL,
    note TEXT,
    changed_by TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY(changed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 10. REPAIR IMAGES
CREATE TABLE IF NOT EXISTS repair_images (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    image_url TEXT NOT NULL,
    image_type TEXT,
    uploaded_by TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY(uploaded_by) REFERENCES users(id)
);

-- 11. PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    amount REAL NOT NULL,
    payment_method TEXT,
    payment_status TEXT DEFAULT 'PENDING' CHECK(payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    transaction_reference TEXT UNIQUE,
    paid_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- 12. NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    appointment_id TEXT,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    notification_type TEXT,
    is_read INTEGER DEFAULT 0 CHECK(is_read IN (0, 1)),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- 13. SPARE PARTS
CREATE TABLE IF NOT EXISTS spare_parts (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    part_number TEXT UNIQUE,
    description TEXT,
    unit_price REAL NOT NULL,
    minimum_stock INTEGER DEFAULT 0,
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1))
);

-- 14. BRANCH SPARE PARTS
CREATE TABLE IF NOT EXISTS branch_spare_parts (
    branch_id TEXT NOT NULL,
    part_id TEXT NOT NULL,
    quantity INTEGER DEFAULT 0,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(branch_id, part_id),
    FOREIGN KEY(branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    FOREIGN KEY(part_id) REFERENCES spare_parts(id) ON DELETE CASCADE
);

-- 15. SERVICE PARTS
CREATE TABLE IF NOT EXISTS service_parts (
    service_id TEXT NOT NULL,
    part_id TEXT NOT NULL,
    quantity_required INTEGER DEFAULT 1,
    PRIMARY KEY(service_id, part_id),
    FOREIGN KEY(service_id) REFERENCES services(id) ON DELETE CASCADE,
    FOREIGN KEY(part_id) REFERENCES spare_parts(id) ON DELETE CASCADE
);

-- 16. SYSTEM SETTINGS
CREATE TABLE IF NOT EXISTS system_settings (
    setting_key TEXT PRIMARY KEY,
    setting_value TEXT NOT NULL
);

-- 17. SYSTEM LOGS
CREATE TABLE IF NOT EXISTS system_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    level TEXT NOT NULL,
    method TEXT,
    path TEXT,
    message TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- TRIGGERS to update 'updated_at' automatically
CREATE TRIGGER IF NOT EXISTS update_users_updated_at AFTER UPDATE ON users
FOR EACH ROW BEGIN UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id; END;

CREATE TRIGGER IF NOT EXISTS update_appointments_updated_at AFTER UPDATE ON appointments
FOR EACH ROW BEGIN UPDATE appointments SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id; END;

CREATE TRIGGER IF NOT EXISTS update_branch_spare_parts_updated_at AFTER UPDATE ON branch_spare_parts
FOR EACH ROW BEGIN UPDATE branch_spare_parts SET updated_at = CURRENT_TIMESTAMP WHERE branch_id = OLD.branch_id AND part_id = OLD.part_id; END;
