# TECHFIX — Full Project Context & Technical Documentation

> **Purpose:** Consolidated project notes based on the information currently available from our conversations.  
> **Project:** TECHFIX — device repair/service management application  
> **Repository:** `NimnaOfficial/TECHFIX`  
> **Student context:** Android Studio / Mobile Application Development (MAD) coursework  
> **Backend:** Cloudflare Workers + Cloudflare D1 (SQLite)

---

## 1. Project Overview

TECHFIX is a device repair/service management application. The system is designed around customers submitting repair appointments for devices, while technicians/managers/admins manage the repair lifecycle.

The backend has been deployed as a Cloudflare Worker and connected successfully to a Cloudflare D1 database.

The system currently supports:

- Customer accounts
- Device categories
- Customer device management
- Service catalogue
- Branches
- Technicians
- Technician assignment
- Repair appointments
- Appointment status progression
- Repair status history
- Payments
- Repair images
- Notifications
- Spare parts and stock relationships
- Technician/service relationships
- Authentication using tokens
- Role-based users

---

# 2. Main Technology Stack

## Mobile / Client

- Android Studio
- Android application
- REST API communication
- JSON request/response format

## Backend

- Cloudflare Workers
- Wrangler CLI
- REST API endpoints
- Authentication middleware/function
- Cloudflare D1 database

## Database

- Cloudflare D1
- SQLite-compatible SQL database
- Relational schema with foreign keys

## Repository

```text
NimnaOfficial/TECHFIX
```

---

# 3. Backend Deployment

The deployed backend is:

```text
https://techfix-api.codse251f-003.workers.dev/
```

The Worker is successfully connected to D1.

A database connection test returned:

```json
{
  "connected": 1
}
```

The database table discovery endpoint returned:

```json
{
  "success": true,
  "tables": [
    {"name":"_cf_KV"},
    {"name":"appointments"},
    {"name":"branch_spare_parts"},
    {"name":"branches"},
    {"name":"device_categories"},
    {"name":"devices"},
    {"name":"notifications"},
    {"name":"payments"},
    {"name":"repair_images"},
    {"name":"repair_status_history"},
    {"name":"service_parts"},
    {"name":"services"},
    {"name":"spare_parts"},
    {"name":"technician_services"},
    {"name":"technicians"},
    {"name":"users"}
  ]
}
```

---

# 4. Database Schema

The current D1 database contains 15 application tables plus Cloudflare's internal `_cf_KV` table.

## 4.1 users

Stores all application users.

```sql
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
```

Roles:

- CUSTOMER
- TECHNICIAN
- MANAGER
- ADMIN

---

## 4.2 device_categories

Stores device types.

