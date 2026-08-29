# 🛠️ TECHFIX Android --- Admin Module / Admin Path Guide

**Developer:** Nima\
**Member:** Member 3\
**Module:** Admin Control Center\
**Main responsibility:** Branch Management + Technician Management +
Technician Assignment + High-Level Business Monitoring

------------------------------------------------------------------------

## 1. Purpose of the Admin Path

The Admin Path is the management side of the TECHFIX Android
application.

It allows an authorized **ADMIN/MANAGER** to:

1.  View overall business information.
2.  View branches.
3.  View technicians.
4.  Monitor technician availability.
5.  View pending appointments requiring assignment.
6.  Assign an available technician.
7.  View appointment and repair status.
8.  Manage technician service skills.
9.  View branch-level staff information.
10. Optionally view branch locations on Google Maps.
11. Refresh data from the Cloudflare API.
12. Cache important data locally with Room.

------------------------------------------------------------------------

## 2. Role Boundary

### Admin / Manager

Responsible for:

-   Dashboard
-   Branch monitoring
-   Technician roster
-   Technician availability
-   Technician assignment
-   Technician skills
-   Appointment monitoring
-   Revenue/repair metrics
-   High-level system monitoring

### Technician

A separate role responsible for:

-   Viewing assigned repairs
-   Diagnosing devices
-   Updating repair status
-   Adding repair notes
-   Testing devices
-   Completing repairs

Do **not** put technician repair-work functionality inside the Admin
Dashboard.

### Customer

Responsible for:

-   Login/register
-   Device management
-   Booking appointments
-   Tracking appointments
-   Viewing technician/repair progress
-   Payment

------------------------------------------------------------------------

## 3. Admin Navigation

``` text
ADMIN LOGIN
     │
     ▼
ADMIN DASHBOARD
     │
     ├── Dashboard
     ├── Branches
     │     ├── Branch List
     │     └── Branch Details
     ├── Technicians
     │     ├── Technician List
     │     ├── Technician Details
     │     └── Technician Skills
     ├── Appointments
     │     ├── Pending Assignment
     │     ├── Assigned
     │     ├── Diagnosing
     │     ├── Repairing
     │     ├── Testing
     │     └── Completed
     └── Profile / Logout
```

------------------------------------------------------------------------

## 4. Executive Dashboard

Recommended metric cards:

  -----------------------------------------------------------------------
  Metric                              Meaning
  ----------------------------------- -----------------------------------
  **Total Revenue**                   Sum of payments whose status is
                                      `PAID`

  **Pending Requests**                Appointments waiting for technician
                                      assignment

  **Active Repairs**                  Appointments in `DIAGNOSING`,
                                      `REPAIRING`, or `TESTING`

  **Available Technicians**           Technicians with
                                      `availability_status = AVAILABLE`
  -----------------------------------------------------------------------

Example:

``` text
┌──────────────────────────────────┐
│          TECHFIX ADMIN           │
│                                  │
│  Welcome, Admin                  │
│                                  │
│  TOTAL REVENUE                   │
│  LKR 125,000                     │
│                                  │
│  PENDING REQUESTS       8        │
│  ACTIVE REPAIRS        12        │
│  AVAILABLE TECHS        5        │
│                                  │
│  Recent Appointments             │
└──────────────────────────────────┘
```

Use:

-   Pull-to-refresh
-   Loading indicator
-   Empty state
-   Error state
-   Retry button

------------------------------------------------------------------------

## 5. Branch Management

### Branch fields

``` text
id
name
address
city
latitude
longitude
phone
opening_time
closing_time
```

### Branch List

Example:

``` text
TECHFIX COLOMBO
Colombo
Open: 08:00 - 18:00

TECHFIX GALLE
Galle
Open: 08:00 - 18:00
```

### Branch Details

Display:

``` text
Branch Name
Address
City
Phone
Opening Time
Closing Time
Latitude
Longitude
Technicians
```

If coordinates exist, provide:

