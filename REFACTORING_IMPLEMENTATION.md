# STEP 4: REFACTORED & NEW CODE SUMMARY

## NEW ARCHITECTURE IMPLEMENTED ✓

### 1. **Database Layer** (`hms.database`)

#### DatabaseConnection.java (Singleton Pattern)
- Manages JDBC connections (SQLite or MySQL)
- Single instance ensures connection pooling
- Supports both embedded SQLite (no server) and MySQL
- Configuration: Change `DB_TYPE` variable to switch databases
- Current: SQLite (data/hms.db) - no external DB server required

```
Key Methods:
- getInstance()              // Get singleton connection
- getConnection()            // Get active Connection
- isConnected()             // Check connection status
- reconnect()               // Reconnect if failed
```

#### DatabaseInitializer.java
- Creates all required database tables on first run
- Tables: users, patients, doctors, appointments, medical_records, prescription_items, bills, bill_items
- Called automatically in App.java startup
- Safe to call multiple times (uses CREATE TABLE IF NOT EXISTS)

### 2. **User & Authentication** (`hms.model` + `hms.service`)

#### User.java (New Model)
- Represents system users with roles (ADMIN, USER)
- Fields: userId, username, passwordHash, email, role, active
- Full input validation in setters
- Enum for Role with descriptions

#### AuthenticationService.java (New Service)
- Handles login, signup, password management
- Key Methods:
  - `login(username, password)` → User or null
  - `signup(username, password, email, role)` → User
  - `changePassword(oldPassword, newPassword)`
  - `getCurrentUser()`, `isLoggedIn()`, `isAdmin()`, `isUser()`
- Integrates with UserDAO for database persistence
- Automatic password hashing

### 3. **DAO Pattern** (`hms.dao`)

#### BaseDAO.java (Abstract)
- Base class for all DAO classes
- Provides getConnection() method

#### UserDAO.java
- CRUD operations for User entity
- Key Methods:
  - `createUser(user)`
  - `findById(userId)`, `findByUsername(username)`
  - `findAll()`, `updateUser(user)`, `deleteUser(userId)`
  - `usernameExists(username)`
  - `updatePassword(userId, newPassword)`
- **Uses PreparedStatement ONLY** - prevents SQL injection

#### PatientDAO.java
- Full CRUD for Patient entity
- Search by name (case-insensitive)
- Check existence before operations
- Mapping: ResultSet ↔ Patient objects

#### DoctorDAO.java
- Full CRUD for Doctor entity
- Search by name, specialization
- Sorted queries for consistent ordering

#### AppointmentDAO.java
- Full CRUD for Appointment entity
- Search by patient, doctor, or status
- DateTime mapping with SQL Timestamp
- Ordered by date DESC

### 4. **Utilities** (`hms.util`)

#### ValidationUtil.java
- Email validation (Regex)
- Phone number validation (7-15 chars)
- Numeric ID validation
- Name validation (letters + spaces only)
- Username validation (3-20 chars, alphanumeric + underscore)
- Password strength validation (8+ chars, uppercase, lowercase, digit)
- Field length checks
- Input sanitization against SQL injection
- All methods are static for easy use

#### PasswordUtil.java
- SHA-256 with salt password hashing
- `hashPassword(plainPassword)` → salted hash
- `verifyPassword(plain, hash)` → boolean
- Secure random salt generation
- Temporary password generation for reset functionality
- **Note**: Implement with BCrypt in production (add dependency)

#### ExceptionHandler.java
- Centralized exception handling
- User-friendly error messages via JOptionPane
- Methods:
  - `handleDatabaseException(parent, exception, context)`
  - `handleValidationError(parent, message)`
  - `handleAuthenticationError(parent, message)`
  - `showSuccessMessage()`, `showWarningMessage()`
  - `showConfirmDialog()`

