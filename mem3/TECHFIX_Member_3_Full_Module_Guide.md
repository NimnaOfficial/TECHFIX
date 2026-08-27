# 🛠️ TECHFIX Android Module Guide

## Member 3: Branch & Technician Management (Admin & Overall Management)

**Developer:** Nima  
**Domain:** System Administration, Staff Allocation, and High-Level Metrics  
**Responsibility:** Branch + technician management

---

## 📌 1. Your Exact Role & Scope

As **Member 3**, you are building the **Admin Control Center** of the TechFix Android application.

Your responsibility is to ensure the business side of TechFix operates correctly while other members handle customer booking flows and spare-parts inventory.

You will build interfaces and logic that allow upper management to:

1. View overall business health:
   - Revenue
   - Active Repairs
   - Staff Availability
2. Monitor branch locations and staff.
3. Manage the technician roster.
4. Assign technicians specific repair skills.
5. Dispatch technicians to pending appointments.

---

## 🗄️ 2. Data Models

### A. Branches

Branches are the physical locations of TechFix.

**Fields:**

| Field | Purpose |
|---|---|
| `id` | Unique branch ID |
| `name` | Branch name |
| `address` | Physical address |
| `city` | Branch city |
| `latitude` | Geographic latitude |
| `longitude` | Geographic longitude |
| `phone` | Branch telephone |
| `opening_time` | Opening time |
| `closing_time` | Closing time |
| `is_active` | Whether branch is active |
| `created_at` | Creation timestamp |

**Your job:** Display branch information cleanly. Use latitude and longitude to potentially show a Google Maps view.

### B. Technicians

Technicians are the workforce of TechFix.

**Fields:**

| Field | Purpose |
|---|---|
| `id` | Technician ID |
| `employee_code` | Employee identifier |
| `specialization` | Technician specialization |
| `availability_status` | Current availability |
| `branch_id` | Assigned branch |

Supported statuses:

```text
AVAILABLE
BUSY
OFF_DUTY
ON_LEAVE
```

When a technician is assigned to a repair, their status should shift to `BUSY`.

### C. Dashboard Metrics

**Active Repairs** are appointments in:

```text
DIAGNOSING
REPAIRING
TESTING
```

Formula:

```text
Active Repairs =
COUNT(appointments where status is
DIAGNOSING OR REPAIRING OR TESTING)
```

**Total Revenue** is the sum of payments whose status is strictly `PAID`.

```text
Total Revenue = Σ Amountᵢ
```

where:

```text
payment_status = 'PAID'
```

---

## 🌐 3. Retrofit API Mapping

Use **Retrofit2** to communicate with the Cloudflare Worker.

| Action | HTTP | Endpoint | JSON Body |
|---|---|---|---|
| Get Dashboard | `GET` | `/api/admin/dashboard` | None |
| Get All Branches | `GET` | `/api/api/branches` | None |
| Get Branch Details | `GET` | `/api/branches/{id}` | None |
| Get All Technicians | `GET` | `/api/technicians` | None |
| Get Tech Skills | `GET` | `/api/technicians/{id}/services` | None |
| Update Tech Skills | `PUT` | `/api/technicians/{id}/services` | `{ "service_ids": ["SVC-001", "SVC-002"] }` |
| Assign Technician | `PUT` | `/api/appointments/{id}/assign` | `{ "technician_id": "TECH-001" }` |

> **Important:** `/api/api/branches` is retained exactly from the supplied specification. Verify the deployed Worker route before implementation.

### Authentication

Protected requests require:

```http
Authorization: Bearer <TOKEN>
```

Do not hard-code the JWT in Android source code.

---

## 💾 4. Android Room Database — Offline Caching

Use **Android Room (SQLite)** to cache management data.

Architecture:

```text
Cloudflare Worker API
        ↓
     Retrofit
        ↓
   Repository
        ↓
   Room Database
        ↓
    ViewModel
        ↓
       UI
```

### Step 1 — TechnicianEntity

```kotlin
@Entity(tableName = "local_technicians")
data class TechnicianEntity(

    @PrimaryKey
    val id: String,

    val employeeCode: String,

    val firstName: String,

    val lastName: String,

    val specialization: String,

    val status: String,

    val branchId: String
)
```

### Step 2 — AdminDao

```kotlin
@Dao
interface AdminDao {

    @Query("SELECT * FROM local_technicians WHERE branchId = :branchId")
    fun getTechniciansByBranch(
        branchId: String
    ): Flow<List<TechnicianEntity>>

    @Query("""
        SELECT * FROM local_technicians
        WHERE branchId = :branchId
        AND status = 'AVAILABLE'
    """)
    fun getAvailableTechniciansForBranch(
        branchId: String
    ): List<TechnicianEntity>
}
```