``` text
View on Map
```

------------------------------------------------------------------------

## 6. Technician Management

The tested technician API returns fields including:

``` text
id
employee_code
specialization
availability_status
hire_date
is_active
user_id
first_name
last_name
email
phone
profile_image_url
branch_id
branch_name
branch_city
```

Example:

``` text
TECH-001
Employee Code: TF-T001
Name: Nimal Fernando
Specialization: Laptop Hardware
Status: BUSY
Branch: TechFix Colombo
```

------------------------------------------------------------------------

## 7. Technician Availability

Expected states:

``` text
AVAILABLE
BUSY
OFF_DUTY
ON_LEAVE
```

Recommended UI:

``` text
AVAILABLE  → Green
BUSY       → Red
OFF_DUTY   → Gray
ON_LEAVE   → Orange
```

------------------------------------------------------------------------

## 8. Technician Roster Screen

Use RecyclerView or LazyColumn.

Example:

``` text
┌────────────────────────────────────┐
│ 👤 Nimal Fernando                  │
│    TF-T001                          │
│    Laptop Hardware                  │
│    TechFix Colombo                  │
│    ● BUSY                           │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ 👤 Amal Perera                     │
│    TF-T003                          │
│    Computer Hardware                │
│    TechFix Galle                    │
│    ● AVAILABLE                      │
└────────────────────────────────────┘
```

Add:

-   Search
-   Filter by branch
-   Filter by availability
-   Filter by specialization
-   Pull-to-refresh

------------------------------------------------------------------------

## 9. Confirmed Technician API

### Request

``` http
GET /api/technicians
```

This endpoint was successfully tested against the deployed Cloudflare
Worker.

Current test data included:

  ----------------------------------------------------------------------------
  ID             Name           Specialization   Status         Branch
  -------------- -------------- ---------------- -------------- --------------
  TECH-003       Amal Perera    Computer         AVAILABLE      TechFix Galle
                                Hardware                        

  TECH-004       Tharindu       Mobile Phone     AVAILABLE      TechFix Galle
                 Jayasinghe     Repair                          

  TECH-001       Nimal Fernando Laptop Hardware  BUSY           TechFix
                                                                Colombo

  TECH-002       Sahan Silva    Mobile Phone     BUSY           TechFix
                                Repair                          Colombo
  ----------------------------------------------------------------------------

------------------------------------------------------------------------

## 10. Technician Details

When the admin selects a technician:

``` text
Technician Details

Name
Employee Code
Specialization
Availability
Branch
Email
Phone
Hire Date

Services / Skills
-----------------
Laptop Screen Replacement
Laptop Repair
Hardware Diagnostics
```

Possible actions:

``` text
View Services
Manage Services
View Assigned Repairs
```

------------------------------------------------------------------------

## 11. Technician Service Skills

Planned endpoints:

``` http
GET /api/technicians/{id}/services
```

``` http
PUT /api/technicians/{id}/services
```

Example request:

``` json
{
  "service_ids": [
    "SVC-001",
    "SVC-002"
  ]
}
```

**Important:** Verify these exact service-management routes against the
deployed Worker before implementing them in Android. Do not assume an
endpoint exists just because it is listed in a planning document.

------------------------------------------------------------------------

## 12. Appointment Assignment

This is the most important Admin action.

``` text
Customer creates appointment
          │
          ▼
       REQUESTED
          │
          ▼
Admin opens appointment
          │
          ▼
Assign Technician
          │
          ▼
Show available technicians
          │
          ▼
Admin selects technician
          │
          ▼
PUT /api/appointments/{id}/assign
          │
          ▼
       ASSIGNED
```

### Confirmed working endpoint

``` http
PUT /api/appointments/{id}/assign
```

Request:

``` json
{
  "technician_id": "TECH-001"
}
```

Successful response tested:

``` json
{
  "success": true,
  "message": "Technician assigned successfully"
}
```

The appointment then contained:

