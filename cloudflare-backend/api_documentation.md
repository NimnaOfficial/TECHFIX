# TECHFIX BACKEND API DOCUMENTATION

## 1. Authentication
All protected routes require a JWT token in the Authorization header.
`Authorization: Bearer <token>`

### 1.1 Customer Registration
```text
Endpoint: POST /api/auth/register
Purpose: Register a new customer
Authentication: None
Allowed roles: ANY
Request:
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john@example.com",
  "password": "Password123!",
  "phone": "0771234567"
}
Response: 201 Created
{ "success": true, "message": "User registered" }
Errors: 400 (Missing fields), 409 (Email exists)
```

### 1.2 User Login
```text
Endpoint: POST /api/auth/login
Purpose: Authenticate user and return JWT
Authentication: None
Allowed roles: ANY
Request: { "email": "john@example.com", "password": "Password123!" }
Response: 200 OK
{ "success": true, "token": "...", "user": { ... } }
Errors: 401 (Invalid credentials)
```

## 2. Customer Routes
### 2.1 Get Profile
```text
Endpoint: GET /api/me
Purpose: Fetch logged-in user profile
Authentication: Required
Allowed roles: ANY
```

### 2.2 Customer Devices
```text
Endpoint: GET /api/devices
Purpose: List customer's devices (enforced by user.id)
Authentication: Required
Allowed roles: CUSTOMER
```
*(Also supports POST, PUT /api/devices/:id, DELETE /api/devices/:id)*

## 3. Appointments (Core Workflow)
### 3.1 Create Appointment
```text
Endpoint: POST /api/appointments
Purpose: Customer creates a new repair appointment
Authentication: Required
Allowed roles: CUSTOMER
Request: { "device_id": "...", "service_id": "...", "branch_id": "...", "appointment_date": "YYYY-MM-DD", "problem_description": "..." }
```

### 3.2 List Appointments
```text
Endpoint: GET /api/appointments
Purpose: Retrieve appointments. Customers see only theirs. Managers see their branch's. Admins see all.
Authentication: Required
Allowed roles: CUSTOMER, MANAGER, ADMIN
```

### 3.3 Assign Technician
```text
Endpoint: PUT /api/appointments/{id}/assign
Purpose: Assign an appointment to an available technician.
Authentication: Required
Allowed roles: MANAGER, ADMIN
Request: { "technician_id": "..." }
Response: Updates appointment to 'ASSIGNED', changes technician to 'BUSY'.
```

### 3.4 Update Status
```text
Endpoint: PUT /api/appointments/{id}/status
Purpose: Update repair status through lifecycle (e.g., DIAGNOSING, REPAIRING, COMPLETED)
Authentication: Required
Allowed roles: TECHNICIAN, MANAGER, ADMIN
```

## 4. Admin / Management Support
### 4.1 Dashboard Metrics
```text
Endpoint: GET /api/admin/dashboard
Purpose: Fetch business overview metrics (active repairs, pending, revenue)
Authentication: Required
Allowed roles: MANAGER, ADMIN
```

### 4.2 Branch Management
```text
Endpoint: GET /api/branches, POST /api/branches
Purpose: Branch CRUD
Authentication: POST requires ADMIN
```

### 4.3 Spare Parts & Inventory
```text
Endpoint: GET /api/spare-parts, POST /api/spare-parts
Purpose: Manage spare parts inventory
Authentication: Required (ADMIN/MANAGER for mutations)
```

## 5. Security Notes
- All endpoints use parameterized SQL queries via Cloudflare D1 (`.bind()`) to strictly prevent SQL Injection.
- Password hashes use PBKDF2 with unique salts.
- Users cannot bypass Data Ownership boundaries (e.g., `WHERE user_id = ?` is hardcoded on Customer routes).
