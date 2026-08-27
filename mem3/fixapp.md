# TECHFIX — Full Project Context & Technical Documentation (v2.0 - Backend Complete)

> **Purpose:** Consolidated project notes and final backend API documentation.  
> **Project:** TECHFIX — device repair/service management application[cite: 4].  
> **Repository:** `NimnaOfficial/TECHFIX`[cite: 4].  
> **Backend:** Cloudflare Workers + Cloudflare D1 (SQLite)[cite: 4].  
> **Frontend Target:** Android Studio (Java/Kotlin) with Retrofit & Room (Offline Cache).

---

## 1. Project Overview

TECHFIX is a comprehensive device repair and service management application[cite: 4]. The system is designed around customers submitting repair appointments for devices, while technicians, managers, and admins manage the entire repair lifecycle[cite: 4].

The backend has been deployed as a Cloudflare Worker and connected successfully to a Cloudflare D1 database[cite: 4]. **As of this update, the Backend REST API is 100% complete and fully operational.**

The system currently supports:
- Customer accounts & Role-based authorization[cite: 4]
- Device categories & Customer device management[cite: 4]
- Service catalogues mapped to specific device categories[cite: 4]
- Branches & Branch-specific spare part inventory[cite: 4]
- Technicians & Technician-specific service skills[cite: 4]
- Technician assignment & Repair appointments[cite: 4]
- Appointment status progression & Repair status history[cite: 4]
- Payments with automated workflow triggers[cite: 4]
- Repair images (Before/After uploads)[cite: 4]
- Automated Notifications[cite: 4]
- Admin Analytics Dashboard

---

## 2. Main Technology Stack

### Mobile / Client
- Android Studio[cite: 4]
- Android application[cite: 4]
- REST API communication via Retrofit/Volley[cite: 4]
- JSON request/response format[cite: 4]
- Local SQLite/Room Database for Offline Mode

### Backend
- Cloudflare Workers[cite: 4]
- Wrangler CLI[cite: 4]
- REST API endpoints[cite: 4]
- JWT Authentication middleware[cite: 4]

### Database
- Cloudflare D1[cite: 4]
- SQLite-compatible SQL database[cite: 4]
- Relational schema with strict foreign keys and CHECK constraints[cite: 4]

---

## 3. Backend Deployment

The deployed backend is active at:
`https://techfix-api.codse251f-003.workers.dev/`[cite: 4]

The Worker is successfully connected to D1[cite: 4]. A health check to `/api/health` confirms `{"connected": 1}`[cite: 4].

---

## 4. Database Schema Structure

The current D1 database contains 15 application tables plus Cloudflare's internal `_cf_KV` table[cite: 4]. The core relationships map as follows:

*   **Users:** Stores CUSTOMER, TECHNICIAN, MANAGER, and ADMIN accounts[cite: 4].
*   **Devices & Categories:** Customers register devices under specific categories (Laptop, Mobile Phone, etc.)[cite: 4].
*   **Branches:** Locations where repairs happen (e.g., Colombo, Galle)[cite: 4].
*   **Services & Spare Parts:** Defines what repairs can be done, their base price, and tracks minimum stock across branches[cite: 4].
*   **Technicians & Technician_Services:** Maps which technician works at which branch, and what specific repairs they are qualified to perform[cite: 4].
*   **Appointments:** The central transactional table tracking the repair request[cite: 4].
*   **History, Payments, Images, Notifications:** Associated tables that track the lifecycle, billing, visual evidence, and user alerts[cite: 4].

---

## 5. Appointment Lifecycle

The strict, fully-tested appointment lifecycle includes[cite: 4]:

`REQUESTED` → `ASSIGNED` → `DEVICE_RECEIVED` → `DIAGNOSING` → `REPAIRING` → `TESTING` → `READY` → `COMPLETED`[cite: 4]

*(Appointments can also be marked as `CANCELLED`).*

---

## 6. Full API Master Reference

The backend API is fully mapped and secured. All routes automatically normalize trailing slashes to prevent 404 errors.