``` text
status: ASSIGNED
technician_id: TECH-001
technician_first_name: Nimal
technician_last_name: Fernando
technician_employee_code: TF-T001
```

------------------------------------------------------------------------

## 13. Smart Assignment Rules

Do not allow the admin to assign any arbitrary technician.

Recommended filtering:

``` text
availability_status == AVAILABLE
```

and:

``` text
technician.branch_id == appointment.branch_id
```

Preferably also check service compatibility:

``` text
technician supports appointment.service_id
```

Flow:

``` text
Appointment
    │
    ├── Branch
    ├── Service
    └── Device
          │
          ▼
Find technicians
          │
          ├── AVAILABLE?
          │      └── No → Hide
          │
          ├── Same branch?
          │      └── No → Hide
          │
          └── Supports service?
                 └── No → Hide
          │
          ▼
       Show list
```

------------------------------------------------------------------------

## 14. Assign Technician Bottom Sheet

Use `BottomSheetDialogFragment` or a Compose bottom sheet.

``` text
┌────────────────────────────────────┐
│ Assign Technician                  │
│                                    │
│ Laptop Screen Replacement          │
│ Branch: TechFix Colombo            │
│                                    │
│ ○ Nimal Fernando                   │
│   TF-T001                          │
│   Laptop Hardware                  │
│   AVAILABLE                        │
│                                    │
│ ○ Sahan Silva                      │
│   TF-T002                          │
│   Mobile Phone Repair              │
│   BUSY                             │
│                                    │
│          [ ASSIGN ]                │
└────────────────────────────────────┘
```

BUSY technicians should not be selectable.

------------------------------------------------------------------------

## 15. Appointment Status Workflow

The tested workflow is:

``` text
REQUESTED
     │
     ▼
ASSIGNED
     │
     ▼
DIAGNOSING
     │
     ▼
REPAIRING
     │
     ▼
TESTING
     │
     ▼
COMPLETED
```

Your backend testing has already confirmed:

``` text
DIAGNOSING → REPAIRING → TESTING
```

and:

``` text
COMPLETED
```

The Admin dashboard should monitor these statuses rather than performing
technician repair operations.

------------------------------------------------------------------------

## 16. Appointment History

The working history endpoint is:

``` http
GET /api/appointments/{id}/history
```

A successful response returned history containing:

``` text
appointment_id
status
note
changed_by
created_at
changed_by_first_name
changed_by_last_name
changed_by_role
```

Example event:

``` text
REQUESTED
Appointment created by customer
John Perera
```

Use this to build an Admin timeline.

------------------------------------------------------------------------

## 17. Appointment Timeline UI

``` text
REQUESTED
   │
   └── Appointment created by customer
       John Perera
       08:58

ASSIGNED
   │
   └── Technician assigned
       Nimal Fernando
       10:34

DIAGNOSING
   │
   └── Diagnosis started

REPAIRING
   │
   └── Repair started

TESTING
   │
   └── Device testing

COMPLETED
```

------------------------------------------------------------------------

## 18. Recommended Android Package Structure

Your current base package is:

``` text
com.mad.techfix
```

Recommended Admin structure:

``` text
com.mad.techfix
│
├── data
│   ├── local
│   │   └── database
│   │       ├── AppDatabase
│   │       ├── TechFixDao
│   │       ├── AdminDao
│   │       ├── TechnicianEntity
│   │       ├── BranchEntity
│   │       └── AppointmentEntity
│   │
│   ├── models
│   │   └── admin
│   │
│   └── network
│       └── AdminApiService.kt
│
├── repository
│   └── AdminRepository.kt
│
├── ui
│   └── admin
│       ├── dashboard
│       ├── branches
│       ├── technicians
│       └── appointments
│
└── utils
```

### Important

Your existing project already has a `data.local.database` package
containing:

``` text
AppDatabase
SparePartEntity
TechFixDao
```

Prefer extending the existing Room database instead of creating a second
database only for Admin.

------------------------------------------------------------------------

