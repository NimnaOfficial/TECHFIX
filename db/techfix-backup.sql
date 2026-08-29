PRAGMA defer_foreign_keys=TRUE;
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'CUSTOMER'
        CHECK (role IN ('CUSTOMER', 'TECHNICIAN', 'MANAGER', 'ADMIN')),
    profile_image_url TEXT,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-001','Admin','TechFix','admin@techfix.lk','0771000001','DEMO_HASH_ADMIN','ADMIN',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-002','Kasun','Perera','manager.colombo@techfix.lk','0771000002','DEMO_HASH_MANAGER','MANAGER',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-003','Nimal','Fernando','nimal@techfix.lk','0771000003','DEMO_HASH_TECH1','TECHNICIAN',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-004','Sahan','Silva','sahan@techfix.lk','0771000004','DEMO_HASH_TECH2','TECHNICIAN',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-005','Amal','Perera','amal@techfix.lk','0771000005','DEMO_HASH_TECH3','TECHNICIAN',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-006','Tharindu','Jayasinghe','tharindu@techfix.lk','0771000006','DEMO_HASH_TECH4','TECHNICIAN',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-007','Kavindu','Perera','kavindu@gmail.com','0712345678','DEMO_HASH_CUSTOMER1','CUSTOMER',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-008','Dinithi','Fernando','dinithi@gmail.com','0723456789','DEMO_HASH_CUSTOMER2','CUSTOMER',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('USR-009','Ravindu','Silva','ravindu@gmail.com','0754567890','DEMO_HASH_CUSTOMER3','CUSTOMER',NULL,1,'2026-08-22 15:47:00','2026-08-22 15:47:00');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('fd1c0efd-e40d-4fca-828c-a0328b900694','UpdatedName','UpdatedLastName','john@example.com','0779999999','d5395ef1-8904-44d4-82a4-86dd6655d08b:ab5f62e1cb66abc1f84ef348b0853b401645ffbe0979b0d6a5099e214d15171f','CUSTOMER',NULL,1,'2026-08-22 16:31:46','2026-08-23 13:24:31');
