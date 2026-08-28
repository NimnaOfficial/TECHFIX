# **Device Repair Management System — Comprehensive Project Specification**

## **1\. Project Overview & Architecture**

The **Device Repair & Maintenance Management System** is an end-to-end multi-role enterprise application designed to streamline customer service bookings, technician workflows, inventory management, billing, and administrative oversight for device repair operations across multiple branches.

       \+-------------------------------------------------------+  
       |                  Frontend Web / Mobile                |  
       |  (Splash Screen, Auth Forms, Dashboards, Booking UI)  |  
       \+---------------------------+---------------------------+  
                                   | HTTP / REST API (JSON)  
                                   v  
       \+-------------------------------------------------------+  
       |             Cloudflare Worker API Engine              |  
       |  \- Custom Router & Web Crypto Authentication (JWT)    |  
       |  \- CORS, Middleware, & Business Logic Controllers     |  
       \+---------------------------+---------------------------+  
                                   | D1 SQL Query Engine  
                                   v  
       \+-------------------------------------------------------+  
       |               Cloudflare D1 Database                  |  
       |   (Relational SQL Schema with FK Constraints)         |  
       \+-------------------------------------------------------+

### **Tech Stack**

* **Database Layer:** Cloudflare D1 (SQLite-compatible edge database).  
* **Backend Runtime:** Cloudflare Workers (JavaScript, Web Crypto API).  
* **Authentication:** Web Crypto API standard implementations using PBKDF2 password derivation and custom JWT (HS256) session generation.  
* **Frontend Layer:** HTML5, CSS3, JavaScript (Fetch API / Async Web Client).

## **2\. Complete Database Model**

The application relies on a relational database architecture enforcing standard foreign key integrity.

| Table Name | Primary Purpose | Key Fields |
| :---- | :---- | :---- |
| users  | Stores identity and credential data for all system users | id, email, password\_hash, full\_name, phone, role (CUSTOMER, TECHNICIAN, MANAGER, ADMIN) |
| branches  | Tracks physical shop locations | id, name, address, phone, email  |
| device\_categories  | Classifies repairable hardware (e.g., Laptops, Phones) | id, name, description  |
| devices  | Registers specific devices owned by users | id, user\_id, category\_id, brand, model, serial\_number  |
| services  | Defines catalog of repair services offered | id, name, description, estimated\_duration, base\_cost  |
| technicians  | Maps users with TECHNICIAN roles to specific branches | id, user\_id, branch\_id, status, specialization  |
| technician\_services  | Many-to-many lookup table for technician skillsets | technician\_id, service\_id  |
| spare\_parts  | Global catalog of replacement parts | id, name, part\_number, cost\_price, selling\_price  |
| branch\_spare\_parts  | Localized inventory counts per branch location | branch\_id, part\_id, quantity, reorder\_level  |
| service\_parts  | Maps specific parts required to perform a service | service\_id, part\_id, quantity  |
| appointments  | Tracks repair orders and workflow progress | id, customer\_id, branch\_id, device\_id, service\_id, technician\_id, appointment\_date, status (PENDING, CONFIRMED, IN\_PROGRESS, COMPLETED, CANCELLED), total\_cost  |
| repair\_status\_history  | Audit log for tracking state changes in repairs | id, appointment\_id, status, notes, updated\_by, created\_at  |
| payments  | Financial transaction ledger | id, appointment\_id, amount, payment\_method, status, transaction\_id  |
| repair\_images  | Links diagnostic and completed photo uploads | id, appointment\_id, image\_url, image\_type  |
| notifications  | System alerts generated for user updates | id, user\_id, title, message, is\_read  |

## **3\. Work Completed To Date (Member 1 Scope)**

As Member 1, you have designed, built, and implemented the entire database layer and serverless REST API backend architecture.

### **Database & Schema Engineering**

* Engineered 15 SQL tables with primary keys, standard data types, explicit constraints, foreign key mappings, and seed data scripts.  
* Defined multi-role support (CUSTOMER, TECHNICIAN, MANAGER, ADMIN).

### **Custom Serverless Core Infrastructure**

* Implemented PBKDF2 key derivation for hashing passwords without relying on external Node modules.  
* Built a custom JSON Web Token (JWT) generator and verifier using standard crypto.subtle APIs.  
* Structured global CORS validation, request body parsers, standard HTTP error responses, and database interaction bindings.

### **Backend API Route Implementations**

* **Authentication & Profile:** /api/auth/register, /api/auth/login, /api/me, /api/profile, /api/profile/password.  
* **Public Information Catalog:** /api/health, /api/device-categories, /api/services, /api/branches.  
* **Device Management:** /api/devices (GET, POST), /api/devices/:id (PUT, DELETE).  
* **Appointments & Repairs:** /api/appointments (GET, POST with automated technician routing), /api/appointments/:id (GET, PUT), /api/appointments/:id/status, /api/appointments/:id/images (GET, POST).  
* **Management & Admin:** /api/technicians (GET, POST), /api/technicians/:id/services, /api/dashboard/summary.  
* **Inventory & Billing:** /api/spare-parts (GET, POST), /api/spare-parts/inventory, /api/payments (GET, POST), /api/notifications.

## **4\. Pending Tasks & Remaining Roadmap**

### **Immediate Tasks for Member 1 (Backend & Integration)**

* **Frontend Hookup:** Wire up the newly written index.html (Splash Screen & Login Form) to interact seamlessly with /api/auth/login.  
* **R2 / Storage Integration:** Replace base64/mock image URL strings in /api/appointments/:id/images with direct uploads to Cloudflare R2 object storage.  
* **Automated Email/SMS Triggering:** Implement external API hooks (e.g., SendGrid, Twilio) when appointment status updates trigger in /api/appointments/:id/status.  
* **Unit Testing:** Write automated integration test suites using Vitest or Jest to validate API endpoints against edge cases.

### **Future Work for Other Team Members (Frontend & Ops)**

* **Customer Portal UI:** Responsive views for booking repairs, managing saved devices, viewing real-time repair progress, and paying bills online.  
* **Technician Mobile Dashboard:** Workstation UI allowing technicians to upload diagnostics photos, update status timelines, and adjust parts inventory on the fly.  
* **Admin Analytics Portal:** Analytics screen to visualize total sales, branch revenues, active repair bottlenecks, and inventory reorder alerts using data from /api/dashboard/summary.

## **5\. Team Division Matrix**

| Responsibility Area | Primary Lead | Status |
| :---- | :---- | :---- |
| Database Schema & D1 Migrations | **Member 1** | **Completed**  |
| Serverless API Routes & Controllers | **Member 1** | **Completed**  |
| Authentication System (Crypto/JWT) | **Member 1** | **Completed**  |
| Splash Screen & Auth UI Code | **Member 1** | **Code Ready / In Progress** |
| Customer Booking UI Interface | Frontend Lead | Pending |
| Technician Workflow Dashboard UI | Frontend Lead | Pending |
| Admin Analytics & Reporting UI | Frontend Lead | Pending |
| Storage Bucket (R2) & Image Upload Pipeline | **Member 1** / Ops | Pending |
| Payment Gateway Integration (Stripe/PayPal) | **Member 1** / Backend | Pending |

Would you like to focus next on connecting the payment gateway logic to the /api/payments backend endpoint, or setting up the cloud storage bindings for diagnostic images?