## 19. Retrofit AdminApiService

Recommended structure:

``` kotlin
interface AdminApiService {

    @GET("api/admin/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("api/branches")
    suspend fun getBranches(): BranchesResponse

    @GET("api/branches/{id}")
    suspend fun getBranch(
        @Path("id") id: String
    ): BranchDetailsResponse

    @GET("api/technicians")
    suspend fun getTechnicians(): TechniciansResponse

    @GET("api/technicians/{id}/services")
    suspend fun getTechnicianServices(
        @Path("id") id: String
    ): TechnicianServicesResponse

    @PUT("api/technicians/{id}/services")
    suspend fun updateTechnicianServices(
        @Path("id") id: String,
        @Body request: UpdateTechnicianServicesRequest
    ): TechnicianServicesResponse

    @PUT("api/appointments/{id}/assign")
    suspend fun assignTechnician(
        @Path("id") id: String,
        @Body request: AssignTechnicianRequest
    ): AppointmentResponse
}
```

The exact dashboard/branch/service routes must match your deployed
Worker.

------------------------------------------------------------------------

## 20. Authentication

Protected Admin requests use:

``` http
Authorization: Bearer <ADMIN_JWT_TOKEN>
```

Recommended interceptor:

``` kotlin
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = tokenProvider()

        val request = chain.request()
            .newBuilder()
            .apply {
                if (!token.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}
```

Do not hard-code:

-   Admin password
-   JWT
-   API secrets

The backend must remain the final authority for role authorization.

------------------------------------------------------------------------

## 21. Admin Login Flow

``` text
Admin Login Screen
       │
       ▼
POST /api/auth/login
       │
       ▼
Cloudflare Worker
       │
       ├── Validate credentials
       ├── Validate account
       └── Validate role
       │
       ▼
JWT Token
       │
       ▼
Save authenticated session
       │
       ▼
Admin Dashboard
```

The Android app should not trust a locally selected "Admin" option as
authorization.

------------------------------------------------------------------------

## 22. Room Offline Cache

Recommended entities:

``` text
BranchEntity
TechnicianEntity
AppointmentEntity
DashboardMetricsEntity
```

Example:

``` kotlin
@Entity(tableName = "local_technicians")
data class TechnicianEntity(

    @PrimaryKey
    val id: String,

    val employeeCode: String,

    val firstName: String,

    val lastName: String,

    val specialization: String,

    val status: String,

    val branchId: String,

    val branchName: String? = null
)
```

------------------------------------------------------------------------

## 23. Admin DAO

``` kotlin
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
        "SELECT * FROM local_technicians " +
        "WHERE branchId = :branchId " +
        "AND status = 'AVAILABLE'"
    )
    suspend fun getAvailableTechniciansForBranch(
        branchId: String
    ): List<TechnicianEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnicians(
        technicians: List<TechnicianEntity>
    )

    @Query("DELETE FROM local_technicians")
    suspend fun clearTechnicians()
}
```

------------------------------------------------------------------------

## 24. Room Architecture

Use one main Room database:

``` text
AppDatabase
    │
    ├── TechFixDao
    ├── AdminDao
    ├── SparePartEntity
    ├── TechnicianEntity
    ├── BranchEntity
    └── AppointmentEntity
```

This is cleaner than creating a completely separate Admin database.

------------------------------------------------------------------------

## 25. Repository

Create:

``` text
AdminRepository.kt
```

Architecture:

``` text
UI
 │
 ▼
ViewModel
 │
 ▼
Repository
 │
 ├──── Retrofit API
 │
 └──── Room database
```

Example:

``` kotlin
class AdminRepository(
    private val api: AdminApiService,
    private val dao: AdminDao
) {

    suspend fun refreshTechnicians() {

        val response = api.getTechnicians()

        if (response.success) {
            dao.insertTechnicians(
                response.data.map {
                    it.toEntity()
                }
            )
        }
    }

    fun observeTechnicians(
        branchId: String
    ): Flow<List<TechnicianEntity>> {
        return dao.getTechniciansByBranch(branchId)
    }
}
```

