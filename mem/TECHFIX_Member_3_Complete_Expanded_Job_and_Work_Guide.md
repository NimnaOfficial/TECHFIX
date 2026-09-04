# TECHFIX — MEMBER 3 COMPLETE JOB & WORK GUIDE

## Branch & Technician Management — Admin / Overall Management

**Developer:** Nima  
**Role:** Member 3  
**Main Domain:** System Administration, Branch Operations, Technician Management, Staff Allocation, High-Level Business Metrics  
**Application:** TECHFIX Android Mobile Application

---

# 1. PURPOSE OF THIS DOCUMENT

This document is the **complete working guide for Member 3**.

It explains:

- Exactly what Member 3 is responsible for
- Which screens Member 3 must build
- What each screen must do
- Which APIs are required
- What data must be displayed
- How the Android architecture should be organized
- How technician assignment should work
- How branch and technician information should connect
- How Room/offline caching can be used
- How authentication should work
- What Member 3 must test
- What Member 3 should demonstrate
- What is inside and outside Member 3's responsibility

The coursework requires each team member to handle at least one UI, and the application is expected to use technologies such as web services/remote data, complex data models, GPS/maps, camera/image integration, or SQLite/offline functionality. The official coursework description also states that management should be able to manage repair appointments, technicians, spare parts, payments and branch operations. Member 3's assigned module should therefore focus specifically on the **management, branch and technician side**, while avoiding duplication of the other members' modules.

---

# 2. OFFICIAL PROJECT CONTEXT

TECHFIX is an Android application for a computer and mobile-phone repair business operating branches in Colombo and Galle.

The system is intended to allow customers to:

- Create accounts
- Log in
- Search repair services
- Submit repair appointments
- Track repair progress
- View repair history

The management side must support operational activities such as:

- Repair appointment management
- Technician management
- Branch operations
- Spare-part availability
- Payments
- Other management functions

The coursework also requires each team member to handle at least one UI and encourages technologies including:

- Locations / Map GPS
- Web Services & Remote Data
- Complex Data Models & Adaptors
- Camera & Image Integration
- SQLite / Offline Application

The final demonstration should show the major application functions within the required demonstration time.

---

# 3. MEMBER 3 — EXACT ROLE

## Your Role

You are **Member 3: Branch & Technician Management (Admin & Overall Management).**

Your job is to build the **Admin / Management Control Center**.

Your module should allow an authorized Admin or Manager to understand and control the operational side of TECHFIX.

Your main responsibilities are:

1. View overall business health.
2. View dashboard metrics.
3. Monitor branches.
4. View branch details and staff.
5. Manage/view the technician roster.
6. View technician availability.
7. View technician specialization.
8. View and manage technician services/skills.
9. View pending repair appointments requiring management action.
10. Assign suitable technicians to appointments.
11. Integrate management screens with the backend API.
12. Use appropriate Android architecture and state handling.
13. Handle loading, error and empty states.
14. Test the complete management workflow.

---

# 4. YOUR RESPONSIBILITY IN ONE SENTENCE

> **Member 3 builds the management/control center that allows Admin/Manager users to monitor the business, branches and technicians and dispatch suitable technicians to repair appointments.**

---

# 5. WHAT MEMBER 3 DOES NOT OWN

To avoid duplicated work, Member 3 should NOT make these the primary responsibility of the module:

### Customer booking

Member 2 owns:

```text
Customer
   ↓
Select Device
   ↓
Select Service
   ↓
Book Repair
```

### Customer repair history

Member 2 owns:

```text
Customer
   ↓
My Appointments
   ↓
Repair Tracking
   ↓
Repair History
```

### Core backend/database implementation

Member 1 owns:

```text
Cloudflare Worker
Cloudflare D1
API
Authentication
Database
Backend business logic
```

Member 3 **consumes these APIs**.

You should not duplicate backend business logic inside Android.

---

# 6. MEMBER 3 COMPLETE FEATURE SET

Your module should contain these major areas:

```text
ADMIN / MANAGER
      │
      ▼
ADMIN LOGIN
      │
      ▼
ADMIN DASHBOARD
      │
      ├── Business Metrics
      │
      ├── Branch Management
      │
      ├── Technician Management
      │
      └── Appointment Assignment
```

Detailed structure:

```text
Admin / Manager
│
├── Dashboard
│   ├── Total Revenue
│   ├── Pending Requests
│   ├── Active Repairs
│   └── Available Technicians
│
├── Branches
│   ├── Branch List
│   ├── Branch Details
│   ├── Location
│   └── Branch Technicians
│
├── Technicians
│   ├── Technician Roster
│   ├── Search
│   ├── Filter
│   ├── Technician Details
│   ├── Availability
│   └── Services / Skills
│
└── Appointments
    ├── Pending Requests
    ├── Appointment Details
    └── Assign Technician
```

---

# 7. SCREEN 1 — ADMIN / MANAGER LOGIN

## Purpose

Allow an authorized management user to enter the management area.

Roles may include:

```text
ADMIN
MANAGER
```

These are separate roles.

Do not assume that Admin and Manager are the same role unless the backend explicitly defines them that way.

## Login Flow

```text
Login Screen
     ↓
Enter username/email + password
     ↓
Backend authentication
     ↓
JWT/token received
     ↓
Validate role
     ↓
Admin/Manager Dashboard
```

## Important

The Android application must not:

- Hard-code a real JWT
- Log JWTs
- Store passwords in plain text
- Put credentials into GitHub

Protected API requests should use:

```http
Authorization: Bearer <TOKEN>
```

---

# 8. SCREEN 2 — EXECUTIVE ADMIN DASHBOARD

This is your **main UI**.

The dashboard should provide a quick overview of TECHFIX operations.

## Main Cards

Build at least four important metric cards:

```text
┌──────────────────┐
│ Total Revenue    │
│ LKR 250,000      │
└──────────────────┘

┌──────────────────┐
│ Pending Requests │
│ 8                │
└──────────────────┘

┌──────────────────┐
│ Active Repairs   │
│ 12               │
└──────────────────┘

┌──────────────────┐
│ Available Techs  │
│ 5                │
└──────────────────┘
```

