# TECHFIX — Member 2 Complete Responsibility Guide

**Project:** TECHFIX Android Application  
**Member:** Member 2  
**Primary Domain:** Customer Appointment, Repair Tracking & Repair History

---

## 1. Main Responsibility

Member 2 owns the **customer repair journey**:

```text
Customer Login
    ↓
Select Device
    ↓
Select Repair Service
    ↓
Create Appointment
    ↓
View Appointment
    ↓
Track Repair Status
    ↓
REQUESTED → ASSIGNED → DIAGNOSING → REPAIRING → TESTING → COMPLETED
    ↓
Repair History
    ↓
Repair History Details
```

Member 2 should focus on what a **customer needs to book, view and track a repair**.

---

## 2. Responsibility Boundary

| Feature | Member 2 | Member 3 |
|---|:---:|:---:|
| Customer appointments | ✅ | |
| Create appointment | ✅ | |
| View customer appointments | ✅ | |
| View one appointment | ✅ | |
| Repair tracking | ✅ | |
| Repair status history | ✅ | |
| Repair history | ✅ | |
| Repair history details | ✅ | |
| Device selection for booking | ✅ | |
| Service selection | ✅ | |
| View assigned technician | ✅ | |
| Admin dashboard | | ✅ |
| Branch management | | ✅ |
| Technician management | | ✅ |
| Technician skills | | ✅ |
| Technician assignment | | ✅ |
| Admin metrics | | ✅ |

**Important:** Member 2 displays the technician after assignment. Member 2 does NOT assign the technician.

---

# 3. Screens Member 2 Should Build

## Screen 1 — Customer Home

Provide access to:

- Book a Repair
- My Devices
- My Appointments
- Repair Tracking
- Repair History
- Profile

---

## Screen 2 — Book Repair

The customer should:

1. Select a device.
2. Select a repair service.
3. Select a branch.
4. Select requested date.
5. Select requested time.
6. Enter the problem description.
7. Optionally provide GPS location if required.
8. Submit the appointment.

---

# 4. Device Information

Device data comes from the backend.

Important fields:

```text
device_id
category_id
category_name
brand
model
serial_number
purchase_year
notes
```

Example:

```text
Laptop
Lenovo LOQ 15
Serial: TEST-12345
Purchase Year: 2025
```

If another member already owns device management, Member 2 should consume that data instead of duplicating device CRUD.

---

# 5. Service Selection

The customer selects the repair service.

Known TechFix examples:

```text
SVC-001 — Laptop Screen Replacement
SVC-003 — Keyboard Replacement
SVC-006 — Mobile Battery Replacement
SVC-007 — Charging Port Repair
```

The UI should show at least:

- Service name
- Applicable device category
- Estimated price when available
- Description when available

---

# 6. Create Appointment API

```http
POST /api/appointments
```

Example request:

```json
{
  "device_id": "DEVICE_ID",
  "service_id": "SVC-001",
  "branch_id": "BR-001",
  "requested_date": "2026-08-25",
  "requested_time": "10:30",
  "customer_latitude": 6.9271,
  "customer_longitude": 79.8612,
  "problem_description": "Laptop screen is cracked and showing display lines."
}
```

The customer should be identified through authentication. Do not hard-code a customer ID in Android.

---

# 7. Tested Appointment Response

A successful appointment created during testing returned:

```json
{
  "success": true,
  "message": "Appointment created successfully",
  "data": {
    "id": "55f77027-e70e-4238-82d4-1ef9bb07b306",
    "appointment_number": "TF-20260823085851-75BF50A8",
    "customer_id": "fd1c0efd-e40d-4fca-828c-a0328b900694",
    "device_id": "f4943b4b-815f-4efd-8011-fdcf28a91bc6",
    "service_id": "SVC-001",
    "branch_id": "BR-001",
    "requested_date": "2026-08-25",
    "requested_time": "10:30",
    "customer_latitude": 6.9271,
    "customer_longitude": 79.8612,
    "problem_description": "Laptop screen is cracked and showing display lines.",
    "status": "REQUESTED",
    "estimated_price": 25000,
    "final_price": null,
    "created_at": "2026-08-23 08:58:51",
    "updated_at": "2026-08-23 08:58:51",
    "device_brand": "Lenovo",
    "device_model": "LOQ 15",
    "service_name": "Laptop Screen Replacement",
    "branch_name": "TechFix Colombo",
    "branch_city": "Colombo"
  }
}
```