------------------------------------------------------------------------

## 26. ViewModel

Create:

``` text
AdminViewModel.kt
```

Responsibilities:

-   Request data
-   Expose UI state
-   Handle loading
-   Handle errors
-   Trigger refresh
-   Call repository
-   Never contain direct Retrofit implementation

Example:

``` kotlin
data class AdminUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalRevenue: Double = 0.0,
    val pendingRequests: Int = 0,
    val activeRepairs: Int = 0,
    val availableTechnicians: Int = 0
)
```

------------------------------------------------------------------------

## 27. Admin UI Package Layout

``` text
ui/admin/
│
├── dashboard/
│   ├── AdminDashboardFragment.kt
│   ├── AdminDashboardViewModel.kt
│   └── AdminDashboardAdapter.kt
│
├── branches/
│   ├── BranchesFragment.kt
│   ├── BranchDetailsFragment.kt
│   └── BranchAdapter.kt
│
├── technicians/
│   ├── TechniciansFragment.kt
│   ├── TechnicianDetailsFragment.kt
│   ├── TechnicianAdapter.kt
│   └── AssignTechnicianBottomSheet.kt
│
└── appointments/
    ├── AdminAppointmentsFragment.kt
    ├── AppointmentDetailsFragment.kt
    └── AppointmentTimelineAdapter.kt
```

------------------------------------------------------------------------

## 28. Data Models

### Branch

``` kotlin
data class Branch(
    val id: String,
    val name: String,
    val address: String?,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val openingTime: String?,
    val closingTime: String?
)
```

### Technician

``` kotlin
data class Technician(
    val id: String,
    val employeeCode: String,
    val specialization: String?,
    val availabilityStatus: String,
    val hireDate: String?,
    val isActive: Int,
    val userId: String?,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val profileImageUrl: String?,
    val branchId: String,
    val branchName: String?,
    val branchCity: String?
)
```

------------------------------------------------------------------------

## 29. Admin Appointment Data

Important appointment fields:

``` text
id
appointment_number
customer_id
device_id
service_id
branch_id
technician_id
requested_date
requested_time
problem_description
status
estimated_price
final_price
created_at
updated_at
technician information
service information
branch information
```

Nullable values are valid, for example:

``` text
technician_id = null
final_price = null
```

for appointments that have not reached those stages.

------------------------------------------------------------------------

## 30. Optional Google Maps Integration

For branch locations, use the **Maps SDK for Android**.

Possible flow:

``` text
Branches
    │
    ▼
Branch Details
    │
    ▼
View on Map
    │
    ▼
Google Map
    │
    └── Branch marker
```

Use:

``` text
latitude
longitude
```

to create a `LatLng` and place a marker.

Google's current Maps SDK for Android supports maps, markers, camera
movement, overlays, and user interaction. Google currently requires a
configured API key and billing for Maps Platform usage; restrict the key
before production.

For the university project, branch markers are sufficient unless the
requirements specifically demand live technician tracking.

------------------------------------------------------------------------

## 31. Admin Map Screen

``` text
┌────────────────────────────────────┐
│ Branch Map                         │
│                                    │
│       📍 Colombo                   │
│                                    │
│                    📍 Galle        │
│                                    │
│                                    │
├────────────────────────────────────┤
│ Selected Branch                    │
│ TechFix Colombo                    │
│ Colombo                            │
│                                    │
│ [ View Details ]                   │
└────────────────────────────────────┘
```

Do not add complex live tracking to this screen unless required.

------------------------------------------------------------------------

## 32. UI States

Every Admin screen should handle:

### Loading

``` text
Loading...
```

### Error

``` text
Unable to load technicians.

[ RETRY ]
```

### Empty

``` text
No technicians found.
```

### Offline

``` text
You're offline.

Showing last synchronized data.
```

------------------------------------------------------------------------