#### PDFExporter.java
- Generate system reports as text files (or integrate iText for true PDF)
- Methods:
  - `exportPatientReport(patient, filePath)`
  - `exportBillingReport(bill, patient, filePath)`
  - `exportAppointmentReport(appointment, patient, doctor, filePath)`
  - `exportSystemReport(totalPatients, totalDoctors, totalAppointments, filePath)`
- Formatted output with proper headers and sections
- **To integrate iText**:
  ```
  <dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.3</version>
  </dependency>
  ```

### 5. **Enhanced UI** (`hms.ui`)

#### LoginFrame.java (NEW)
- Beautiful gradient login/signup interface
- Two panels: Login and Signup (CardLayout switch)
- Login Panel:
  - Username field
  - Password field (Enter key triggers login)
  - Login button
  - "Sign up" link
  - Demo credentials hint
- Signup Panel:
  - Username, Email, Password, Confirm Password
  - Role selector (ADMIN or USER)
  - Full validation before signup
  - "Back to login" link
- Error handling with descriptive messages
- Automatic user creation with proper role

#### MainFrame.java (REFACTORED)
- Now accepts `AuthenticationService` parameter
- Shows current logged-in user info in sidebar
- Logout button visible in sidebar
- Title shows user role
- Window closing triggers logout
- Removed serialization save/load (DB is now persistent)

#### App.java (REFACTORED v2.0)
- **Database initialization on startup**
  1. Initialize DatabaseConnection (Singleton)
  2. Create database schema (tables)
  3. Create demo admin/user if database empty
  4. Show LoginFrame for authentication
- Demo credentials built-in:
  - Admin: username=`admin`, password=`Admin@123`
  - User: username=`user`, password=`User@123`
- Prevents app launch until user authenticates
- Proper error handling with informative messages

## KEY IMPROVEMENTS ✓

| Feature | Before | After |
|---------|--------|-------|
| **Persistence** | Java Serialization | JDBC + Database |
| **Authentication** | None | Full login/signup system |
| **Authorization** | All users equal | Role-based (Admin/User) |
| **Validation** | Basic class-level | Regex-based, comprehensive |
| **SQL Safety** | N/A | PreparedStatement ONLY |
| **Error Messages** | Generic exceptions | User-friendly JOptionPane |
| **Password Security** | N/A | SHA-256 with salt hashing |
| **Code Structure** | Service only | Service + DAO + Util layers |
| **Database Queries** | N/A | DAO pattern with CRUD |
| **Input Sanitization** | None | Anti-SQL injection checks |

---

# STEP 5: NEW FEATURES IMPLEMENTATION

## 1. Authentication System ✓ COMPLETE

### Components
- User model with role support
- LoginFrame UI
- AuthenticationService with validation
- Password hashing with salt

### Usage Example
```java
UserDAO userDAO = new UserDAO();
AuthenticationService authService = new AuthenticationService(userDAO);

// Login
User user = authService.login("admin", "Admin@123");
if (user != null) {
    System.out.println("Welcome " + user.getUsername());
}

// Signup
User newUser = authService.signup("newuser", "Pass@1234", "user@email.com", User.Role.USER);

// Check current user
if (authService.isLoggedIn()) {
    System.out.println("Logged in as: " + authService.getCurrentUser().getUsername());
}
```

## 2. DAO Pattern ✓ COMPLETE

### Implemented DAOs
- `UserDAO` - User management
- `PatientDAO` - Patient CRUD
- `DoctorDAO` - Doctor CRUD
- `AppointmentDAO` - Appointment CRUD
- *(Additional DAOs for MedicalRecord, Bill can follow same pattern)*

### Usage Pattern
```java
// Create
PatientDAO patientDAO = new PatientDAO();
Patient patient = new Patient("P001", "John Doe", "555-1234", 30, "Male", "123 Main St");
patientDAO.createPatient(patient);

// Read
Patient found = patientDAO.findById("P001");

// Update
patient.setAddress("456 Oak Ave");
patientDAO.updatePatient(patient);

// Delete
patientDAO.deletePatient("P001");

// Search
List<Patient> results = patientDAO.searchByName("John");
```