INSERT INTO "users" ("id","first_name","last_name","email","phone","password_hash","role","profile_image_url","is_active","created_at","updated_at") VALUES('31d87879-921f-44a7-8a89-72d937410431','Admin','TechFix','admin@techfix.test','0770000000','39205cee-4049-4752-a930-accb9b8ea988:afec679c56e829b79908510f19adcb797dcdabe1ed46f229db7797c6763479fd','ADMIN',NULL,1,'2026-08-23 10:11:06','2026-08-23 10:11:06');
CREATE TABLE branches (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    city TEXT NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    phone TEXT,
    email TEXT,
    opening_time TEXT,
    closing_time TEXT,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO "branches" ("id","name","address","city","latitude","longitude","phone","email","opening_time","closing_time","is_active","created_at") VALUES('BR-001','TechFix Colombo','125 Galle Road, Colombo 03','Colombo',6.9271,79.8612,'0112345678','colombo@techfix.lk','08:30','18:00',1,'2026-08-22 15:47:11');
INSERT INTO "branches" ("id","name","address","city","latitude","longitude","phone","email","opening_time","closing_time","is_active","created_at") VALUES('BR-002','TechFix Galle','42 Wakwella Road, Galle','Galle',6.0329,80.2168,'0912345678','galle@techfix.lk','08:30','18:00',1,'2026-08-22 15:47:11');
CREATE TABLE device_categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    image_url TEXT,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO "device_categories" ("id","name","description","image_url","is_active","created_at") VALUES('CAT-001','Laptop','Laptop computers and notebooks',NULL,1,'2026-08-22 15:47:20');
INSERT INTO "device_categories" ("id","name","description","image_url","is_active","created_at") VALUES('CAT-002','Desktop','Desktop computers and workstations',NULL,1,'2026-08-22 15:47:20');
INSERT INTO "device_categories" ("id","name","description","image_url","is_active","created_at") VALUES('CAT-003','Mobile Phone','Smartphones and mobile devices',NULL,1,'2026-08-22 15:47:20');
INSERT INTO "device_categories" ("id","name","description","image_url","is_active","created_at") VALUES('CAT-004','Tablet','Tablet computers',NULL,1,'2026-08-22 15:47:20');
CREATE TABLE devices (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    category_id TEXT NOT NULL,
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    serial_number TEXT,
    purchase_year INTEGER,
    notes TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES device_categories(id)
);
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('DEV-001','USR-007','CAT-001','Lenovo','LOQ 15IRX9','LNV-LOQ-001',2025,'Gaming laptop with display issue','2026-08-22 15:47:49');
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('DEV-002','USR-007','CAT-003','Samsung','Galaxy A55','SAM-A55-001',2024,'Battery drains quickly','2026-08-22 15:47:49');
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('DEV-003','USR-008','CAT-003','Apple','iPhone 15','APL-IP15-001',2024,'Cracked display','2026-08-22 15:47:49');
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('DEV-004','USR-008','CAT-002','Dell','OptiPlex 7090','DLL-OPT-001',2022,'System running slowly','2026-08-22 15:47:49');
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('DEV-005','USR-009','CAT-004','Samsung','Galaxy Tab S9','SAM-TAB-S9-001',2024,'Charging problem','2026-08-22 15:47:49');
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('18197e74-3d35-476e-8890-b419cd0355f2','fd1c0efd-e40d-4fca-828c-a0328b900694','CAT-001','Lenovo','LOQ 15','LOQ-ABC123456',2025,'Gaming laptop','2026-08-23 07:59:06');
INSERT INTO "devices" ("id","user_id","category_id","brand","model","serial_number","purchase_year","notes","created_at") VALUES('f4943b4b-815f-4efd-8011-fdcf28a91bc6','fd1c0efd-e40d-4fca-828c-a0328b900694','CAT-001','Lenovo','LOQ 15','LOQ-ABC123456',2025,'Gaming laptop','2026-08-23 08:09:02');
CREATE TABLE services (
    id TEXT PRIMARY KEY,
    category_id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    estimated_days INTEGER DEFAULT 1,
    base_price REAL NOT NULL DEFAULT 0,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES device_categories(id)
);
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-001','CAT-001','Laptop Screen Replacement','Replacement of damaged laptop display',2,25000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-002','CAT-001','SSD Upgrade','Upgrade laptop storage with a new SSD',1,12000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-003','CAT-001','Keyboard Replacement','Replacement of damaged laptop keyboard',2,15000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-004','CAT-002','Operating System Installation','Windows operating system installation and configuration',1,8000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-005','CAT-003','Mobile Screen Replacement','Replacement of damaged smartphone display',2,30000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-006','CAT-003','Mobile Battery Replacement','Replacement of degraded smartphone battery',1,12000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-007','CAT-003','Charging Port Repair','Repair or replacement of damaged charging port',2,10000,1,'2026-08-22 15:47:58');
INSERT INTO "services" ("id","category_id","name","description","estimated_days","base_price","is_active","created_at") VALUES('SVC-008','CAT-004','Tablet Charging Repair','Diagnosis and repair of tablet charging problems',2,11000,1,'2026-08-22 15:47:58');
CREATE TABLE technicians (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL UNIQUE,
    branch_id TEXT NOT NULL,
    employee_code TEXT NOT NULL UNIQUE,
    specialization TEXT,
    availability_status TEXT NOT NULL DEFAULT 'AVAILABLE'
        CHECK (availability_status IN
        ('AVAILABLE', 'BUSY', 'OFF_DUTY', 'ON_LEAVE')),
    hire_date TEXT,
    is_active INTEGER NOT NULL DEFAULT 1,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id)
);
INSERT INTO "technicians" ("id","user_id","branch_id","employee_code","specialization","availability_status","hire_date","is_active") VALUES('TECH-001','USR-003','BR-001','TF-T001','Laptop Hardware','BUSY','2024-01-15',1);
INSERT INTO "technicians" ("id","user_id","branch_id","employee_code","specialization","availability_status","hire_date","is_active") VALUES('TECH-002','USR-004','BR-001','TF-T002','Mobile Phone Repair','BUSY','2024-03-10',1);
INSERT INTO "technicians" ("id","user_id","branch_id","employee_code","specialization","availability_status","hire_date","is_active") VALUES('TECH-003','USR-005','BR-002','TF-T003','Computer Hardware','AVAILABLE','2024-05-20',1);
INSERT INTO "technicians" ("id","user_id","branch_id","employee_code","specialization","availability_status","hire_date","is_active") VALUES('TECH-004','USR-006','BR-002','TF-T004','Mobile Phone Repair','AVAILABLE','2025-01-05',1);
CREATE TABLE technician_services (
    technician_id TEXT NOT NULL,
    service_id TEXT NOT NULL,

    PRIMARY KEY (technician_id, service_id),

    FOREIGN KEY (technician_id) REFERENCES technicians(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-001','SVC-001');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-001','SVC-002');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-001','SVC-003');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-001','SVC-004');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-002','SVC-005');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-002','SVC-006');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-002','SVC-007');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-003','SVC-002');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-003','SVC-003');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-003','SVC-004');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-004','SVC-005');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-004','SVC-006');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-004','SVC-007');