### Step 3 — Database Structure

```text
AppDatabase
    │
    └── AdminDao
          ├── getTechniciansByBranch()
          └── getAvailableTechniciansForBranch()
```

Suggested package:

```text
com.mad.techfix
└── data
    └── local
        └── database
            ├── AppDatabase
            ├── TechnicianEntity
            └── AdminDao
```

---

# 🎨 5. UI/UX Architecture

## Screen 1 — Executive Dashboard

Use either:

- ConstraintLayout
- Jetpack Compose `LazyVerticalGrid`

Create four elevated metric cards:

### Total Revenue
Green highlight.

### Pending Requests
Red/orange highlight.

### Active Repairs
Blue highlight.

### Available Technicians
Green highlight.

### Pull-to-Refresh

```text
Swipe down
    ↓
Call API
    ↓
Update Room cache
    ↓
UI observes data
    ↓
Refresh ends
```

---

## Screen 2 — Technician Roster

Use:

- RecyclerView, or
- Compose `LazyColumn`

Each technician item should show:

- Face/avatar
- Name
- Employee code
- Specialization
- Branch
- Availability status

Example:

```text
┌────────────────────────────────────┐
│ 👤 Nimal Fernando                  │
│    TF-T001                         │
│    Laptop Hardware                 │
│    TechFix Colombo                 │
│    🟢 AVAILABLE                    │
└────────────────────────────────────┘
```

Suggested indicators:

```text
🟢 AVAILABLE
🔴 BUSY
⚪ OFF_DUTY
🟠 ON_LEAVE
```

---

## Screen 3 — Smart Assign Bottom Sheet

### Trigger

Admin clicks **Assign** on a pending appointment.

### Component

For XML/View UI:

```text
BottomSheetDialogFragment
```

For Compose, use the appropriate Material bottom-sheet component.

### Assignment Rule

Only show technicians satisfying both:

```text
availability_status == AVAILABLE
```

and:

```text
technician.branch_id == appointment.branch_id
```

Therefore:

```text
Eligible Technician =
AVAILABLE
+
Same Branch
```

Example:

```text
Appointment:
BR-001 / TechFix Colombo

TECH-001 → BR-001 → AVAILABLE
TECH-003 → BR-002 → AVAILABLE
TECH-004 → BR-002 → AVAILABLE
```

Only `TECH-001` should be shown.

Busy, off-duty, and on-leave technicians must not be selectable.

The backend should also enforce this rule.

---

# 🚀 6. Step-by-Step Execution Plan

## Phase 1 — Verify API Endpoints

Test:

```text
GET /api/admin/dashboard
GET /api/api/branches
GET /api/branches/{id}
GET /api/technicians
GET /api/technicians/{id}/services
PUT /api/technicians/{id}/services
PUT /api/appointments/{id}/assign
```

For protected endpoints:

```http
Authorization: Bearer <ADMIN_OR_MANAGER_TOKEN>
```

Record:

- HTTP status
- JSON response
- Error response
- Required request body

---

## Phase 2 — Setup Retrofit

Create:

```text
AdminApiService.kt
```

Example:

```kotlin
interface AdminApiService {

    @GET("api/admin/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("api/technicians")
    suspend fun getTechnicians(): TechnicianListResponse

    @GET("api/technicians/{id}/services")
    suspend fun getTechnicianServices(
        @Path("id") technicianId: String
    ): TechnicianServicesResponse

    @PUT("api/technicians/{id}/services")
    suspend fun updateTechnicianServices(
        @Path("id") technicianId: String,
        @Body request: UpdateTechnicianServicesRequest
    ): TechnicianServicesResponse

    @PUT("api/appointments/{id}/assign")
    suspend fun assignTechnician(
        @Path("id") appointmentId: String,
        @Body request: AssignTechnicianRequest
    ): AppointmentResponse
}
```

Response/request types must match the actual Worker API.

---

## Phase 3 — Authentication

Authenticate as:

```text
ADMIN
```

or:

```text
MANAGER
```

Use secure token storage.

Recommended flow:

```text
Retrofit Request
      ↓
Auth Interceptor
      ↓
Add Authorization header
      ↓
Cloudflare Worker
```

Handle:

- Expired tokens
- HTTP 401
- HTTP 403
- Logout/session expiry

Never log JWT tokens.

---

## Phase 4 — Room

Create:

```text
BranchEntity
TechnicianEntity
AdminDao
AppDatabase
```