## 33. Admin API Checklist

  -----------------------------------------------------------------------------------------
  Feature            Method            Endpoint                           Current status
  ------------------ ----------------- ---------------------------------- -----------------
  Admin login        POST              `/api/auth/login`                  Existing

  Dashboard          GET               `/api/admin/dashboard`             Verify/build

  Branch list        GET               `/api/branches`                    Verify/build

  Branch details     GET               `/api/branches/{id}`               Verify/build

  Technician list    GET               `/api/technicians`                 **Tested
                                                                          working**

  Technician         GET               `/api/technicians/{id}/services`   Verify
  services                                                                

  Update technician  PUT               `/api/technicians/{id}/services`   Verify
  services                                                                

  Assign technician  PUT               `/api/appointments/{id}/assign`    **Tested
                                                                          working**

  Appointment        GET               `/api/appointments/{id}`           **Tested
  details                                                                 working**

  Appointment        GET               `/api/appointments/{id}/history`   **Tested
  history                                                                 working**

  Appointment list   GET               `/api/appointments`                Tested

  Status workflow    PUT               Project-specific status route      Tested

  Payments/revenue   GET               Project-specific payment route     Member 4 / verify
  -----------------------------------------------------------------------------------------

------------------------------------------------------------------------

## 34. What Member 3 Should NOT Duplicate

### Member 1

Authentication + Customer UI.

Do not rebuild:

``` text
Customer registration
Customer login
Customer profile
Customer device UI
```

### Member 2

Repair booking + tracking.

Do not rebuild the complete customer booking flow.

### Member 4

Spare parts + payment + reports.

Do not duplicate:

``` text
Payment processing
Spare parts inventory
Financial reporting
```

### Member 3

Focus on:

``` text
ADMIN
 │
 ├── Dashboard
 ├── Branches
 ├── Technicians
 ├── Technician availability
 ├── Technician services
 ├── Appointment assignment
 └── High-level monitoring
```

------------------------------------------------------------------------

# 35. Exact Development Order

## Phase 1 --- Project structure

Create:

``` text
data/models/admin
data/network/AdminApiService.kt
repository/AdminRepository.kt
ui/admin/dashboard
ui/admin/branches
ui/admin/technicians
ui/admin/appointments
```

## Phase 2 --- Authentication

Make sure:

``` text
Admin login
    ↓
JWT
    ↓
Admin session
    ↓
Admin dashboard
```

works.

## Phase 3 --- Retrofit

Implement and test:

``` text
GET /api/technicians
GET /api/appointments
GET /api/appointments/{id}
GET /api/appointments/{id}/history
PUT /api/appointments/{id}/assign
```

## Phase 4 --- Technician screen

Build:

``` text
Technician List
Search
Filter
Technician Details
Availability
Branch
```

## Phase 5 --- Branch screen

Build:

``` text
Branch List
Branch Details
```

Then optionally add Google Maps.

## Phase 6 --- Appointment management

Build:

``` text
Admin Appointment List
Appointment Details
Appointment History
Assign Technician
```

## Phase 7 --- Smart assignment

Implement:

``` text
AVAILABLE
+
same branch
+
compatible service
```

## Phase 8 --- Dashboard

After the underlying APIs work, build:

``` text
Revenue
Pending Requests
Active Repairs
Available Technicians
```

Avoid hard-coded dashboard values.

## Phase 9 --- Room

Add:

``` text
BranchEntity
TechnicianEntity
AppointmentEntity
DashboardMetricsEntity
AdminDao
```

Synchronize:

``` text
API → Room → UI
```

## Phase 10 --- Final testing

Test:

``` text
ADMIN LOGIN
     ↓
ADMIN DASHBOARD
     ↓
VIEW APPOINTMENT
     ↓
VIEW BRANCH
     ↓
VIEW TECHNICIANS
     ↓
FILTER AVAILABLE TECHNICIANS
     ↓
ASSIGN TECHNICIAN
     ↓
APPOINTMENT = ASSIGNED
     ↓
TECHNICIAN WORKFLOW
     ↓
DIAGNOSING
     ↓
REPAIRING
     ↓
TESTING
     ↓
COMPLETED
     ↓
ADMIN SEES UPDATED STATUS
```