INSERT INTO "technician_services" ("technician_id","service_id") VALUES('TECH-004','SVC-008');
CREATE TABLE spare_parts (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    part_number TEXT UNIQUE,
    description TEXT,
    unit_price REAL NOT NULL DEFAULT 0,
    minimum_stock INTEGER NOT NULL DEFAULT 5,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-001','Lenovo LOQ Display','LCD-LOQ-15','15.6 inch compatible display',22000,2,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-002','1TB NVMe SSD','SSD-NVME-1TB','1TB NVMe solid state drive',18000,3,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-003','Lenovo Laptop Keyboard','KB-LNV-001','Replacement Lenovo keyboard',9000,2,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-004','Windows License','WIN-11-PRO','Windows operating system license',25000,2,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-005','iPhone 15 Display','DSP-IP15','Replacement iPhone 15 display',28000,2,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-006','Samsung A55 Battery','BAT-A55','Replacement Samsung Galaxy A55 battery',8000,2,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-007','USB-C Charging Port','PORT-USBC','Universal USB-C charging port component',3500,5,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-008','Tablet Battery','BAT-TAB-001','Replacement tablet battery',7500,2,1,'2026-08-22 15:48:24');
INSERT INTO "spare_parts" ("id","name","part_number","description","unit_price","minimum_stock","is_active","created_at") VALUES('PART-600057','MacBook Pro Battery','BAT-MAC-M1','Replacement battery for M1 MacBook Pro',45000,2,1,'2026-08-23 13:26:40');
CREATE TABLE branch_spare_parts (
    branch_id TEXT NOT NULL,
    spare_part_id TEXT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (branch_id, spare_part_id),

    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (spare_part_id) REFERENCES spare_parts(id)
);
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-001',5,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-002',10,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-003',4,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-004',8,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-005',3,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-006',6,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-007',12,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-001','PART-008',1,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-001',1,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-002',5,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-003',6,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-004',4,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-005',0,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-006',4,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-007',8,'2026-08-22 15:48:33');
INSERT INTO "branch_spare_parts" ("branch_id","spare_part_id","quantity","updated_at") VALUES('BR-002','PART-008',5,'2026-08-22 15:48:33');
CREATE TABLE service_parts (
    service_id TEXT NOT NULL,
    spare_part_id TEXT NOT NULL,
    quantity_required INTEGER NOT NULL DEFAULT 1,

    PRIMARY KEY (service_id, spare_part_id),

    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (spare_part_id) REFERENCES spare_parts(id)
);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-001','PART-001',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-002','PART-002',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-003','PART-003',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-004','PART-004',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-005','PART-005',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-006','PART-006',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-007','PART-007',1);
INSERT INTO "service_parts" ("service_id","spare_part_id","quantity_required") VALUES('SVC-008','PART-007',1);
CREATE TABLE appointments (
    id TEXT PRIMARY KEY,
    appointment_number TEXT NOT NULL UNIQUE,

    customer_id TEXT NOT NULL,
    device_id TEXT NOT NULL,
    service_id TEXT NOT NULL,

    branch_id TEXT,
    technician_id TEXT,

    requested_date TEXT NOT NULL,
    requested_time TEXT,

    customer_latitude REAL,
    customer_longitude REAL,

    problem_description TEXT NOT NULL,

    status TEXT NOT NULL DEFAULT 'REQUESTED'
        CHECK (status IN (
            'REQUESTED',
            'CONFIRMED',
            'ASSIGNED',
            'DEVICE_RECEIVED',
            'DIAGNOSING',
            'REPAIRING',
            'TESTING',
            'READY',
            'COMPLETED',
            'CANCELLED'
        )),

    estimated_price REAL,
    final_price REAL,

    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (device_id) REFERENCES devices(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (technician_id) REFERENCES technicians(id)
);
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('APT-001','TF-2026-0001','USR-007','DEV-001','SVC-001','BR-001','TECH-001','2026-08-22','10:00',6.9271,79.8612,'Laptop screen is flickering and has several dead pixels.','REPAIRING',25000,NULL,'2026-08-22 15:48:49','2026-08-22 15:48:49');
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('APT-002','TF-2026-0002','USR-008','DEV-003','SVC-005','BR-001','TECH-002','2026-08-21','11:30',6.91,79.85,'Phone display is cracked after accidental drop.','READY',30000,32000,'2026-08-22 15:48:49','2026-08-22 15:48:49');
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('APT-003','TF-2026-0003','USR-007','DEV-002','SVC-006','BR-001','TECH-002','2026-08-23','09:30',6.925,79.86,'Battery capacity has significantly decreased.','CONFIRMED',12000,NULL,'2026-08-22 15:48:49','2026-08-22 15:48:49');
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('APT-004','TF-2026-0004','USR-008','DEV-004','SVC-004','BR-002','TECH-003','2026-08-20','14:00',6.05,80.22,'Desktop requires operating system reinstallation.','COMPLETED',8000,8000,'2026-08-22 15:48:49','2026-08-22 15:48:49');
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('APT-005','TF-2026-0005','USR-009','DEV-005','SVC-008','BR-002','TECH-004','2026-08-24','13:00',6.04,80.21,'Tablet does not charge properly.','REQUESTED',11000,NULL,'2026-08-22 15:48:49','2026-08-22 15:48:49');
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('878b3195-f77d-457c-bee6-88a9d2f506f1','TF-20260823085850-512339E6','fd1c0efd-e40d-4fca-828c-a0328b900694','f4943b4b-815f-4efd-8011-fdcf28a91bc6','SVC-001','BR-001',NULL,'2026-08-25','10:30',6.9271,79.8612,'Laptop screen is cracked and showing display lines.','REQUESTED',25000,NULL,'2026-08-23 08:58:50','2026-08-23 08:58:50');
INSERT INTO "appointments" ("id","appointment_number","customer_id","device_id","service_id","branch_id","technician_id","requested_date","requested_time","customer_latitude","customer_longitude","problem_description","status","estimated_price","final_price","created_at","updated_at") VALUES('55f77027-e70e-4238-82d4-1ef9bb07b306','TF-20260823085851-75BF50A8','fd1c0efd-e40d-4fca-828c-a0328b900694','f4943b4b-815f-4efd-8011-fdcf28a91bc6','SVC-001','BR-001','TECH-001','2026-08-25','10:30',6.9271,79.8612,'Laptop screen is cracked and showing display lines.','COMPLETED',25000,25000,'2026-08-23 08:58:51','2026-08-23 11:02:29');
CREATE TABLE repair_status_history (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    status TEXT NOT NULL,
    note TEXT,
    changed_by TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (changed_by) REFERENCES users(id)
);
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-001','APT-001','REQUESTED','Repair request submitted by customer.','USR-007','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-002','APT-001','CONFIRMED','Appointment confirmed by TechFix.','USR-002','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-003','APT-001','DEVICE_RECEIVED','Device received at Colombo branch.','USR-003','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-004','APT-001','DIAGNOSING','Technician started device diagnosis.','USR-003','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-005','APT-001','REPAIRING','Replacement display installation started.','USR-003','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-006','APT-002','REQUESTED','Repair request submitted.','USR-008','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-007','APT-002','CONFIRMED','Appointment confirmed.','USR-002','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-008','APT-002','REPAIRING','New display installed.','USR-004','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-009','APT-002','TESTING','Device testing completed.','USR-004','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-010','APT-002','READY','Device is ready for collection.','USR-004','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-011','APT-004','REQUESTED','Repair request submitted.','USR-008','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('HIST-012','APT-004','COMPLETED','Operating system installation completed.','USR-005','2026-08-22 15:49:08');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('7c0bcd04-001a-4602-8cd8-cc50944ef071','878b3195-f77d-457c-bee6-88a9d2f506f1','REQUESTED','Appointment created by customer','fd1c0efd-e40d-4fca-828c-a0328b900694','2026-08-23 08:58:50');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('f0b12cc7-3120-4c2f-a08c-d93b678bd7be','55f77027-e70e-4238-82d4-1ef9bb07b306','REQUESTED','Appointment created by customer','fd1c0efd-e40d-4fca-828c-a0328b900694','2026-08-23 08:58:51');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('ef5e7e3a-bce5-46a2-a2b1-ae914bf4e9c0','55f77027-e70e-4238-82d4-1ef9bb07b306','ASSIGNED','Appointment assigned to technician Nimal Fernando','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 10:27:11');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('365d3a8b-8dd8-4ffe-b911-4b23e1f6c1f6','55f77027-e70e-4238-82d4-1ef9bb07b306','ASSIGNED','Appointment assigned to technician Nimal Fernando','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 10:34:05');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('4206ec72-7c2a-404d-8446-8444c5373624','55f77027-e70e-4238-82d4-1ef9bb07b306','DEVICE_RECEIVED','Laptop received at TechFix Colombo branch.','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 10:55:45');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('6d2af2de-9bcc-4d78-8023-158d167de49d','55f77027-e70e-4238-82d4-1ef9bb07b306','DIAGNOSING','Technician is inspecting the cracked screen and display cables.','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 10:59:01');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('e5dde43e-da97-4271-b811-4faef0736328','55f77027-e70e-4238-82d4-1ef9bb07b306','REPAIRING','Replacing the LED panel. Spare parts logged.','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 10:59:11');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('6c0dcc75-3619-4ce8-ad6d-3b2dce3a7325','55f77027-e70e-4238-82d4-1ef9bb07b306','REPAIRING','Replacing the LED panel. Spare parts logged.','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 10:59:13');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('a089c0a3-7383-4a7c-b152-0c4460c19565','55f77027-e70e-4238-82d4-1ef9bb07b306','READY','Repair completed and device passed testing. Ready for customer collection.','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 11:02:03');
INSERT INTO "repair_status_history" ("id","appointment_id","status","note","changed_by","created_at") VALUES('7735878f-8c40-49e9-abc0-dba5d631580e','55f77027-e70e-4238-82d4-1ef9bb07b306','COMPLETED','Device collected by customer. Repair completed successfully.','31d87879-921f-44a7-8a89-72d937410431','2026-08-23 11:02:29');
CREATE TABLE payments (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    amount REAL NOT NULL,
    payment_method TEXT NOT NULL
        CHECK (payment_method IN
        ('CASH', 'CARD', 'ONLINE')),
    payment_status TEXT NOT NULL DEFAULT 'PENDING'
        CHECK (payment_status IN
        ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    transaction_reference TEXT UNIQUE,
    paid_at TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);
INSERT INTO "payments" ("id","appointment_id","amount","payment_method","payment_status","transaction_reference","paid_at","created_at") VALUES('PAY-001','APT-002',32000,'CARD','PAID','TXN-20260821-001','2026-08-21 16:30:00','2026-08-22 15:49:15');
INSERT INTO "payments" ("id","appointment_id","amount","payment_method","payment_status","transaction_reference","paid_at","created_at") VALUES('PAY-002','APT-004',8000,'CASH','PAID','CASH-20260820-001','2026-08-20 17:15:00','2026-08-22 15:49:15');
INSERT INTO "payments" ("id","appointment_id","amount","payment_method","payment_status","transaction_reference","paid_at","created_at") VALUES('PAY-003','APT-001',25000,'ONLINE','PENDING',NULL,NULL,'2026-08-22 15:49:15');
INSERT INTO "payments" ("id","appointment_id","amount","payment_method","payment_status","transaction_reference","paid_at","created_at") VALUES('5d58de76-3c4f-46cb-9b87-8c50093f229e','55f77027-e70e-4238-82d4-1ef9bb07b306',25000,'ONLINE','PAID',NULL,'2026-08-23 12:04:51','2026-08-23 12:02:52');
CREATE TABLE repair_images (
    id TEXT PRIMARY KEY,
    appointment_id TEXT NOT NULL,
    image_url TEXT NOT NULL,
    image_type TEXT NOT NULL
        CHECK (image_type IN
        ('CUSTOMER_PROBLEM',
         'BEFORE_REPAIR',
         'DURING_REPAIR',
         'AFTER_REPAIR')),
    uploaded_by TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
);
INSERT INTO "repair_images" ("id","appointment_id","image_url","image_type","uploaded_by","created_at") VALUES('IMG-001','APT-001','https://placehold.co/800x600?text=Damaged+Laptop+Screen','CUSTOMER_PROBLEM','USR-007','2026-08-22 15:49:24');
INSERT INTO "repair_images" ("id","appointment_id","image_url","image_type","uploaded_by","created_at") VALUES('IMG-002','APT-001','https://placehold.co/800x600?text=Before+Repair','BEFORE_REPAIR','USR-003','2026-08-22 15:49:24');
INSERT INTO "repair_images" ("id","appointment_id","image_url","image_type","uploaded_by","created_at") VALUES('IMG-003','APT-002','https://placehold.co/800x600?text=Cracked+iPhone','CUSTOMER_PROBLEM','USR-008','2026-08-22 15:49:24');
INSERT INTO "repair_images" ("id","appointment_id","image_url","image_type","uploaded_by","created_at") VALUES('IMG-004','APT-002','https://placehold.co/800x600?text=Repaired+iPhone','AFTER_REPAIR','USR-004','2026-08-22 15:49:24');
CREATE TABLE notifications (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    appointment_id TEXT,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    notification_type TEXT,
    is_read INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);
INSERT INTO "notifications" ("id","user_id","appointment_id","title","message","notification_type","is_read","created_at") VALUES('NOT-001','USR-007','APT-001','Repair Started','Your laptop repair TF-2026-0001 has started.','REPAIR_STATUS',0,'2026-08-22 15:49:32');
INSERT INTO "notifications" ("id","user_id","appointment_id","title","message","notification_type","is_read","created_at") VALUES('NOT-002','USR-008','APT-002','Repair Ready','Your iPhone repair TF-2026-0002 is ready for collection.','REPAIR_STATUS',0,'2026-08-22 15:49:32');
INSERT INTO "notifications" ("id","user_id","appointment_id","title","message","notification_type","is_read","created_at") VALUES('NOT-003','USR-008','APT-004','Repair Completed','Your desktop repair TF-2026-0004 has been completed.','REPAIR_STATUS',1,'2026-08-22 15:49:32');
INSERT INTO "notifications" ("id","user_id","appointment_id","title","message","notification_type","is_read","created_at") VALUES('NOT-004','USR-007','APT-001','Payment Pending','Payment of LKR 25,000 is pending for your repair.','PAYMENT',0,'2026-08-22 15:49:32');
INSERT INTO "notifications" ("id","user_id","appointment_id","title","message","notification_type","is_read","created_at") VALUES('8e39cea7-d175-46af-8826-95a25850f125','fd1c0efd-e40d-4fca-828c-a0328b900694','55f77027-e70e-4238-82d4-1ef9bb07b306','Payment Received','Payment of LKR 25000 received for repair TF-20260823085851-75BF50A8. Thank you!','PAYMENT',0,'2026-08-23 12:04:51');