## 3. Validation System ✓ COMPLETE

### Regex Patterns
- **Email**: Standard email format validation
- **Phone**: 7-15 characters with allowed symbols
- **ID**: Numeric only
- **Name**: Letters and spaces only
- **Username**: 3-20 chars (alphanumeric + underscore)
- **Password**: 8+ chars with uppercase, lowercase, digit

### Usage
```java
if (!ValidationUtil.isValidEmail(emailInput)) {
    ExceptionHandler.handleValidationError(this, "Invalid email format");
}

if (!ValidationUtil.isValidPhone(phoneInput)) {
    ExceptionHandler.handleValidationError(this, "Invalid phone number");
}

if (!ValidationUtil.isStrongPassword(password)) {
    ExceptionHandler.handleValidationError(this, "Password too weak");
}
```

## 4. Error Handling ✓ COMPLETE

### ExceptionHandler Methods
```java
// Database errors
ExceptionHandler.handleDatabaseException(parent, e, "loading patients");

// Validation errors
ExceptionHandler.handleValidationError(parent, "Email is invalid");

// Authentication errors
ExceptionHandler.handleAuthenticationError(parent, "Invalid username or password");

// Success messages
ExceptionHandler.showSuccessMessage(parent, "Patient added successfully!");

// Warnings
ExceptionHandler.showWarningMessage(parent, "No appointments found");

// Confirmations
int result = ExceptionHandler.showConfirmDialog(parent, "Delete this patient?", "Confirm");
```

## 5. PDF/Report Export ✓ COMPLETE

### Supported Reports
- Patient report
- Billing report
- Appointment report
- System statistics report

### Usage
```java
// Export patient report
Patient patient = new Patient(...);
PDFExporter.exportPatientReport(patient, "reports/patient_P001.txt");

// Export bill report
Bill bill = new Bill(...);
PDFExporter.exportBillingReport(bill, patient, "reports/bill_B001.txt");

// Export system stats
PDFExporter.exportSystemReport(50, 20, 150, "reports/system_stats.txt");
```

**Integration with iText for true PDF:**
Add dependency and replace FileOutputStream with PDF writer classes.

## 6. Role-Based Access Control ✓ STARTED

### Implemented
- User roles: ADMIN and USER
- LoginFrame allows role selection during signup
- MainFrame displays user role in sidebar
- AuthenticationService provides `isAdmin()` and `isUser()` methods

### To Extend
In each UI panel, add checks:
```java
if (authService.isAdmin()) {
    // Show admin-only buttons
    deleteButton.setVisible(true);
} else {
    // Regular user - limited access
    deleteButton.setVisible(false);
}
```

---

## FILES CREATED (14 Total)

| File | Purpose |
|------|---------|
| `hms/database/DatabaseConnection.java` | JDBC connection singleton |
| `hms/database/DatabaseInitializer.java` | Schema creation |
| `hms/model/User.java` | User entity |
| `hms/dao/BaseDAO.java` | Abstract DAO base |
| `hms/dao/UserDAO.java` | User CRUD |
| `hms/dao/PatientDAO.java` | Patient CRUD |
| `hms/dao/DoctorDAO.java` | Doctor CRUD |
| `hms/dao/AppointmentDAO.java` | Appointment CRUD |
| `hms/service/AuthenticationService.java` | Login/Signup service |
| `hms/util/ValidationUtil.java` | Input validation |
| `hms/util/PasswordUtil.java` | Password hashing |
| `hms/util/ExceptionHandler.java` | Error handling |
| `hms/util/PDFExporter.java` | Report generation |
| `hms/ui/LoginFrame.java` | Authentication UI |

## FILES MODIFIED (2 Total)

| File | Changes |
|------|---------|
| `App.java` | JDBC init, auth flow, demo user creation |
| `hms/ui/MainFrame.java` | Auth support, user info display, logout |

---
