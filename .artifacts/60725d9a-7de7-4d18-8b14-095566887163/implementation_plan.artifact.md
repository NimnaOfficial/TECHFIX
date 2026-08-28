# Implementation Plan - Fix Technician Form Crash and Deletion Logic

The app currently crashes when opening the technician form due to a theme inflation error. Additionally, technician deletion is currently "soft" (flagged as inactive) in the backend and out-of-sync in the local cache.

## Proposed Changes

### [Component] UI Framework (Crash Fix)

#### [MODIFY] [themes.xml](file:///C:/Users/SANDANIMNE/Desktop/MyCodes/MAD/TECHFIX/app/src/main/res/values/themes.xml)
- Fix `Widget.App.TextInputLayout.OutlinedBox.ExposedDropdownMenu.WhitePopup` and its `ThemeOverlay` to properly inherit from Material 3.
- This resolves the `InflateException` by providing correct theme attribute resolution.

#### [MODIFY] [dialog_technician_form.xml](file:///C:/Users/SANDANIMNE/Desktop/MyCodes/MAD/TECHFIX/app/src/main/res/layout/dialog_technician_form.xml)
- Replace `AutoCompleteTextView` with `com.google.android.material.textfield.MaterialAutoCompleteTextView`.
- Remove manual `android:padding` and `android:inputType` which can conflict with the exposed dropdown menu style.

---

### [Component] Backend (Hard Delete)

#### [MODIFY] [worker.js](file:///C:/Users/SANDANIMNE/Desktop/MyCodes/MAD/TECHFIX/mem3/worker.js)
- Change the `DELETE` handler for technicians to perform a `DELETE FROM technicians` instead of `UPDATE technicians SET is_active = 0`.
- *Note*: Associated data in `technician_services` and `appointments` (where it might be nullable) will be handled to avoid foreign key constraints if necessary (or the user can decide if they want cascade delete).

---

### [Component] Data Layer (Sync Fix)

#### [MODIFY] [AdminDao.java](file:///C:/Users/SANDANIMNE/Desktop/MyCodes/MAD/TECHFIX/app/src/main/java/com/mad/techfix/data/local/database/AdminDao.java)
- [NEW] Add `@Query("DELETE FROM local_technicians WHERE id = :id") void deleteTechnicianById(String id);`.

#### [MODIFY] [AdminRepository.java](file:///C:/Users/SANDANIMNE/Desktop/MyCodes/MAD/TECHFIX/app/src/main/java/com/mad/techfix/repository/AdminRepository.java)
- Update `deleteTechnician` to call `adminDao.deleteTechnicianById(technicianId)` inside the success callback.

## Verification Plan

### Automated Tests
- Build the app to ensure XML inflation errors are gone.

### Manual Verification
- **Test Crash**: Navigate to Technician Roster and click the "+" button. The form should open without crashing.
- **Test Delete**: Delete a technician. Verify it's gone from the UI immediately and check (via App Inspection or Backend) that the record is truly removed.