## Dashboard Responsibilities

The dashboard must:

- Load data from the backend.
- Display correct values.
- Show loading state.
- Show error state.
- Support refresh.
- Avoid hard-coded numbers.
- Update after management actions.

---

# 9. DASHBOARD METRIC 1 — TOTAL REVENUE

## Meaning

Total Revenue represents payments that have successfully reached:

```text
PAID
```

The definition used by this module is:

```text
Total Revenue =
SUM(payment amount)
WHERE payment_status = 'PAID'
```

Do not count:

```text
PENDING
FAILED
CANCELLED
```

as completed revenue.

## Example

If payments are:

```text
LKR 10,000 → PAID
LKR 15,000 → PAID
LKR 8,000  → FAILED
```

Then:

```text
Total Revenue = LKR 25,000
```

The dashboard should receive this from the backend rather than calculating it from incomplete client-side data.

---

# 10. DASHBOARD METRIC 2 — ACTIVE REPAIRS

Active repairs are appointments currently in:

```text
DIAGNOSING
REPAIRING
TESTING
```

Formula:

```text
Active Repairs =
COUNT(
    DIAGNOSING
    + REPAIRING
    + TESTING
)
```

Example:

```text
DIAGNOSING = 3
REPAIRING  = 6
TESTING    = 2

Active Repairs = 11
```

---

# 11. DASHBOARD METRIC 3 — PENDING REQUESTS

Pending requests represent appointments waiting for management action.

The primary status used for the management queue is:

```text
REQUESTED
```

The exact business definition should follow the backend.

Example:

```text
REQUESTED = 7

Pending Requests = 7
```

This card should ideally navigate to the pending appointment list.

---

# 12. DASHBOARD METRIC 4 — AVAILABLE TECHNICIANS

Count technicians whose availability is:

```text
AVAILABLE
```

Example:

```text
AVAILABLE = 5
BUSY = 3
OFF_DUTY = 1
ON_LEAVE = 1
```

Then:

```text
Available Technicians = 5
```

---

# 13. DASHBOARD REFRESH

The dashboard must not rely on data loaded only once.

Recommended:

```text
Pull to Refresh
       ↓
Call Dashboard API
       ↓
Receive latest values
       ↓
Update local cache
       ↓
Update ViewModel state
       ↓
Update UI
```

Also refresh after important operations such as technician assignment.

---

# 14. SCREEN 3 — BRANCH LIST

## Purpose

Allow Admin/Manager to view the TechFix branches.

The coursework context identifies two branches:

```text
Colombo
Galle
```

The actual database should remain the source of truth.

## Branch List Example

```text
TECHFIX BRANCHES

┌──────────────────────────────┐
│ TechFix Colombo              │
│ Colombo                      │
│ Open                         │
│ 08:30 - 18:00                │
└──────────────────────────────┘

┌──────────────────────────────┐
│ TechFix Galle                │
│ Galle                        │
│ Open                         │
│ 08:30 - 18:00                │
└──────────────────────────────┘
```

---

# 15. BRANCH DATA

A branch may contain:

```text
id
name
address
city
latitude
longitude
phone
opening_time
closing_time
is_active
created_at
```

## UI should display

At minimum:

- Branch name
- Address
- City
- Phone
- Opening time
- Closing time
- Active/inactive state if available
- Location
- Technicians working at the branch

---

# 16. SCREEN 4 — BRANCH DETAILS

When the Admin taps a branch:

```text
Branch List
     ↓
Branch Details
```

## Branch Details Layout

```text
TECHFIX COLOMBO

Address
123 Example Road

City
Colombo

Phone
011 XXXXXXX

Opening
08:30

Closing
18:00

Location
[ MAP ]

Technicians
------------------
Nimal Fernando
AVAILABLE

Sahan Silva
BUSY
```

## Map Enhancement

Because the branch contains:

```text
latitude
longitude
```

you can provide:

```text
Open in Google Maps
```

or an embedded map.

This is a strong way to demonstrate the coursework's GPS/Map requirement when appropriate.

---

# 17. SCREEN 5 — TECHNICIAN ROSTER

## Purpose

The Technician Roster is the main workforce-management screen.

It should show all technicians returned by the backend.

## Each Technician Card

Display:

- Name
- Face/avatar if available
- Employee code
- Specialization
- Branch
- Availability
- Optional service count

Example:

```text
┌───────────────────────────────────┐
│ 👤 Nimal Fernando                 │
│                                   │
│ Employee: TF-T001                 │
│ Specialization: Laptop Hardware   │
│ Branch: TechFix Colombo           │
│ Status: AVAILABLE                 │
└───────────────────────────────────┘
```

---

# 18. TECHNICIAN DATA

Technician information includes:

```text
id
employee_code
specialization
availability_status
branch_id
```

Additional user information may come from the related user record:

```text
first_name
last_name
```

## Supported availability

```text
AVAILABLE
BUSY
OFF_DUTY
ON_LEAVE
```

---

# 19. TECHNICIAN AVAILABILITY

The availability status must come from the backend.

Do not hard-code:

```text
AVAILABLE
```

for every technician.

Example:

```text
🟢 AVAILABLE
🔴 BUSY
⚪ OFF_DUTY
🟠 ON_LEAVE
```

The exact colors are a UI decision; the actual status value must come from the API.

---

# 20. TECHNICIAN SEARCH

Add a search field if practical.

Example:

```text
Search technicians...
```

Allow searching by:

- Name
- Employee code
- Specialization

Example:

```text
Search: Nimal

Result:
Nimal Fernando
TF-T001
Laptop Hardware
```

---

# 21. TECHNICIAN FILTERS

Recommended filters:

```text
ALL
AVAILABLE
BUSY
OFF DUTY
ON LEAVE
```

Branch filtering is also recommended:

```text
ALL BRANCHES
COLOMBO
GALLE
```