Add the queries needed by the UI.

---

## Phase 5 — Repository

Create:

```text
AdminRepository.kt
```

Responsibilities:

1. Request remote data.
2. Convert API models to local entities.
3. Save/update Room cache.
4. Expose local data.
5. Handle API/network errors.
6. Execute technician skill updates.
7. Execute technician assignments.

---

## Phase 6 — ViewModel

Create:

```text
AdminViewModel.kt
```

Expose:

```text
Dashboard
Branches
Technicians
Technician skills
Assignment status
Loading state
Error state
```

Recommended:

```text
StateFlow
```

---

## Phase 7 — Dashboard UI

Build:

```text
1. Dashboard screen
2. Revenue card
3. Pending Requests card
4. Active Repairs card
5. Available Technicians card
6. Pull-to-refresh
7. Loading state
8. Error state
```

---

## Phase 8 — Branch Management

Build:

```text
Branch List
        ↓
Branch Details
```

Display:

- Name
- Address
- City
- Phone
- Email
- Opening time
- Closing time
- Location
- Technicians working at the branch

Potential enhancement:

```text
Open in Google Maps
```

using latitude/longitude.

---

## Phase 9 — Technician Management

Build:

```text
Technician Roster
```

Add:

- Search
- Branch filtering
- Availability filtering
- Specialization display
- Technician details

Suggested filters:

```text
All
Available
Busy
Off Duty
On Leave
```

---

## Phase 10 — Technician Skills

For each technician:

```text
GET /api/technicians/{id}/services
```

Display supported services.

Allow management to modify them.

Request:

```json
{
  "service_ids": [
    "SVC-001",
    "SVC-002"
  ]
}
```

Endpoint:

```text
PUT /api/technicians/{id}/services
```

---

## Phase 11 — Smart Technician Assignment

Flow:

```text
Pending Appointment
       ↓
Admin clicks Assign
       ↓
Read appointment branch
       ↓
Load technicians
       ↓
Filter:
AVAILABLE
+
Same Branch
       ↓
Show Bottom Sheet
       ↓
Admin selects technician
       ↓
PUT /api/appointments/{id}/assign
       ↓
Backend validates
       ↓
Appointment → ASSIGNED
       ↓
Technician → BUSY
       ↓
Refresh Room cache
       ↓
Update UI
```

---

# 🧪 7. End-to-End Testing Checklist

## Dashboard

- [ ] Dashboard API responds successfully.
- [ ] Revenue displays correctly.
- [ ] Pending request count is correct.
- [ ] Active repair count is correct.
- [ ] Available technician count is correct.
- [ ] Pull-to-refresh works.
- [ ] Loading state works.
- [ ] Error state works.
- [ ] Cached data is available when appropriate.

## Branches

- [ ] Branch list loads.
- [ ] Branch details load.
- [ ] Address displays.
- [ ] Contact information displays.
- [ ] Opening/closing times display.
- [ ] Latitude/longitude are available.
- [ ] Branch technician list displays correctly.

## Technicians

- [ ] Technician list loads.
- [ ] Names display.
- [ ] Employee codes display.
- [ ] Specializations display.
- [ ] Branch information displays.
- [ ] Availability status displays.
- [ ] Search works.
- [ ] Status filtering works.
- [ ] Branch filtering works.

## Technician Skills

- [ ] Technician services load.
- [ ] Current services display.
- [ ] Service selection works.
- [ ] Correct service IDs are sent.
- [ ] API confirms update.
- [ ] UI refreshes after update.

## Technician Assignment

- [ ] Assignment button opens.
- [ ] Correct branch is identified.
- [ ] Only available technicians are shown.
- [ ] Only same-branch technicians are shown.
- [ ] Busy technicians cannot be assigned.
- [ ] Off-duty technicians cannot be assigned.
- [ ] On-leave technicians cannot be assigned.
- [ ] Assignment API succeeds.
- [ ] Appointment becomes `ASSIGNED`.
- [ ] Technician becomes `BUSY`.
- [ ] UI refreshes after assignment.

---

# 🏗️ 8. Suggested Member 3 Package Structure

```text
com.mad.techfix
│
├── data
│   ├── local
│   │   └── database
│   │       ├── AppDatabase.kt
│   │       ├── TechnicianEntity.kt
│   │       ├── BranchEntity.kt
│   │       └── AdminDao.kt
│   │
│   └── remote
│       ├── AdminApiService.kt
│       └── ...
│
├── models
│   └── admin
│       ├── DashboardResponse.kt
│       ├── BranchResponse.kt
│       ├── TechnicianResponse.kt
│       └── ...
│
├── repository
│   └── AdminRepository.kt
│
├── ui
│   └── admin
│       ├── dashboard
│       ├── branches
│       ├── technicians
│       └── assignment
│
└── viewmodel
    └── AdminViewModel.kt
```