After success, show the appointment number and `REQUESTED` status.

---

# 8. My Appointments

## API

```http
GET /api/appointments
```

The customer's token should determine which appointments are returned.

Display:

- Appointment number
- Device
- Service
- Requested date/time
- Current status
- Estimated/final price when appropriate
- View Details button

Recommended filters:

```text
Upcoming
Active
Completed
Cancelled (if supported)
```

---

# 9. One Appointment Details

## API

```http
GET /api/appointments/{id}
```

This endpoint was successfully tested.

The detail screen should show:

### Appointment

```text
id
appointment_number
requested_date
requested_time
status
problem_description
estimated_price
final_price
created_at
updated_at
```

### Customer

```text
customer_id
first_name
last_name
```

### Device

```text
device_id
category
brand
model
serial_number
purchase_year
```

### Service

```text
service_id
service_name
```

### Branch

```text
branch_id
branch_name
branch_city
```

### Technician

Only show when assigned:

```text
technician_id
technician_first_name
technician_last_name
technician_employee_code
specialization (if provided)
```

---

# 10. Repair Status Tracking

The tested repair lifecycle is:

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

The Android UI should read the actual status from the API.

Example:

```text
✓ REQUESTED
     ↓
✓ ASSIGNED
     ↓
✓ DIAGNOSING
     ↓
● REPAIRING
     ↓
○ TESTING
     ↓
○ COMPLETED
```

Customers should not arbitrarily change these statuses.

---

# 11. Repair Status History

## API

```http
GET /api/appointments/{id}/history
```

This endpoint was successfully tested.

Example response:

```json
{
  "success": true,
  "data": [
    {
      "id": "f0b12cc7-3120-4c2f-a08c-d93b678bd7be",
      "appointment_id": "55f77027-e70e-4238-82d4-1ef9bb07b306",
      "status": "REQUESTED",
      "note": "Appointment created by customer",
      "changed_by": "fd1c0efd-e40d-4fca-828c-a0328b900694",
      "created_at": "2026-08-23 08:58:51",
      "changed_by_first_name": "John",
      "changed_by_last_name": "Perera",
      "changed_by_role": "CUSTOMER"
    }
  ]
}
```

Render this as a timeline.

Example:

```text
REQUESTED
23 Aug 2026 • 08:58 AM

Appointment created by customer
Changed by: John Perera
Role: CUSTOMER

        ↓

ASSIGNED
23 Aug 2026 • 10:34 AM

Technician assigned
```

---

# 12. Repair History

Completed repairs should appear in the customer's history.

Example card:

```text
┌──────────────────────────────┐
│ Lenovo LOQ 15                │
│ Laptop Screen Replacement    │
│                              │
│ TF-20260823085851-75BF50A8   │
│ Completed: 25 Aug 2026       │
│ Final Price: LKR 25,000      │
│                              │
│ [ View Details ]             │
└──────────────────────────────┘
```

---

# 13. Repair History Detail

For one historical repair, show the complete summary.

## Appointment

```text
Appointment Number
Status
Requested Date
Requested Time
Problem Description
Estimated Price
Final Price
Created At
Updated At
```

## Device

```text
Category
Brand
Model
Serial Number
Purchase Year
```

## Service

```text
Service ID
Service Name
```

## Technician

```text
Technician ID
Name
Employee Code
Specialization
```

## Branch

```text
Branch ID
Branch Name
City
```

## Timeline

```text
REQUESTED
ASSIGNED
DIAGNOSING
REPAIRING
TESTING
COMPLETED
```

## Optional related data

If the backend exposes them:

```text
Repair Images
Parts Used
Payment
Technician Notes
```

---

# 14. Price Handling

Never assume:

```text
estimated_price == final_price
```

Example:

```text
Estimated Price: LKR 25,000
Final Price:     LKR 27,500
```

Before completion:

```text
Estimated Price: LKR 25,000
Final Price: Not finalized
```

After completion:

```text
Estimated Price: LKR 25,000
Final Price: LKR 25,000
```

---

# 15. Technician Integration

Technician assignment is handled by the Admin/Member 3 side.

Member 3 can perform:

```http
PUT /api/appointments/{id}/assign
```