------------------------------------------------------------------------

# 36. Definition of Done

-   [ ] Admin can log in.
-   [ ] Admin JWT is attached to protected requests.
-   [ ] Admin dashboard loads real backend data.
-   [ ] Dashboard has loading/error/empty states.
-   [ ] Branch list loads.
-   [ ] Branch details load.
-   [ ] Technician list loads.
-   [ ] Technician details load.
-   [ ] Technician status is displayed.
-   [ ] Technician can be filtered by branch.
-   [ ] Technician can be filtered by availability.
-   [ ] Technician services can be viewed.
-   [ ] Technician services can be updated if the backend endpoint
    exists.
-   [ ] Admin can view appointments.
-   [ ] Admin can view appointment details.
-   [ ] Admin can view appointment history.
-   [ ] Admin can open assignment UI.
-   [ ] Busy technicians cannot be assigned.
-   [ ] Technician assignment succeeds.
-   [ ] Appointment changes to `ASSIGNED`.
-   [ ] Assigned technician information appears.
-   [ ] Admin sees updated repair status.
-   [ ] Room cache works.
-   [ ] Offline cached data can be displayed.
-   [ ] Logout works.
-   [ ] No password or JWT is hard-coded.
-   [ ] API errors are handled gracefully.

------------------------------------------------------------------------

# 37. Final Architecture

``` text
                         ┌───────────────────┐
                         │   ADMIN LOGIN     │
                         └─────────┬─────────┘
                                   │
                                   ▼
                         ┌───────────────────┐
                         │       JWT         │
                         └─────────┬─────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────┐
│                    ADMIN ANDROID APP                    │
│                                                         │
│  Dashboard   Branches   Technicians   Appointments      │
│      │           │           │              │           │
│      └───────────┴───────────┴──────────────┘           │
│                          │                              │
│                       ViewModel                         │
│                          │                              │
│                       Repository                        │
│                    ┌─────┴─────┐                        │
│                    │           │                        │
│                 Retrofit     Room                       │
└────────────────────┼───────────┼────────────────────────┘
                     │           │
                     ▼           ▼
             Cloudflare API   Local Cache
                     │
                     ▼
               Cloudflare D1
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
     Branches    Technicians   Appointments
                                  │
                                  ▼
                           Technician Assignment
                                  │
                                  ▼
                              ASSIGNED
```

------------------------------------------------------------------------

# 38. The Core Goal of Member 3

The Admin module is not simply an Admin screen.

The real business flow is:

``` text
ADMIN
  ↓
UNDERSTANDS BUSINESS STATE
  ↓
SEES PENDING REPAIRS
  ↓
SEES AVAILABLE STAFF
  ↓
SELECTS THE CORRECT TECHNICIAN
  ↓
ASSIGNS TECHNICIAN
  ↓
MONITORS THE REPAIR
```

That is the core of **Branch + Technician Management**.

------------------------------------------------------------------------

# 39. Best First Task

Do not build every screen simultaneously.

Start with:

``` text
1. Admin login/session
        ↓
2. AdminApiService
        ↓
3. GET /api/technicians
        ↓
4. Technician model
        ↓
5. Technician RecyclerView
        ↓
6. Technician details
        ↓
7. Appointment list
        ↓
8. Assign Technician
        ↓
9. Dashboard
        ↓
10. Room cache
```

This uses the backend functionality you have already tested and
minimizes work against unverified endpoints.

------------------------------------------------------------------------

## Official Google Maps note

For the optional Admin branch map, use the current **Maps SDK for
Android** documentation. The current SDK supports Kotlin/Java, maps,
markers, camera movement, and interaction. Google requires a configured
API key and billing for Maps Platform usage, and recommends restricting
API keys before production.
