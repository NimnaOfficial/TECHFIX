# Member 2 - Investigation & Supervision Report

**Date & Time:** 2026-09-03
**Focus Area:** Customer Repair Journey (Booking, Appointments, History)

## ✅ 1. What is OK (Correct Implementations)
- **Authentication Security:** The implementation successfully avoids hardcoding JWTs or Customer IDs. `SessionManager` is correctly utilized to retrieve the authenticated token, which is passed dynamically to `ApiService` calls via the `Authorization: Bearer <TOKEN>` header.
- **End-to-End Core Logic:** The core flow for booking a repair, viewing appointments, and checking history is physically present in the Android UI (`RepairBookingFragment`, `BookingReviewFragment`, `MyAppointmentsFragment`, `RepairHistoryFragment`).
- **UI State Handling:** Loading, Error, and Success states are manually managed in the UI components (e.g., in `CustomerAppointmentDetailBottomSheet`, labels temporarily show "Loading..." and adapt upon API failure).

## ❌ 2. What are the Issues & Missing Features
- **Major Architecture Violation (No MVVM/Clean Architecture):** 
  Member 2 completely bypassed the required architecture. Network requests (`apiService.enqueue(...)`) are executed directly inside UI Fragments instead of delegating to a `ViewModel` and `Repository`.
- **Missing GPS/Location Data:** 
  In `BookingReviewFragment.java`, the `POST /api/appointments` payload (the `Map<String, Object> body`) entirely omits `customer_latitude` and `customer_longitude`. The guide explicitly required passing location coordinates when required.
- **Client-Side Joins Instead of Backend Joins:** 
  In `MyAppointmentsFragment.java`, Member 2 fetches 4 separate endpoints (`/api/appointments`, `/api/devices`, `/api/services`, `/api/branches`) and manually joins the data inside `updateLookupData()` for the adapter. The guide explicitly forbids this: *"The actual joins should be performed by the backend API. Android should receive API responses, not query D1 directly."*
- **Room Database Caching Not Used:** 
  An `AppointmentEntity.java` exists, but there is no `AppointmentDao` or Room integration for offline caching as requested in the guide.
- **Incorrect Package Structure:** 
  Files were placed flatly in `ui.booking` and `ui.history`. The guide specified they must be encapsulated within a `com.mad.techfix.ui.customer.*` parent package structure to clearly delineate member domains.
- **ApiService Comment Mismatches:** 
  Inside `ApiService.java`, the Customer Appointment endpoints are incorrectly commented under `// MEMBER 4`, while Technician endpoints are incorrectly attributed to `// MEMBER 2`.

## 🛠 3. How Should They Be Changed/Added
1. **Refactor to MVVM:**
   - Create `BookingViewModel`, `AppointmentViewModel`, and `RepairHistoryViewModel`.
   - Create an `AppointmentRepository` to abstract `RetrofitClient` calls away from the Fragments.
2. **Update Booking Payload:**
   - In `BookingReviewFragment`, extract GPS coordinates and append them to the request body:
     ```java
     body.put("customer_latitude", latitudeValue);
     body.put("customer_longitude", longitudeValue);
     ```
3. **Refactor Backend List Endpoint (worker.js):**
   - The Cloudflare worker's `GET /api/appointments` endpoint must be updated to use a `LEFT JOIN` for device, service, and branch names (similar to `GET /api/appointments/:id`). 
   - Once the backend is updated, remove the redundant `getDevices()`, `getServices()`, and `getBranches()` requests from `MyAppointmentsFragment`.
4. **Fix Package Structure:**
   - Move all booking, appointment, and history packages into `com/mad/techfix/ui/customer/`.
5. **Implement Local Caching:**
   - Implement `AppointmentDao` and hook it to the `Repository` so customer appointments are cached in SQLite for offline access.