with:

```json
{
  "technician_id": "TECH-001"
}
```

The tested result was:

```text
status: ASSIGNED
technician_id: TECH-001
technician: Nimal Fernando
employee code: TF-T001
```

Member 2 should then display that information.

Flow:

```text
Member 2
Creates appointment
       ↓
REQUESTED
       ↓
Member 3 / Admin
Assigns technician
       ↓
ASSIGNED
       ↓
Member 2
Displays assigned technician
```

---

# 16. Branch Integration

Member 2 should display the branch handling the repair.

Example:

```text
TechFix Colombo
Colombo
```

If available, display:

- Address
- Phone
- Latitude
- Longitude
- Map

Branch CRUD/management is NOT Member 2's responsibility.

---

# 17. GPS / Location

Appointment creation can contain:

```text
customer_latitude
customer_longitude
```

Example:

```text
6.9271
79.8612
```

Member 2 should pass these values when required by the backend.

---

# 18. Authentication

Customer API requests should use:

```http
Authorization: Bearer <CUSTOMER_TOKEN>
```

Never hard-code:

```text
customer_id = ...
```

The backend should identify the customer from the authenticated token.

Correct:

```text
Customer Login
    ↓
JWT
    ↓
Retrofit Authorization Header
    ↓
Cloudflare Worker
    ↓
D1
```

---

# 19. Retrofit Structure

Recommended interface:

```kotlin
interface AppointmentApiService {

    @GET("api/appointments")
    suspend fun getAppointments():
        Response<AppointmentListResponse>

    @GET("api/appointments/{id}")
    suspend fun getAppointment(
        @Path("id") appointmentId: String
    ): Response<AppointmentResponse>

    @GET("api/appointments/{id}/history")
    suspend fun getAppointmentHistory(
        @Path("id") appointmentId: String
    ): Response<AppointmentHistoryResponse>

    @POST("api/appointments")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequest
    ): Response<AppointmentResponse>
}
```

Exact models should match the actual Worker API.

---

# 20. Recommended Android Architecture

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Retrofit API
 ↓
Cloudflare Worker
 ↓
Cloudflare D1
```

With Room:

```text
Cloudflare D1
 ↓
Worker
 ↓
Retrofit
 ↓
Repository
 ↓
Room
 ↓
ViewModel
 ↓
UI
```

The Android app should NOT connect directly to D1.

---

# 21. Recommended Package Structure

```text
com.techfix
│
├── data
│   ├── remote
│   │   └── appointment
│   │       ├── AppointmentApiService.kt
│   │       ├── AppointmentModels.kt
│   │       └── AppointmentRepository.kt
│   │
│   └── local
│       └── appointment
│           ├── AppointmentEntity.kt
│           └── AppointmentDao.kt
│
├── ui
│   └── customer
│       ├── booking
│       │   ├── BookRepairScreen.kt
│       │   └── BookingViewModel.kt
│       │
│       ├── appointments
│       │   ├── AppointmentListScreen.kt
│       │   ├── AppointmentDetailScreen.kt
│       │   └── AppointmentViewModel.kt
│       │
│       └── history
│           ├── RepairHistoryScreen.kt
│           ├── RepairHistoryDetailScreen.kt
│           └── RepairHistoryViewModel.kt
│
└── navigation
```

---

# 22. Room Caching

If the project uses Room, appointments can be cached locally.

Example:

```kotlin
@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey
    val id: String,

    val appointmentNumber: String,
    val customerId: String,
    val deviceId: String,
    val serviceId: String,
    val branchId: String,
    val technicianId: String?,
    val requestedDate: String,
    val requestedTime: String,
    val problemDescription: String,
    val status: String,
    val estimatedPrice: Double?,
    val finalPrice: Double?,
    val createdAt: String,
    val updatedAt: String
)
```

---

# 23. UI States

Every API-driven screen should handle:

### Loading

```text
Loading appointments...
```

### Success

Show the actual data.

### Empty

```text
No repair history yet.

[ Book a Repair ]
```

### Error

```text
Couldn't load your appointments.