This makes the management screen much more useful.

---

# 22. SCREEN 6 — TECHNICIAN DETAILS

When a technician is selected:

```text
Technician Roster
        ↓
Technician Details
```

Display:

```text
Nimal Fernando

Employee Code
TF-T001

Specialization
Laptop Hardware

Branch
TechFix Colombo

Availability
AVAILABLE

Services / Skills
-------------------------
Laptop Screen Replacement
Keyboard Replacement
...
```

This screen can also contain an action:

```text
Manage Services
```

---

# 23. SCREEN 7 — TECHNICIAN SERVICES / SKILLS

## Purpose

Management should be able to see which repair services a technician can perform.

Example:

```text
Nimal Fernando

Supported Services

[x] Laptop Screen Replacement
[x] Keyboard Replacement
[x] Laptop Diagnostics
[ ] Mobile Battery Replacement
[ ] Charging Port Repair

        [ SAVE ]
```

The exact services should come from the backend.

---

# 24. GET TECHNICIAN SERVICES

API:

```text
GET /api/technicians/{id}/services
```

The response should be converted into an Android model and displayed.

---

# 25. UPDATE TECHNICIAN SERVICES

API:

```text
PUT /api/technicians/{id}/services
```

Example body:

```json
{
  "service_ids": [
    "SVC-001",
    "SVC-002"
  ]
}
```

## Save Flow

```text
Open Technician
       ↓
Load Services
       ↓
User selects services
       ↓
Press Save
       ↓
Validate selection
       ↓
PUT API
       ↓
Backend confirms
       ↓
Refresh technician services
       ↓
Show success
```

---

# 26. SCREEN 8 — MANAGEMENT APPOINTMENT QUEUE

Member 3 needs a management view for appointments that require action.

The purpose is NOT to recreate the customer's appointment screen.

The purpose is:

> Give management enough information to make an assignment/dispatch decision.

## Recommended information

```text
Appointment Number
Customer
Device
Service
Branch
Requested Date
Requested Time
Status
Assigned Technician
Estimated Price
```

Example:

```text
TF-20260823085851-75BF50A8

Customer:
John Perera

Device:
Lenovo LOQ 15

Service:
Laptop Screen Replacement

Branch:
TechFix Colombo

Requested:
25 Aug 2026 — 10:30

Status:
REQUESTED

[ ASSIGN TECHNICIAN ]
```

---

# 27. SCREEN 9 — SMART TECHNICIAN ASSIGNMENT

This is one of the most important features in your module.

## Purpose

Management needs to assign a suitable technician to a repair request.

The assignment process should be:

```text
Pending Appointment
        ↓
Admin clicks ASSIGN
        ↓
Read appointment branch
        ↓
Read required service
        ↓
Load technicians
        ↓
Filter eligible technicians
        ↓
Display candidates
        ↓
Admin selects technician
        ↓
Confirm assignment
        ↓
PUT /assign
        ↓
Backend validates
        ↓
Appointment becomes ASSIGNED
        ↓
Refresh UI
```

---

# 28. TECHNICIAN ELIGIBILITY

At minimum, filter based on:

```text
availability_status == AVAILABLE
```

and:

```text
technician.branch_id == appointment.branch_id
```

Recommended additional rule:

```text
Technician supports the appointment service
```

Therefore:

```text
Eligible Technician =
AVAILABLE
+
SAME BRANCH
+
SUPPORTS SERVICE
```

The backend remains the final authority.

---

# 29. ASSIGNMENT EXAMPLE

Appointment:

```text
Service:
Laptop Screen Replacement

Branch:
BR-001 / TechFix Colombo
```

Technicians:

```text
TECH-001
Branch: BR-001
Status: AVAILABLE
Specialization: Laptop Hardware

TECH-002
Branch: BR-001
Status: BUSY

TECH-003
Branch: BR-002
Status: AVAILABLE
```

Eligible candidates:

```text
TECH-001
```

Do not allow:

```text
TECH-002 → BUSY
TECH-003 → Wrong branch
```

---

# 30. ASSIGNMENT BOTTOM SHEET

Recommended UI:

```text
ASSIGN TECHNICIAN

Appointment:
Laptop Screen Replacement
TechFix Colombo

Suitable Technicians:

○ Nimal Fernando
  TF-T001
  Laptop Hardware
  AVAILABLE

○ Another Technician
  TF-T005
  Laptop Hardware
  AVAILABLE

[ CANCEL ]     [ ASSIGN ]
```

For XML-based UI, a `BottomSheetDialogFragment` is appropriate.

For Jetpack Compose, use the appropriate Material bottom-sheet component.

---

# 31. ASSIGNMENT API

Endpoint:

```text
PUT /api/appointments/{id}/assign
```

Example:

```json
{
  "technician_id": "TECH-001"
}
```

After success, the UI should reflect:

```text
Appointment:
REQUESTED
      ↓
ASSIGNED
```

and, according to the current module behavior:

```text
Technician:
AVAILABLE
      ↓
BUSY
```

The backend should enforce the actual state transition.

---

# 32. COMPLETE ASSIGNMENT FLOW

```text
                  REQUESTED APPOINTMENT
                          │
                          ▼
                    [ ASSIGN ]
                          │
                          ▼
               Read appointment details
                          │
                          ▼
                   Required branch
                          │
                          ▼
                   Required service
                          │
                          ▼
                 Load technicians
                          │
                          ▼
             ┌─────────────────────────┐
             │ Filter eligible staff   │
             │                         │
             │ AVAILABLE               │
             │ SAME BRANCH             │
             │ SERVICE COMPATIBLE      │
             └────────────┬────────────┘
                          │
                          ▼
                 Show candidate list
                          │
                          ▼
                Admin selects one
                          │
                          ▼
                       CONFIRM
                          │
                          ▼
              PUT /appointments/{id}/assign
                          │
                          ▼
                   Backend validation
                          │
                          ▼
                Appointment = ASSIGNED
                          │
                          ▼
                Technician = BUSY
                          │
                          ▼
                    Refresh UI
```