| Method | Endpoint | Auth Role | Module | Purpose |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/health` | Public | System | Checks if API and D1 Database are online. |
| **GET** | `/api/device-categories` | Public | Master Data | Retrieves all active device categories. |
| **GET** | `/api/services` | Public | Master Data | Retrieves all repair services and base prices. |
| **GET** | `/api/branches` | Public | Master Data | Retrieves all TechFix branch locations. |
| **GET** | `/api/branches/:id` | Public | Branch | Retrieves a single branch and its assigned staff. |
| **GET** | `/api/spare-parts` | Public | Master Data | Retrieves the global catalog of spare parts. |
| **POST** | `/api/auth/register` | Public | Auth | Creates a new customer account & hashes password. |
| **POST** | `/api/auth/login` | Public | Auth | Verifies credentials and generates a JWT. |
| **GET** | `/api/me` | Any Token | Profile | Validates token and returns the current user. |
| **PUT** | `/api/profile` | Any Token | Profile | Updates user's first name, last name, and phone. |
| **PUT** | `/api/profile/password` | Any Token | Profile | Verifies old password and securely saves a new one. |
| **GET** | `/api/devices` | Any Token | Devices | Lists all devices owned by the logged-in customer. |
| **POST** | `/api/devices` | Any Token | Devices | Registers a new device. |
| **PUT** | `/api/devices/:id` | Any Token | Devices | Modifies existing device details. |
| **DELETE**| `/api/devices/:id` | Any Token | Devices | Removes a device. |
| **POST** | `/api/appointments` | Customer | Booking | Submits a new repair request. |
| **GET** | `/api/appointments` | Any Token | Tracking | Lists repairs (Customers see own; Staff see all). |
| **GET** | `/api/appointments/:id` | Any Token | Tracking | Gets full details of one repair. |
| **PUT** | `/api/appointments/:id/assign`| Admin/Mgr | Workflow | Assigns a tech and updates tech to BUSY. |
| **PUT** | `/api/appointments/:id/status`| Staff Only | Workflow | Updates repair stage (e.g., DIAGNOSING, READY). |
| **GET** | `/api/appointments/:id/history`| Any Token | Tracking | Retrieves the audit trail of status changes. |
| **POST** | `/api/appointments/:id/images`| Any Token | Camera | Saves an image URL linked to a specific repair. |
| **GET** | `/api/appointments/:id/images`| Any Token | Camera | Retrieves all images uploaded for a repair. |
| **DELETE**| `/api/appointments/:id/images/:imageId`| Staff/User| Camera | Deletes a previously uploaded repair image. |
| **GET** | `/api/technicians` | Admin/Mgr | Staff | Lists all technicians and availability. |
| **GET** | `/api/technician/appointments`| Staff Only | Staff | Lists only the repairs assigned to the logged-in tech. |
| **GET** | `/api/technicians/:id/services`| Any Token | Skills | Lists repair services a tech is qualified for. |
| **PUT** | `/api/technicians/:id/services`| Admin/Mgr | Skills | Updates a technician's skills. |
| **GET** | `/api/branches/:id/spare-parts`| Any Token | Inventory | Retrieves current stock levels at one branch. |
| **POST** | `/api/spare-parts` | Admin/Mgr | Inventory | Adds a new spare part to the global catalog. |
| **POST** | `/api/payments` | Any Token | Billing | Generates a new PENDING invoice. |
| **GET** | `/api/payments` | Admin/Mgr | Billing | Lists all payments globally. |
| **GET** | `/api/appointments/:id/payments`| Any Token | Billing | Retrieves all invoices linked to a repair. |
| **GET** | `/api/payments/:id` | Any Token | Billing | Retrieves details of a specific payment receipt. |
| **PUT** | `/api/payments/:id/status` | Staff Only | Billing | Marks bill PAID, sets final repair price, sends alert. |
| **GET** | `/api/notifications` | Any Token | Alerts | Retrieves the latest 50 notifications for the user. |
| **PUT** | `/api/notifications/:id/read` | Any Token | Alerts | Marks a specific notification as read. |
| **PUT** | `/api/notifications/read-all` | Any Token | Alerts | Marks all unread notifications as read. |
| **GET** | `/api/admin/dashboard` | Admin/Mgr | Analytics | Retrieves global revenue, active repairs, and stats. |

---

## 7. API Development Progress

Every backend requirement has been successfully developed, integrated, and securely tested.

### Completed Modules
- [✓] Database connection & table verification[cite: 4]
- [✓] Device categories, Services, and Branches APIs[cite: 4]
- [✓] Customer authentication, token generation, and password hashing[cite: 4]
- [✓] Customer device creation/update/deletion APIs[cite: 4]
- [✓] Customer profile and password update APIs
- [✓] Appointments creation, retrieval, and full lifecycle tracking[cite: 4]
- [✓] Technician assignment and dashboard APIs[cite: 4]
- [✓] Repair image upload and listing APIs
- [✓] Payment creation, status updates, and automated triggers
- [✓] Notification creation, list, and read APIs
- [✓] Spare-parts management and branch inventory APIs
- [✓] Technician-service capability mapping APIs
- [✓] Admin/Manager dashboard metrics APIs
- [✓] Strict Role-Based Access Control (RBAC) across all endpoints

---

## 8. Current Overall Status & Recommended Next Steps

**The TECHFIX backend and database integration is officially 100% complete.** 

The entire end-to-end workflow—from Customer Registration, Device Management, and Appointment Booking, to Technician Assignment, Repair Processing, and Payment Validation—is fully operational[cite: 4]. The Cloudflare D1 database is responding flawlessly with all foreign keys and constraints intact[cite: 4].

### Recommended Phase: Android Frontend Development
With the backend finalized, all focus should shift to the mobile application environment:
1. **Network Client:** Implement Retrofit/OkHttp to consume the 35+ available API endpoints.
2. **Offline Architecture:** Design the local SQLite/Room database schema to cache `branches`, `services`, and `spare_parts` for offline capability.
3. **Hardware Integration:** Implement the Android CameraX API to capture and upload `BEFORE_REPAIR` and `AFTER_REPAIR` images.
4. **UI/UX:** Build out the intuitive dashboard interfaces using modern Android UI paradigms.

---
> *End of TECHFIX Project Context*