[ Retry ]
```

---

# 24. Security Rules

Member 2 must:

- Never hard-code JWT tokens.
- Never hard-code customer IDs.
- Securely store authentication information.
- Send the Authorization header.
- Only display the authenticated customer's data.
- Never expose another customer's appointments.
- Never store API secrets in source code.
- Never commit secrets to GitHub.
- Never expose full card/payment credentials.

---

# 25. Database Areas Used

Member 2 mainly consumes information from:

```text
users
devices
device_categories
services
branches
appointments
repair_status_history
repair_images
payments
service_parts
spare_parts
```

The actual joins should be performed by the backend API.

Android should receive API responses, not query D1 directly.

---

# 26. API Checklist

| Function | Method | Endpoint | Member 2 |
|---|---|---|:---:|
| Customer appointments | GET | `/api/appointments` | ✅ |
| One appointment | GET | `/api/appointments/{id}` | ✅ |
| Create appointment | POST | `/api/appointments` | ✅ |
| Status history | GET | `/api/appointments/{id}/history` | ✅ |
| Technician assignment | PUT | `/api/appointments/{id}/assign` | ❌ |
| Technician management | Various | `/api/technicians...` | ❌ |
| Branch management | Various | `/api/branches...` | ❌ |
| Admin dashboard | GET | `/api/admin/dashboard` | ❌ |

---

# 27. Complete End-to-End Test

Member 2 should test this exact flow:

```text
1. Login as customer
        ↓
2. Open Book Repair
        ↓
3. Select Lenovo LOQ 15
        ↓
4. Select Laptop Screen Replacement
        ↓
5. Select TechFix Colombo
        ↓
6. Select date/time
        ↓
7. Enter problem
        ↓
8. POST /api/appointments
        ↓
9. Verify REQUESTED
        ↓
10. GET /api/appointments
        ↓
11. Open appointment
        ↓
12. GET /api/appointments/{id}
        ↓
13. Admin assigns TECH-001
        ↓
14. Customer sees ASSIGNED
        ↓
15. Repair becomes DIAGNOSING
        ↓
16. REPAIRING
        ↓
17. TESTING
        ↓
18. COMPLETED
        ↓
19. Open Repair History
        ↓
20. Open Repair History Details
        ↓
21. Verify complete timeline/details
```

---

# 28. Definition of Done

## Authentication

- [ ] Customer login works.
- [ ] JWT is attached to API requests.
- [ ] Customer data is isolated.

## Booking

- [ ] Device selection works.
- [ ] Service selection works.
- [ ] Branch selection works.
- [ ] Date/time selection works.
- [ ] Problem description works.
- [ ] GPS is passed when required.
- [ ] Appointment creation works.
- [ ] Appointment starts as `REQUESTED`.

## Appointments

- [ ] Appointment list works.
- [ ] One appointment detail works.
- [ ] Technician appears after assignment.
- [ ] Branch information appears.
- [ ] Device information appears.
- [ ] Service information appears.
- [ ] Prices appear correctly.

## Tracking

- [ ] Current status appears.
- [ ] Status timeline works.
- [ ] History endpoint works.
- [ ] Timeline updates after status changes.

## Repair History

- [ ] Completed repairs appear.
- [ ] Historical repair can be opened.
- [ ] Device details appear.
- [ ] Service details appear.
- [ ] Technician details appear.
- [ ] Branch details appear.
- [ ] Estimated/final price appears.
- [ ] Full status timeline appears.

## Quality

- [ ] Loading states.
- [ ] Empty states.
- [ ] Error states.
- [ ] Retry handling.
- [ ] Responsive UI.
- [ ] No secrets in Git.
- [ ] Clean architecture.

---

# 29. Final Responsibility Summary

## Member 2

> **Owns the customer repair journey: booking a repair, viewing appointments, tracking repair progress, and viewing completed repair history/details.**

## Member 3

> **Owns the administration side: dashboard metrics, branches, technicians, technician skills, and assigning technicians to appointments.**

The main integration point is:

```text
                 APPOINTMENT
                     │
          ┌──────────┴──────────┐
          │                     │
      MEMBER 2              MEMBER 3
          │                     │
   Create appointment       Manage appointment
   View appointment         Assign technician
   Track status             Manage technicians
   View history             Manage branches
   View repair details      Admin dashboard
          │                     │
          └──────────┬──────────┘
                     │
              Cloudflare Worker
                     │
                Cloudflare D1
```

## The one-sentence rule

**Member 2 = customer repair journey.**  
**Member 3 = admin/business management.**