---

# 33. RETROFIT API CONTRACT

Use Retrofit2 for communication with the Worker API.

Required management APIs:

| Function | Method | Endpoint |
|---|---|---|
| Dashboard | GET | `/api/admin/dashboard` |
| Branches | GET | `/api/branches`* |
| Branch Details | GET | `/api/branches/{id}` |
| Technicians | GET | `/api/technicians` |
| Technician Services | GET | `/api/technicians/{id}/services` |
| Update Technician Services | PUT | `/api/technicians/{id}/services` |
| Assign Technician | PUT | `/api/appointments/{id}/assign` |

\* The earlier supplied specification contained `/api/api/branches`. This should be verified against the actual deployed Worker before implementation. Do not blindly duplicate a route if the backend uses `/api/branches`.

---

# 34. RETROFIT INTERFACE

Example structure:

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

The response/request classes must match the actual Worker JSON.

---

# 35. AUTHENTICATION ARCHITECTURE

Protected management requests should work like this:

```text
Admin Login
    ↓
JWT
    ↓
Secure Token Storage
    ↓
Retrofit
    ↓
Auth Interceptor
    ↓
Authorization: Bearer <TOKEN>
    ↓
Cloudflare Worker
```

## Handle

```text
401 Unauthorized
403 Forbidden
Expired Token
Network Error
Server Error
```

Never log:

```text
JWT
Password
Refresh Token
```

---

# 36. ROOM / SQLITE OFFLINE CACHE

Room can be used to cache management data.

Recommended architecture:

```text
Cloudflare Worker
       ↓
    Retrofit
       ↓
  Repository
       ↓
   Room Cache
       ↓
   ViewModel
       ↓
      UI
```

Benefits:

- Faster screen loading
- Cached technician data
- Cached branch data
- Better offline/read-only behavior
- Demonstrates SQLite/Room usage

The backend remains the source of truth.

---

# 37. BRANCH ENTITY

A possible Room entity:

```kotlin
@Entity(tableName = "local_branches")
data class BranchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val city: String,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val openingTime: String?,
    val closingTime: String?,
    val isActive: Boolean
)
```

Use the actual API/database fields when implementing.

---

# 38. TECHNICIAN ENTITY

Example:

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

---

# 39. ADMIN DAO

Example:

```kotlin
@Dao
interface AdminDao {

    @Query(
        "SELECT * FROM local_technicians " +
        "WHERE branchId = :branchId"
    )
    fun getTechniciansByBranch(
        branchId: String
    ): Flow<List<TechnicianEntity>>

    @Query(
        """
        SELECT * FROM local_technicians
        WHERE branchId = :branchId
        AND status = 'AVAILABLE'
        """
    )
    fun getAvailableTechniciansForBranch(
        branchId: String
    ): List<TechnicianEntity>
}
```

You can add queries for:

- Search
- Status
- Branch
- Availability
- Technician lookup

---

# 40. REPOSITORY

Create:

```text
AdminRepository.kt
```

The repository should be responsible for:

1. Calling remote APIs.
2. Converting DTOs to local models.
3. Saving data to Room.
4. Reading cached data.
5. Handling network errors.
6. Updating technician services.
7. Assigning technicians.
8. Refreshing data after mutations.

Architecture:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ├── Retrofit
 └── Room
```

Do not place API calls directly inside UI components.

---

# 41. VIEWMODEL

Create:

```text
AdminViewModel.kt
```

It should expose state for:

```text
Dashboard
Branches
Branch Details
Technicians
Technician Details
Technician Services
Assignment
Loading
Error
Empty
```

Recommended:

```text
StateFlow
```

or the project's existing state-management approach.

---

# 42. VIEWMODEL STATE EXAMPLE

Conceptually:

```text
AdminUiState

isLoading
dashboard
branches
technicians
selectedTechnician
technicianServices
assignmentState
errorMessage
```

The UI observes the state.

Example:

```text
ViewModel
     ↓
StateFlow
     ↓
UI
```

---

# 43. PACKAGE STRUCTURE

A clean structure:

```text
com.mad.techfix
│
├── data
│   ├── local
│   │   └── database
│   │       ├── AppDatabase.kt
│   │       ├── BranchEntity.kt
│   │       ├── TechnicianEntity.kt
│   │       └── AdminDao.kt
│   │
│   └── remote
│       ├── AdminApiService.kt
│       ├── DashboardResponse.kt
│       ├── TechnicianResponse.kt
│       └── ...
│
├── models
│   └── admin
│       ├── Dashboard.kt
│       ├── Branch.kt
│       ├── Technician.kt
│       └── ...
│
├── repository
│   └── AdminRepository.kt
│
├── viewmodel
│   └── AdminViewModel.kt
│
└── ui
    └── admin
        ├── dashboard
        ├── branches
        ├── technicians
        └── assignment
```

Keep `data`, `local`, and `database` as separate package levels if that matches your project organization.

---

# 44. COMPLETE UI NAVIGATION

Recommended navigation:

```text
Admin/Manager Login
        ↓
Admin Dashboard
        │
        ├───────────────┐
        ↓               ↓
    Branches        Technicians
        ↓               ↓
 Branch Details    Technician Details
        │               ↓
        │          Manage Services
        │
        └───────────────┐
                        ↓
                Pending Appointments
                        ↓
                 Appointment Details
                        ↓
                 Assign Technician
                        ↓
                 Assignment Success
```

---

# 45. DASHBOARD → PENDING REQUESTS

When the Admin taps:

```text
Pending Requests
```

navigate to:

```text
Pending Appointment List
```

This makes the dashboard actionable rather than just informational.

---

# 46. DASHBOARD → ACTIVE REPAIRS

When the Admin taps:

```text
Active Repairs
```

you can show:

```text
DIAGNOSING
REPAIRING
TESTING
```

appointments.

This gives management visibility into ongoing repairs.

---

# 47. DASHBOARD → AVAILABLE TECHNICIANS

When the Admin taps:

```text
Available Technicians
```

navigate to:

```text
Technician Roster
```

with:

```text
Filter = AVAILABLE
```

---

# 48. DASHBOARD → REVENUE

Revenue can be displayed as a metric.

If the backend provides detailed payment data, management may also have a payment/revenue view.

Do not create a fake payment system in Member 3 if payment functionality belongs elsewhere in the project.

---

# 49. BRANCH → TECHNICIANS

Branch details should connect naturally to technician data:

```text
Branch
  ↓
