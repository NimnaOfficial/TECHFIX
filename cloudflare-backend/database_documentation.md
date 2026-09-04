# TECHFIX DATABASE DOCUMENTATION

## Architecture
**Database Engine**: Cloudflare D1 (SQLite-based distributed relational database).
**Design**: 3rd Normal Form (3NF) compliant with enforced Foreign Keys and Check Constraints.

## Schema Highlights

### 1. Users & Roles
- **`users`**: Central table for all authentication.
- **Roles**: Enforced via `CHECK(role IN ('CUSTOMER', 'TECHNICIAN', 'MANAGER', 'ADMIN'))`.
- **Passwords**: Hashed securely; plain text is never stored.

### 2. Device Relationships
- **`devices`**: Links directly to `users` via `user_id` (`FOREIGN KEY` with `ON DELETE CASCADE`).
- **`device_categories`**: Normalizes device types (e.g., Laptops, Smartphones).

### 3. Core Repair Entities
- **`appointments`**: The central transactional table. Links Customer, Device, Service, Branch, and Technician.
- **Status Validation**: Status lifecycle enforced via `CHECK(status IN ('REQUESTED', 'ASSIGNED', 'DIAGNOSING', 'REPAIRING', 'TESTING', 'COMPLETED', 'CANCELLED'))`.
- **`repair_status_history`**: Audit log of every status transition per appointment.

### 4. Personnel & Availability
- **`technicians`**: Links `users` to `branches`. Tracks `availability_status` (AVAILABLE, BUSY, OFF_DUTY).
- **`technician_services`**: Junction table matching technicians to the specific services they are skilled at performing.

### 5. Inventory & Payments
- **`spare_parts`** & **`branch_spare_parts`**: Tracks inventory levels locally per branch.
- **`payments`**: Tracks transaction references and ties payments 1:1 with `appointments`.

## Database Integrity Rules
1. **No Orphaned Records**: Deleting a user safely cascades to their devices and appointments. Deleting a branch removes its stock associations.
2. **Data Consistency**: Check constraints prevent invalid status values or unapproved roles from entering the database.
3. **Audit Trails**: Triggers automatically update `updated_at` timestamps on row modifications.