```sql
CREATE TABLE device_categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    image_url TEXT,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Current categories:

| ID | Category |
|---|---|
| CAT-001 | Laptop |
| CAT-002 | Desktop |
| CAT-003 | Mobile Phone |
| CAT-004 | Tablet |

---

## 4.3 devices

Stores customer-owned devices.

```sql
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
```

Example test device:

```text
Brand: Lenovo
Model: LOQ 15
Serial: TEST-12345
Category: Laptop
Purchase Year: 2025
```

One known test device ID:

```text
f4943b4b-815f-4efd-8011-fdcf28a91bc6
```

---

## 4.4 branches

Stores TECHFIX service branches.

```sql
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
```

Current branches:

### BR-001

```text
Name: TechFix Colombo
Address: 125 Galle Road, Colombo 03
City: Colombo
Latitude: 6.9271
Longitude: 79.8612
Phone: 0112345678
Email: colombo@techfix.lk
Opening: 08:30
Closing: 18:00
```

### BR-002

```text
Name: TechFix Galle
Address: 42 Wakwella Road, Galle
City: Galle
Latitude: 6.0329
Longitude: 80.2168
Phone: 0912345678
Email: galle@techfix.lk
Opening: 08:30
Closing: 18:00
```

---

# 5. Services

## services table

```sql
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
```

Current services:

| ID | Category | Service | Days | Base Price (LKR) |
|---|---|---|---:|---:|
| SVC-001 | Laptop | Laptop Screen Replacement | 2 | 25,000 |
| SVC-002 | Laptop | SSD Upgrade | 1 | 12,000 |
| SVC-003 | Laptop | Keyboard Replacement | 2 | 15,000 |
| SVC-004 | Desktop | Operating System Installation | 1 | 8,000 |
| SVC-005 | Mobile Phone | Mobile Screen Replacement | 2 | 30,000 |
| SVC-006 | Mobile Phone | Mobile Battery Replacement | 1 | 12,000 |
| SVC-007 | Mobile Phone | Charging Port Repair | 2 | 10,000 |
| SVC-008 | Tablet | Tablet Charging Repair | 2 | 11,000 |

---

# 6. Technicians

## technicians table

```sql
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
```

Current technician data:

| ID | Employee | Name | Specialization | Status | Branch |
|---|---|---|---|---|---|
| TECH-001 | TF-T001 | Nimal Fernando | Laptop Hardware | AVAILABLE/BUSY depending on current assignment | Colombo |
| TECH-002 | TF-T002 | Sahan Silva | Mobile Phone Repair | BUSY | Colombo |
| TECH-003 | TF-T003 | Amal Perera | Computer Hardware | AVAILABLE | Galle |
| TECH-004 | TF-T004 | Tharindu Jayasinghe | Mobile Phone Repair | AVAILABLE | Galle |

Known technician users:

```text
TECH-001 -> USR-003
TECH-002 -> USR-004
TECH-003 -> USR-005
TECH-004 -> USR-006
```

---

# 7. Technician-Service Relationship

## technician_services

Many-to-many relationship between technicians and services.

```sql
CREATE TABLE technician_services (
    technician_id TEXT NOT NULL,
    service_id TEXT NOT NULL,

    PRIMARY KEY (technician_id, service_id),

    FOREIGN KEY (technician_id) REFERENCES technicians(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);
```

This allows the application to determine which technicians can perform which services.

---

# 8. Spare Parts

## spare_parts

```sql
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
```

## service_parts

Connects services with required spare parts.

```sql
CREATE TABLE service_parts (
    service_id TEXT NOT NULL,
    spare_part_id TEXT NOT NULL,
    quantity_required INTEGER NOT NULL DEFAULT 1,

    PRIMARY KEY (service_id, spare_part_id),

    FOREIGN KEY (service_id) REFERENCES services(id),
    FOREIGN KEY (spare_part_id) REFERENCES spare_parts(id)
);
```

## branch_spare_parts

Stores branch-level stock.

```sql
CREATE TABLE branch_spare_parts (
    branch_id TEXT NOT NULL,
    spare_part_id TEXT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (branch_id, spare_part_id),

    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (spare_part_id) REFERENCES spare_parts(id)
);
```

---

# 9. Appointments

## appointments table

This is one of the central tables of TECHFIX.

```sql
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
```

---

# 10. Appointment Lifecycle

The tested appointment lifecycle includes:

```text
REQUESTED
    ↓
ASSIGNED
    ↓
DIAGNOSING
    ↓
REPAIRING
    ↓
TESTING
    ↓
COMPLETED
```

The following transitions have been successfully tested:

```text
DIAGNOSING → REPAIRING
REPAIRING → TESTING
TESTING → COMPLETED
```

Technician assignment is also working.

---

# 11. Known Test Appointment

A successfully created test appointment:

```text
Appointment ID:
55f77027-e70e-4238-82d4-1ef9bb07b306

Appointment Number:
TF-20260823085851-75BF50A8

Customer:
John Perera

Customer ID:
fd1c0efd-e40d-4fca-828c-a0328b900694

Device:
Lenovo LOQ 15

Device ID:
f4943b4b-815f-4efd-8011-fdcf28a91bc6

Service:
Laptop Screen Replacement

Service ID:
SVC-001

Branch:
TechFix Colombo

Branch ID:
BR-001

Technician:
Nimal Fernando

Technician ID:
TECH-001

Requested Date:
2026-08-25

Requested Time:
10:30

Problem:
Laptop screen is cracked and showing display lines.

Estimated Price:
LKR 25,000
```

After assignment, the API returned:

```json
{
  "success": true,
  "message": "Technician assigned successfully"
}
```

The appointment status became:

```text
ASSIGNED
```

---

# 12. Customer

Known test customer:

```text
Name: John Perera
Role: CUSTOMER
User ID: fd1c0efd-e40d-4fca-828c-a0328b900694
```

The customer was used to create the test appointment and obtain a customer authentication token.

The exact original password is NOT confirmed in the currently available project context. Do not guess it.

---

# 13. Repair Status History

## repair_status_history

```sql
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
```

For the test appointment, the initial history record was:

```json
{
  "status": "REQUESTED",
  "note": "Appointment created by customer",
  "changed_by": "fd1c0efd-e40d-4fca-828c-a0328b900694"
}
```

The API successfully returned the history.

---

# 14. Repair Images

```sql
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
```

Supported image types:

- CUSTOMER_PROBLEM
- BEFORE_REPAIR
- DURING_REPAIR
- AFTER_REPAIR

---

# 15. Payments

```sql
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
```

Payment methods:

```text
CASH
CARD
ONLINE
```

Payment statuses:

```text
PENDING
PAID
FAILED
REFUNDED
```

---

# 16. Notifications

```sql
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
```

Notifications can optionally be linked to an appointment.

---

# 17. Database Relationships

High-level relationship structure:

```text
USERS
 │
 ├──< DEVICES
 │       │
 │       └── DEVICE_CATEGORIES
 │
 ├──< APPOINTMENTS
 │       │
 │       ├── DEVICE
 │       ├── SERVICE
 │       ├── BRANCH
 │       ├── TECHNICIAN
 │       │
 │       ├──< REPAIR_STATUS_HISTORY
 │       ├──< REPAIR_IMAGES
 │       ├──< PAYMENTS
 │       └──< NOTIFICATIONS
 │
 └── TECHNICIAN
         │
         ├── BRANCH
         └──< TECHNICIAN_SERVICES >── SERVICES
                                      │
                                      └──< SERVICE_PARTS >── SPARE_PARTS
                                                             │
                                                             └──< BRANCH_SPARE_PARTS >── BRANCHES
```

---

# 18. Tested API Functionality

The following functionality has already been tested successfully during development.

## Database connection

Working:

```text
Database connection test
```

Response:

```json
{
  "connected": 1
}
```

## List tables

Working.

## Get device categories

Working.

Returned:

```text
Laptop
Desktop
Mobile Phone
Tablet
```

## Get services

Working.

Returned all current services and their categories.

## Get branches

Working.

Returned:

```text
TechFix Colombo
TechFix Galle
```

## Device creation/update

Working.

Example update response:

```json
{
  "success": true,
  "message": "Device updated successfully"
}
```

## Appointment creation

Working.

Response included:

```json
{
  "success": true,
  "message": "Appointment created successfully"
}
```

## Get appointments

Working.

## Get appointment by ID

Working.

Example:

```text
GET /api/appointments/55f77027-e70e-4238-82d4-1ef9bb07b306
```

## Appointment history

Working after using the correct endpoint.

Example:

```text
GET /api/appointments/55f77027-e70e-4238-82d4-1ef9bb07b306/history
```

Returned the REQUESTED history record.

## Get technicians

Working.

Returned four technicians.

## Technician assignment

Working.

Example result:

```text
Technician assigned successfully
```

Technician:

```text
TECH-001
Nimal Fernando
```

## Status updates

Working:

```text
DIAGNOSING
REPAIRING
TESTING
COMPLETED
```

---

# 19. Authentication

The backend uses token-based authentication.

A reusable:

```text
authenticate()
```

function was planned/created as part of the API authentication implementation.

The API accepts authorization headers using:

```text
Authorization: Bearer <TOKEN>
```

The application has different user roles:

```text
CUSTOMER
TECHNICIAN
MANAGER
ADMIN
```

Role-specific access is intended for protected operations.

---

# 20. Admin / Manager / Technician Testing

A manager/admin authentication token was obtained during backend testing.

Technician list endpoint was tested successfully.

Technician assignment was successfully tested using an administrative/manager-level authentication flow.

The exact admin/manager credentials should be kept private and should not be stored in public project documentation.

---

# 21. Cloudflare Wrangler

Wrangler version used:

```text
4.125.0
```

Operating environment:

```text
Windows 11 Pro for Workstations
Node.js 24
PowerShell
```

---

# 22. Wrangler Authentication Issue and Solution

Initially Wrangler OAuth failed because Windows reserved port 8976.

The error was:

```text
listen EACCES: permission denied ::1:8976
```

Windows showed the following excluded port range:

```text
8974 - 9073
```

Therefore 8976 was unavailable.

There was also an environment issue:

```text
C:\Users\SANDANIMNE\.env
```

was a directory rather than a `.env` file.

The OAuth issue was bypassed by authenticating Wrangler using:

```powershell
$env:CLOUDFLARE_API_TOKEN="YOUR_TOKEN"
```

Then:

```powershell
npx wrangler whoami
```

successfully showed:

```text
You are logged in with an Account API Token
```

This confirmed Wrangler authentication was working.

---

# 23. D1 Export Issue

The attempted export command was:

```powershell
npx wrangler d1 export techfix-db --remote --output=techfix-db.sql
```

The command reached the D1 export operation but failed with:

```text
Authentication error [code: 10000]
```

Wrangler also reported:

```text
Unable to get membership roles.
Are you missing the User->Memberships->Read permission?
```

This indicates the API token used for `whoami` did not have sufficient permissions for the D1 export operation.

The next required step is to create/update the Cloudflare API token with appropriate account/D1 permissions and then retry:

```powershell
npx wrangler d1 list
```

then:

```powershell
npx wrangler d1 info techfix-db
```

and finally:

```powershell
npx wrangler d1 export techfix-db --remote --output=techfix-db.sql
```

---

# 24. Important Security Rules

Never commit these to a public GitHub repository:

```text
CLOUDFLARE_API_TOKEN
JWT tokens
User passwords
Database credentials
Private API keys
Full production database exports
Password hashes
```

The full database export should remain private.

A schema-only SQL file is more suitable for public coursework repositories.

Recommended structure:

```text
database/
├── techfix-schema.sql
└── README.md
```

Private:

```text
techfix-db.sql
```

---

# 25. Suggested Project Structure

A suitable overall structure is:

```text
TECHFIX/
│
├── android/
│   ├── app/
│   ├── gradle/
│   └── ...
│
├── backend/
│   ├── src/
│   ├── wrangler.toml
│   └── package.json
│
├── database/
│   ├── techfix-schema.sql
│   └── README.md
│
├── docs/
│   ├── API.md
│   ├── DATABASE.md
│   └── PROJECT.md
│
└── README.md
```

The exact current repository structure has not been fully inspected in this conversation, so the above is a recommended organization rather than a confirmed existing structure.

---

# 26. API Development Progress

### Completed / Working

```text
[✓] Database connection
[✓] Database table verification
[✓] Device categories API
[✓] Services API
[✓] Branches API
[✓] Customer authentication/token
[✓] Reusable authentication handling
[✓] Customer device creation/update
[✓] Appointments creation
[✓] Get appointments
[✓] Get appointment by ID
[✓] Appointment history
[✓] Get technicians
[✓] Technician assignment
[✓] Appointment status progression
[✓] DIAGNOSING
[✓] REPAIRING
[✓] TESTING
[✓] COMPLETED
```

### Areas that may still need implementation/testing

Based on the current conversation, these areas were not all confirmed as fully implemented:

```text
[ ] Customer profile API
[ ] Device deletion API
[ ] Appointment cancellation
[ ] Appointment confirmation
[ ] DEVICE_RECEIVED transition
[ ] READY transition
[ ] Repair image upload/list APIs
[ ] Payment creation
[ ] Payment status/update APIs
[ ] Notification creation/list/read APIs
[ ] Spare-parts management APIs
[ ] Branch inventory APIs
[ ] Technician-service management APIs
[ ] Technician-specific appointment APIs
[ ] Manager dashboard APIs
[ ] Admin management APIs
[ ] Full role-based authorization audit
[ ] Production error handling audit
[ ] Input validation audit
[ ] API documentation
[ ] Full integration testing
```

These are not confirmed missing from the source code; they are simply not confirmed as completed in the currently available conversation context.

---

# 27. Current Test Scenario

The main successful test flow was:

```text
1. Customer logs in
        ↓
2. Customer obtains authentication token
        ↓
3. Customer has a device
        ↓
4. Customer selects service
        ↓
5. Customer selects branch
        ↓
6. Customer creates appointment
        ↓
7. Appointment status = REQUESTED
        ↓
8. Manager/Admin obtains technician list
        ↓
9. Technician assigned
        ↓
10. Appointment status = ASSIGNED
        ↓
11. Technician starts diagnosis
        ↓
12. DIAGNOSING
        ↓
13. REPAIRING
        ↓
14. TESTING
        ↓
15. COMPLETED
```

This core workflow has been successfully demonstrated.

---

# 28. Important IDs from Testing

## Customer

```text
John Perera
USER ID:
fd1c0efd-e40d-4fca-828c-a0328b900694
```

## Device

```text
Device ID:
f4943b4b-815f-4efd-8011-fdcf28a91bc6
```

## Appointment

```text
Appointment ID:
55f77027-e70e-4238-82d4-1ef9bb07b306

Appointment Number:
TF-20260823085851-75BF50A8
```

## Service

```text
SVC-001
Laptop Screen Replacement
```

## Branch

```text
BR-001
TechFix Colombo
```

## Technician

```text
TECH-001
Nimal Fernando
TF-T001
```

---

# 29. Example Appointment Creation

The tested appointment represented:

```json
{
  "device_id": "f4943b4b-815f-4efd-8011-fdcf28a91bc6",
  "service_id": "SVC-001",
  "branch_id": "BR-001",
  "requested_date": "2026-08-25",
  "requested_time": "10:30",
  "customer_latitude": 6.9271,
  "customer_longitude": 79.8612,
  "problem_description": "Laptop screen is cracked and showing display lines."
}
```

The server automatically associated:

```text
customer_id
appointment ID
appointment number
estimated price
created_at
updated_at
```

---

# 30. Data Integrity

The database uses:

- Primary keys
- Unique constraints
- Foreign keys
- CHECK constraints
- Default values
- Timestamp fields

Examples:

```text
users.email -> UNIQUE
appointments.appointment_number -> UNIQUE
technicians.employee_code -> UNIQUE
payments.transaction_reference -> UNIQUE
```

The appointment status is protected with a CHECK constraint.

Payment method and payment status are also protected with CHECK constraints.

---

# 31. Recommended Next Development Order

For a clean completion strategy, continue in this order:

## Phase 1 — Core customer functionality

```text
1. Customer profile
2. Device list
3. Device delete
4. Appointment list
5. Appointment details
6. Appointment cancellation
```

## Phase 2 — Repair tracking

```text
7. Status history
8. Repair images
9. Technician details
10. Customer repair tracking
```

## Phase 3 — Technician functionality

```text
11. Technician dashboard
12. Assigned appointments
13. Update repair status
14. Add repair notes
15. Upload repair images
```

## Phase 4 — Manager/Admin functionality

```text
16. Technician management
17. Technician assignment
18. Branch management
19. Service management
20. Spare-part management
21. Appointment management
```

## Phase 5 — Payments and notifications

```text
22. Payment creation
23. Payment status
24. Notification creation
25. Notification list
26. Mark notification as read
```

## Phase 6 — Finalization

```text
27. Authentication audit
28. Authorization audit
29. Validation
30. Error handling
31. Database export
32. API documentation
33. Android integration testing
34. Final CW testing
```

---

# 32. Current Overall Status

The TECHFIX backend/database integration is at a strong working stage.

The most important end-to-end workflow is already working:

```text
Customer
   ↓
Authentication
   ↓
Device
   ↓
Service
   ↓
Branch
   ↓
Appointment
   ↓
Manager/Admin
   ↓
Technician Assignment
   ↓
Diagnosis
   ↓
Repair
   ↓
Testing
   ↓
Completed
```

The Cloudflare D1 database is connected and responding correctly.

The remaining work is primarily expanding/testing the secondary features such as payments, notifications, repair images, spare parts, management dashboards, and final authorization/integration testing.

---

# 33. Notes About Accuracy

This document intentionally distinguishes between:

- **Confirmed working functionality** — directly demonstrated in our conversation.
- **Known database structure** — based on the schema provided.
- **Recommended next work** — not necessarily missing from the current code.

The exact Android source-code structure, complete endpoint list, exact JWT implementation, and exact original customer password are not available in the current conversation context and therefore are not fabricated here.

---

## End of TECHFIX Project Context