Branch ID
  ↓
Technicians
  ↓
Filter branch_id
```

Example:

```text
BR-001
TechFix Colombo

Technicians:
TECH-001
TECH-002
```

---

# 50. TECHNICIAN → SERVICES

Technician details should connect to service data:

```text
Technician
      ↓
Technician ID
      ↓
GET /services
      ↓
Supported Services
```

This is important for smart assignment.

---

# 51. APPOINTMENT → TECHNICIAN

The appointment contains the information required to make a dispatch decision.

Conceptually:

```text
Appointment
 ├── Branch
 ├── Service
 ├── Device
 └── Status
```

Technician:

```text
Technician
 ├── Branch
 ├── Availability
 └── Services
```

Match:

```text
Appointment.branch
       =
Technician.branch

Appointment.service
       ∈
Technician.services

Technician.status
       =
AVAILABLE
```

---

# 52. IMPORTANT: CLIENT VS SERVER VALIDATION

The Android app should filter unsuitable technicians for a good user experience.

However, the backend must remain the final authority.

Android:

```text
Filter candidates
```

Backend:

```text
Validate assignment
```

This prevents invalid assignments if another user changes the technician's status at the same time.

---

# 53. RACE CONDITION EXAMPLE

Suppose:

```text
Admin A sees Nimal = AVAILABLE
```

Then:

```text
Admin B assigns Nimal
```

Nimal becomes:

```text
BUSY
```

Admin A still has an old screen showing:

```text
AVAILABLE
```

Admin A tries to assign Nimal.

The backend must reject the invalid assignment if the technician is no longer available.

Therefore:

```text
Android filtering ≠ security
Backend validation = final authority
```

---

# 54. LOADING STATES

Every network screen should support:

```text
Loading
Success
Empty
Error
```

Example:

```text
Loading:
[progress indicator]

Success:
Technician list

Empty:
"No technicians found"

Error:
"Unable to load technicians"
[Retry]
```

Do not leave a blank screen when an API request fails.

---

# 55. ERROR HANDLING

Handle at least:

```text
401 Unauthorized
403 Forbidden
404 Not Found
400 Bad Request
500 Server Error
Network Timeout
No Internet
```

Display user-friendly messages.

Avoid showing raw backend stack traces to users.

---

# 56. EMPTY STATES

Examples:

### No technicians

```text
No technicians found for this branch.
```

### No pending appointments

```text
No pending repair requests.
```

### No services

```text
No services assigned to this technician.
```

---

# 57. SUCCESS MESSAGES

After successful operations:

```text
Technician assigned successfully.
```

or:

```text
Technician services updated successfully.
```

Then refresh the affected screen.

---

# 58. DATA REFRESH RULES

Refresh after:

- Login
- Dashboard opening
- Pull-to-refresh
- Technician service update
- Technician assignment
- Returning to important management screens

Example:

```text
Assign Technician
       ↓
API Success
       ↓
Refresh Appointment
       ↓
Refresh Technician
       ↓
Refresh Dashboard
```

---

# 59. GPS / MAP FEATURE

The coursework specifically lists:

```text
Locations / Map GPS
```

Member 3 can use this meaningfully for branch management.

## Recommended feature

Branch Details:

```text
TechFix Colombo
Latitude: ...
Longitude: ...

[ VIEW ON MAP ]
```

Possible flow:

```text
Branch Details
      ↓
Latitude + Longitude
      ↓
Map
      ↓
Branch Marker
```

This is more meaningful than adding GPS without a business purpose.

---

# 60. MAP FEATURE — OPTIONAL ENHANCEMENT

A branch screen can include:

```text
        MAP
 ┌─────────────────────┐
 │                     │
 │        📍           │
 │      Colombo        │
 │                     │
 └─────────────────────┘

[ OPEN GOOGLE MAPS ]
```

The exact map technology should follow the project's chosen Android implementation.

---

# 61. OFFLINE MODE

If Room is implemented, the management app can still display previously cached:

```text
Branches
Technicians
```

when the network is unavailable.

Example:

```text
Internet unavailable.

Showing last synchronized data:
Last updated: 21:45
```

Important:

Do not pretend that cached technician availability is real-time.

For assignment operations, use the backend when possible.

---

# 62. REAL-TIME DATA WARNING

Technician availability is operational data.

A cached value can become stale.

Therefore:

```text
Cached technician list
        ↓
Good for display
```

but:

```text
Assignment
        ↓
Must reach backend
        ↓
Backend validates current state
```

---

# 63. MEMBER 3 END-TO-END WORKFLOW

Your complete workflow should be:

```text
ADMIN / MANAGER LOGIN
        ↓
ADMIN DASHBOARD
        ↓
View Business Metrics
        ↓
View Pending Requests
        ↓
Open Appointment
        ↓
Check Branch
        ↓
Check Service
        ↓
Find Suitable Technician
        ↓
Check Availability
        ↓
Assign Technician
        ↓
Appointment = ASSIGNED
        ↓
Technician = BUSY
        ↓
Refresh Management Data
```

---

# 64. FULL MANAGEMENT ARCHITECTURE

```text
                    ADMIN / MANAGER
                           │
                           ▼
                    ┌─────────────┐
                    │ Android UI  │
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ ViewModel   │
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ Repository  │
                    └──────┬──────┘
                           │
              ┌────────────┴────────────┐
              ▼                         ▼
       ┌─────────────┐           ┌─────────────┐
       │  Retrofit   │           │    Room     │
       │  Remote API │           │ Local Cache │
       └──────┬──────┘           └─────────────┘
              │
              ▼
       ┌─────────────┐
       │  Cloudflare │
       │   Worker    │
       └──────┬──────┘
              │
              ▼
       ┌─────────────┐
       │ Cloudflare  │
       │     D1      │
       └─────────────┘