---

# 🔐 9. Security Requirements

Never hard-code:

```kotlin
val token = "eyJhbGciOi..."
```

Use secure token storage.

The application should:

- Validate authentication state.
- Handle expired tokens.
- Handle 401/403 responses.
- Never log JWT tokens.
- Never expose passwords in logs.
- Never commit secrets to Git.

---

# 🏗️ 10. Overall Member 3 Architecture

```text
                    ┌──────────────────────┐
                    │   Admin / Manager    │
                    │      Android UI      │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      ViewModel       │
                    │   AdminViewModel     │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Repository       │
                    │   AdminRepository    │
                    └───────┬───────┬──────┘
                            │       │
                ┌───────────┘       └────────────┐
                ▼                                ▼
      ┌──────────────────┐             ┌──────────────────┐
      │    Retrofit      │             │   Room Database  │
      │   Remote API     │             │   Local Cache    │
      └────────┬─────────┘             └────────▲─────────┘
               │                                │
               ▼                                │
      ┌──────────────────┐                      │
      │ Cloudflare Worker│──────────────────────┘
      │      API         │
      └────────┬─────────┘
               │
               ▼
      ┌──────────────────┐
      │   Cloudflare D1  │
      │     Database     │
      └──────────────────┘
```

---

# 🎯 11. Main Member 3 Deliverables

### 1. Executive Dashboard

```text
Revenue
Pending Requests
Active Repairs
Available Technicians
```

### 2. Branch Management

```text
Branch List
Branch Details
Branch Staff
Location Information
```

### 3. Technician Management

```text
Technician Roster
Availability
Specialization
Branch
Employee Code
```

### 4. Technician Skills

```text
View Services
Update Services
```

### 5. Smart Assignment

```text
Pending Appointment
        ↓
Eligible Technicians
        ↓
Select Technician
        ↓
Assign
        ↓
Appointment = ASSIGNED
Technician = BUSY
```

### 6. Offline Cache

```text
Room
 ├── BranchEntity
 ├── TechnicianEntity
 └── AdminDao
```

### 7. Network Layer

```text
AdminApiService
AdminRepository
Authentication
```

### 8. State Management

```text
AdminViewModel
StateFlow / LiveData
```

---

# 📋 12. Recommended Development Order

```text
STEP 1
Verify Admin/Manager authentication
        ↓
STEP 2
Test every Member 3 API
        ↓
STEP 3
Create AdminApiService
        ↓
STEP 4
Create API request/response models
        ↓
STEP 5
Create BranchEntity
        ↓
STEP 6
Create TechnicianEntity
        ↓
STEP 7
Create AdminDao
        ↓
STEP 8
Update AppDatabase
        ↓
STEP 9
Create AdminRepository
        ↓
STEP 10
Create AdminViewModel
        ↓
STEP 11
Build Dashboard
        ↓
STEP 12
Build Branch List
        ↓
STEP 13
Build Branch Details
        ↓
STEP 14
Build Technician Roster
        ↓
STEP 15
Build Technician Details
        ↓
STEP 16
Build Technician Skills
        ↓
STEP 17
Build Smart Assign Bottom Sheet
        ↓
STEP 18
Connect Technician Assignment
        ↓
STEP 19
Add Refresh / Loading / Error states
        ↓
STEP 20
Complete end-to-end testing
```

---

# ✅ 13. Final Member 3 Goal

The finished Member 3 module should allow an authorized **Admin or Manager** to control the operational side of TechFix from one Android application.

Complete management flow:

```text
                ADMIN / MANAGER
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
      Dashboard    Branches   Technicians
          │           │           │
          │           │           ├── Availability
          │           │           ├── Specialization
          │           │           ├── Branch
          │           │           └── Services
          │           │
          │           └── Staff
          │
          ├── Revenue
          ├── Pending Requests
          ├── Active Repairs
          └── Available Technicians
                      │
                      ▼
              Pending Appointment
                      │
                      ▼
               Smart Assignment
                      │
                      ▼
              Eligible Technician
                      │
                      ▼
                  ASSIGNED
                      │
                      ▼
             Technician = BUSY
```

## 📝 Source/Scope Note

This guide is based on the Member 3 specification supplied in the conversation. Endpoint names and architecture choices are retained from that specification. The `/api/api/branches` route is intentionally not silently corrected and should be verified against the deployed Worker.