```

---

# 65. DEVELOPMENT PHASE 1 — API VERIFICATION

Before building the UI, verify:

```text
GET /api/admin/dashboard
GET /api/branches
GET /api/branches/{id}
GET /api/technicians
GET /api/technicians/{id}/services
PUT /api/technicians/{id}/services
PUT /api/appointments/{id}/assign
```

For each API record:

```text
Endpoint
HTTP method
Authentication required
Request body
Success response
Error response
```

Do not start writing complex UI around an API contract that has not been verified.

---

# 66. DEVELOPMENT PHASE 2 — RETROFIT

Create:

```text
AdminApiService.kt
```

Then create:

```text
DTO models
```

Examples:

```text
DashboardResponse
BranchResponse
TechnicianResponse
TechnicianServicesResponse
UpdateTechnicianServicesRequest
AssignTechnicianRequest
AppointmentResponse
```

---

# 67. DEVELOPMENT PHASE 3 — AUTHENTICATION

Verify that:

```text
ADMIN
```

or:

```text
MANAGER
```

can authenticate.

Then verify:

```text
Authorization: Bearer <TOKEN>
```

works for protected endpoints.

Test:

```text
Valid token
Expired token
Missing token
Wrong role
```

---

# 68. DEVELOPMENT PHASE 4 — ROOM

Create:

```text
BranchEntity
TechnicianEntity
AdminDao
AppDatabase
```

Then add only the queries actually required by your UI.

---

# 69. DEVELOPMENT PHASE 5 — REPOSITORY

Create:

```text
AdminRepository.kt
```

Responsibilities:

```text
API → DTO
DTO → Domain
Domain → Room
Room → UI
```

Also handle:

```text
Network errors
HTTP errors
Refresh
Mutation operations
```

---

# 70. DEVELOPMENT PHASE 6 — VIEWMODEL

Create:

```text
AdminViewModel.kt
```

Expose:

```text
dashboardState
branchState
technicianState
serviceState
assignmentState
loadingState
errorState
```

---

# 71. DEVELOPMENT PHASE 7 — DASHBOARD

Build first:

```text
Admin Dashboard
```

Then:

```text
Revenue
Pending Requests
Active Repairs
Available Technicians
```

Then:

```text
Refresh
Loading
Error
```

---

# 72. DEVELOPMENT PHASE 8 — BRANCHES

Build:

```text
Branch List
      ↓
Branch Details
      ↓
Technicians
      ↓
Location
```

---

# 73. DEVELOPMENT PHASE 9 — TECHNICIANS

Build:

```text
Technician Roster
      ↓
Search
      ↓
Filters
      ↓
Technician Details
```

---

# 74. DEVELOPMENT PHASE 10 — TECHNICIAN SERVICES

Build:

```text
Technician Details
      ↓
Services
      ↓
Edit
      ↓
Save
      ↓
API
      ↓
Refresh
```

---

# 75. DEVELOPMENT PHASE 11 — APPOINTMENT MANAGEMENT

Build:

```text
Pending Requests
      ↓
Appointment Details
      ↓
Assign Technician
```

---

# 76. DEVELOPMENT PHASE 12 — SMART ASSIGNMENT

Implement:

```text
Branch filter
Availability filter
Service/skill filter
Candidate selection
Confirmation
API assignment
Refresh
```

---

# 77. DEVELOPMENT PHASE 13 — POLISH

Add:

- Loading indicators
- Empty states
- Retry buttons
- Error messages
- Pull-to-refresh
- Search
- Filters
- Confirmation dialogs
- Success messages
- Smooth navigation
- Consistent UI
- Accessibility-friendly labels

---

# 78. DEVELOPMENT PHASE 14 — FINAL TESTING

Test the entire module from login to assignment.

```text
LOGIN
 ↓
DASHBOARD
 ↓
BRANCHES
 ↓
TECHNICIANS
 ↓
SERVICES
 ↓
PENDING APPOINTMENT
 ↓
ASSIGN TECHNICIAN
 ↓
REFRESH
```

---

# 79. MEMBER 3 TESTING — DASHBOARD

- [ ] Admin login works.
- [ ] Manager login works if supported.
- [ ] Dashboard loads.
- [ ] Revenue is correct.
- [ ] Pending request count is correct.
- [ ] Active repair count is correct.
- [ ] Available technician count is correct.
- [ ] Refresh works.
- [ ] Loading state works.
- [ ] Error state works.
- [ ] No hard-coded production values.

---

# 80. MEMBER 3 TESTING — BRANCHES

- [ ] Branch list loads.
- [ ] Branch names display.
- [ ] Addresses display.
- [ ] Cities display.
- [ ] Phone numbers display.
- [ ] Opening times display.
- [ ] Closing times display.
- [ ] Location information is available.
- [ ] Branch details open.
- [ ] Branch technicians are correct.
- [ ] Map/location feature works if implemented.

---

# 81. MEMBER 3 TESTING — TECHNICIANS

- [ ] Technician list loads.
- [ ] Names display.
- [ ] Employee codes display.
- [ ] Specializations display.
- [ ] Branches display.
- [ ] Availability displays.
- [ ] Search works.
- [ ] Branch filtering works.
- [ ] Availability filtering works.
- [ ] Technician details open.
- [ ] Empty state works.

---

# 82. MEMBER 3 TESTING — SERVICES

- [ ] Technician services load.
- [ ] Correct services display.
- [ ] Service selection works.
- [ ] Correct service IDs are sent.
- [ ] PUT request succeeds.
- [ ] Success message appears.
- [ ] UI refreshes.
- [ ] Error handling works.

---

# 83. MEMBER 3 TESTING — ASSIGNMENT

- [ ] Pending appointment is visible.
- [ ] Assign button works.
- [ ] Correct appointment is selected.
- [ ] Correct branch is identified.
- [ ] Available technicians are shown.
- [ ] Wrong-branch technicians are excluded.
- [ ] Busy technicians are excluded.
- [ ] Off-duty technicians are excluded.
- [ ] On-leave technicians are excluded.
- [ ] Service compatibility is checked where implemented.
- [ ] Assignment request is sent.
- [ ] Backend confirms assignment.
- [ ] Appointment changes to `ASSIGNED`.
- [ ] Technician changes to `BUSY` where backend behavior provides this.
- [ ] UI refreshes.
- [ ] Error is shown if assignment fails.

---

# 84. SECURITY TESTING

Verify:

```text
No token in source code
No password in logs
No JWT in logs
No secrets committed to Git
```

Test:

```text
Missing token
Invalid token
Expired token
Customer token
Admin token
Manager token
Technician token
```

The backend must decide which roles are allowed.

---

# 85. PERFORMANCE / QUALITY

Avoid:

- API calls directly from every UI component
- Duplicate network calls
- Blocking the main thread
- Loading the same data repeatedly
- Hard-coded technician data
- Hard-coded branch data
- Hard-coded dashboard values

Prefer:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
API / Room
```

---

# 86. WHAT SHOULD BE HARD-CODED?

Static UI labels can be hard-coded:

```text
"Total Revenue"
"Available Technicians"
"Assign Technician"
```

Backend data should NOT be hard-coded:

```text
Technician names
Technician status
Branch data
Revenue
Appointment IDs
Service IDs
```

---

# 87. WHAT DATA COMES FROM THE BACKEND?

Examples:

```text
Technician name
Employee code
Specialization
Availability
Branch
Service IDs
Branch coordinates
Dashboard metrics
Appointment status
Assigned technician
```

The backend/database is the source of truth.

---

# 88. GIT WORK FOR MEMBER 3

Keep your work organized.

Recommended branch:

```text
member03/admin-management
```

or your team's agreed branch name.

Commit by feature.

Example:

```text
feat: add admin dashboard
feat: add technician roster
feat: add branch details
feat: add technician services
feat: add technician assignment
fix: handle assignment error
```

Avoid giant commits containing unrelated changes.

---

# 89. MEMBER 3 DEFINITION OF DONE

Your module is NOT finished simply because the dashboard opens.

It is finished when:

```text
Admin Login
     ↓
Dashboard
     ↓
Business Metrics
     ↓
Branches
     ↓
Technicians
     ↓
Technician Skills
     ↓
Pending Appointments
     ↓
Suitable Technician
     ↓
Assignment
     ↓
Backend Confirmation
     ↓
Updated UI
```

works reliably.

---

# 90. FINAL MEMBER 3 DELIVERABLES

Your final module should contain:

## UI

```text
1. Admin/Manager entry/login
2. Executive Dashboard
3. Branch List
4. Branch Details
5. Technician Roster
6. Technician Details
7. Technician Services
8. Pending Appointment Management
9. Smart Assignment UI
```

## Data

```text
Branch models
Technician models
Dashboard models
Appointment/assignment models
Service models
```

## Networking

```text
AdminApiService
AdminRepository
Authentication
```

## Local Storage

```text
Room
BranchEntity
TechnicianEntity
AdminDao
AppDatabase
```

## State Management

```text
AdminViewModel
StateFlow / project-standard state handling
```

## Quality

```text
Loading
Error
Empty
Refresh
Validation
Security
```

---

# 91. STRONG OPTIONAL FEATURES

If time permits, consider:

### Branch map

```text
Branch → Map Marker
```

### Technician search

```text
Search by name/code
```

### Technician filters

```text
Branch
Availability
Specialization
```

### Dashboard navigation

```text
Metric Card → Relevant Screen
```

### Offline cache

```text
Room → Cached branches/technicians
```

### Better assignment intelligence

```text
Branch
+
Availability
+
Service compatibility
```

Only implement enhancements that can be supported correctly by the backend.

---

# 92. IMPORTANT COURSEWORK CONNECTION

The coursework marking guidance describes higher performance as involving:

- Comprehensive analysis
- Clear system design
- Appropriate modelling
- Fully implemented functionality
- Reliable and user-friendly application
- Additional useful features where appropriate
- Advanced Android technologies
- Clean reusable code
- Efficient data management
- Professional documentation
- Clear and confident demonstration

Therefore, Member 3 should not stop at a static dashboard.

A stronger implementation demonstrates:

```text
REAL API DATA
+
REAL DATABASE DATA
+
ROOM CACHE
+
ROLE-BASED ACCESS
+
DASHBOARD
+
BRANCH MANAGEMENT
+
TECHNICIAN MANAGEMENT
+
SERVICE/SKILL MANAGEMENT
+
SMART ASSIGNMENT
+
GPS/MAP WHERE APPROPRIATE
+
ERROR HANDLING
+
CLEAN ARCHITECTURE
```

---

# 93. FINAL DEMONSTRATION SCRIPT FOR MEMBER 3

Use this as your practical demonstration sequence.

## Part 1 — Login

```text
Open TECHFIX
      ↓
Login as Admin/Manager
      ↓
Management Dashboard
```

## Part 2 — Dashboard

Show:

```text
Total Revenue
Pending Requests
Active Repairs
Available Technicians
```

Explain that the values come from the backend.

## Part 3 — Branches

Open:

```text
Branches
```

Select:

```text
TechFix Colombo
```

Show:

```text
Address
City
Phone
Opening/Closing
Location
Technicians
```

## Part 4 — Technicians

Open:

```text
Technicians
```

Show:

```text
Nimal Fernando
TF-T001
Laptop Hardware
AVAILABLE
TechFix Colombo
```

Demonstrate search/filter.

## Part 5 — Technician Services

Open the technician.

Show supported services.

Modify services if the backend supports the update.

Press:

```text
SAVE
```

Show success.

## Part 6 — Pending Appointment

Open:

```text
Pending Requests
```

Select an appointment.

Show:

```text
Customer
Device
Service
Branch
Status
```

## Part 7 — Assignment

Press:

```text
ASSIGN TECHNICIAN
```

Show only suitable technicians.

Select one.

Confirm.

## Part 8 — Result

Show:

```text
Appointment:
REQUESTED → ASSIGNED
```

and the technician's updated availability when returned by the backend.

## Part 9 — Refresh

Refresh the dashboard/list.

Show that the data has changed.

---

# 94. COMPLETE MEMBER 3 CHECKLIST

## Authentication

- [ ] Admin/Manager authentication
- [ ] JWT/token handling
- [ ] Secure storage
- [ ] 401 handling
- [ ] 403 handling
- [ ] Logout/session handling

## Dashboard

- [ ] Total Revenue
- [ ] Pending Requests
- [ ] Active Repairs
- [ ] Available Technicians
- [ ] API integration
- [ ] Refresh
- [ ] Loading
- [ ] Error
- [ ] Empty state

## Branches

- [ ] Branch list
- [ ] Branch details
- [ ] Address
- [ ] City
- [ ] Phone
- [ ] Opening time
- [ ] Closing time
- [ ] Coordinates
- [ ] Branch technicians
- [ ] Map/GPS enhancement

## Technicians

- [ ] Technician roster
- [ ] Employee code
- [ ] Name
- [ ] Specialization
- [ ] Branch
- [ ] Availability
- [ ] Search
- [ ] Filters
- [ ] Technician details

## Technician Services

- [ ] Load services
- [ ] Display services
- [ ] Select/unselect services
- [ ] Save changes
- [ ] API update
- [ ] Success state
- [ ] Error handling
- [ ] Refresh

## Appointment Management

- [ ] Pending requests
- [ ] Appointment information
- [ ] Branch
- [ ] Service
- [ ] Device
- [ ] Customer
- [ ] Status
- [ ] Assign button

## Smart Assignment

- [ ] Read branch
- [ ] Read service
- [ ] Load technicians
- [ ] Filter available
- [ ] Filter branch
- [ ] Filter service compatibility
- [ ] Candidate selection
- [ ] Confirmation
- [ ] API assignment
- [ ] Backend validation
- [ ] Refresh
- [ ] Assignment success/error

## Architecture

- [ ] Retrofit
- [ ] Repository
- [ ] ViewModel
- [ ] Room
- [ ] DAO
- [ ] Entities
- [ ] StateFlow/appropriate state
- [ ] Clean package structure

## Final Quality

- [ ] No hard-coded backend data
- [ ] No hard-coded credentials
- [ ] No JWT logging
- [ ] No crashes
- [ ] Proper error handling
- [ ] Loading states
- [ ] Empty states
- [ ] Refresh
- [ ] Clean UI
- [ ] End-to-end test
- [ ] Git commits organized
- [ ] Demonstration ready

---

# 95. THE MOST IMPORTANT THING TO REMEMBER

Your responsibility is **not simply "make an admin screen."**

Your responsibility is to build a complete **management workflow**.

The difference is:

### Weak implementation

```text
Admin Login
   ↓
Static Dashboard
   ↓
Fake Technician List
```

### Strong implementation

```text
Admin/Manager Login
       ↓
Authenticated API
       ↓
Live Dashboard Metrics
       ↓
Real Branch Data
       ↓
Real Technician Data
       ↓
Availability + Skills
       ↓
Real Pending Appointment
       ↓
Eligible Technician Filtering
       ↓
Backend Assignment
       ↓
Updated Appointment
       ↓
Updated Technician State
       ↓
Refresh / Cache
```

The second version is the target.

---

# 96. FINAL SIMPLE DESCRIPTION OF YOUR JOB

If your lecturer asks:

> **"What is your responsibility in the project?"**

You can explain:

> **"I am responsible for the Admin and overall management module of the TECHFIX Android application. I manage the management dashboard, business metrics, branch information, technician roster, technician availability and service skills, and the technician assignment workflow. My module communicates with the backend using Retrofit and uses Room for local management-data caching. I also provide branch location/map information and ensure that management can select and assign an appropriate available technician to pending repair appointments."**

---

# 97. FINAL ARCHITECTURE SUMMARY

```text
                 MEMBER 3
             ADMIN / MANAGER
                     │
                     ▼
              ┌─────────────┐
              │    LOGIN    │
              └──────┬──────┘
                     │
                     ▼
              ┌─────────────┐
              │  DASHBOARD  │
              └──────┬──────┘
                     │
       ┌─────────────┼──────────────┐
       │             │              │
       ▼             ▼              ▼
   BRANCHES     TECHNICIANS     APPOINTMENTS
       │             │              │
       ▼             ▼              ▼
   DETAILS       SERVICES       ASSIGNMENT
       │             │              │
       └─────────────┼──────────────┘
                     │
                     ▼
                VIEWMODEL
                     │
                     ▼
                REPOSITORY
                /         \
               /           \
          RETROFIT        ROOM
             │              │
             ▼              ▼
       CLOUDFLARE API   LOCAL CACHE
             │
             ▼
        CLOUDFLARE D1
```

---

# 98. FINAL BOTTOM LINE

## Member 3 = Management Control Center

### You build:

```text
ADMIN / MANAGER
       ↓
DASHBOARD
       ↓
BUSINESS METRICS
       ↓
BRANCHES
       ↓
TECHNICIANS
       ↓
TECHNICIAN SKILLS
       ↓
PENDING APPOINTMENTS
       ↓
SMART TECHNICIAN ASSIGNMENT
```

### Your main technical responsibilities:

```text
Retrofit
+
Room / SQLite
+
Repository
+
ViewModel
+
State Management
+
Role-based Authentication
+
API Integration
+
GPS/Map where appropriate
```

### Your final goal:

> **Give management a real, connected control center for monitoring branches and staff and dispatching the right technician to the right repair request.**

