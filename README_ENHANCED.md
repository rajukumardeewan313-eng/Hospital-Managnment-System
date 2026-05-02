# Hospital Management System (HMS) - Enhanced v2.0

## Overview

A professional Hospital Management System built with **Java Swing** and **JDBC**, featuring:
- ✅ Authentication & Authorization (Role-based access)
- ✅ Complete CRUD operations with database persistence
- ✅ Advanced search & filtering
- ✅ Comprehensive data validation
- ✅ Professional error handling
- ✅ Report generation capability
- ✅ Modern UI with dark theme

---

## Quick Start

### 1. Prerequisites
- JDK 8 or higher
- SQLite JDBC driver (included in build process)

### 2. Compile & Run

**Windows:**
```bash
# Compile
javac -d out -sourcepath src src/App.java src/hms/**/*.java

# Run
java -cp out App
```

**Linux/macOS:**
```bash
# Compile
javac -d out -sourcepath src $(find src -name "*.java")

# Run
java -cp out App
```

### 3. Login

**Demo Credentials:**
- Admin: `admin` / `Admin@123`
- User: `user` / `User@123`

Or create your own account via signup.

---

## Features

### 🔐 Authentication & Authorization
- **Login/Signup System**: Create accounts with email validation
- **Role-Based Access**: Admin and User roles
- **Password Security**: SHA-256 hashing with salt
- **Session Management**: Current user tracking

### 📊 Dashboard
- System statistics (patients, doctors, appointments)
- Unpaid bills report
- Doctor schedule lookup

### 👥 Patient Management
- **CRUD Operations**: Add, view, update, delete patients
- **Search**: Find patients by name (case-insensitive)
- **Validation**: Age (1-150), gender, phone, address
- **Data**: ID, Name, Age, Gender, Phone, Address

### 👨‍⚕️ Doctor Management
- **CRUD Operations**: Complete doctor management
- **Search**: By name or specialization
- **Specializations**: Filter doctors by specialty
- **Consultation Fees**: Track and manage fees

### 📅 Appointment Management
- **Booking**: Create appointments between patients and doctors
- **Clash Prevention**: Avoid double-booking
- **Status Tracking**: BOOKED, CANCELLED, COMPLETED
- **Notes**: Add appointment notes/comments

### 📋 Medical Records
- **Diagnosis**: Record medical diagnoses
- **Prescriptions**: Add multiple prescription items
- **Dosage**: Track medicine dosages and duration
- **Linked Data**: Connected to appointments and patients

### 💳 Billing System
- **Generate Bills**: Create bills for consultations/services
- **Bill Items**: Add consultation, lab, and medicine items
- **Payment Tracking**: Mark bills as paid
- **Reports**: Generate billing reports

---

## Architecture

### Project Structure
```
src/
├── App.java                          # Entry point
└── hms/
    ├── database/                     # Database layer
    │   ├── DatabaseConnection.java   # JDBC singleton
    │   └── DatabaseInitializer.java  # Schema creation
    ├── model/                        # Domain models
    │   ├── User.java                 # User entity (NEW)
    │   ├── Patient.java
    │   ├── Doctor.java
    │   ├── Appointment.java
    │   ├── MedicalRecord.java
    │   ├── Bill.java
    │   └── ...
    ├── dao/                          # Data access layer
    │   ├── BaseDAO.java              # Abstract base
    │   ├── UserDAO.java              # User CRUD (NEW)
    │   ├── PatientDAO.java           # Patient CRUD
    │   ├── DoctorDAO.java            # Doctor CRUD
    │   ├── AppointmentDAO.java       # Appointment CRUD
    │   ├── MedicalRecordDAO.java     # Medical record CRUD (NEW)
    │   └── BillDAO.java              # Bill CRUD (NEW)
    ├── service/                      # Business logic
    │   ├── HospitalSystem.java       # Core logic
    │   └── AuthenticationService.java # Auth logic (NEW)
    ├── util/                         # Utilities (NEW)
    │   ├── ValidationUtil.java       # Input validation
    │   ├── PasswordUtil.java         # Password hashing
    │   ├── ExceptionHandler.java     # Error handling
    │   └── PDFExporter.java          # Report generation
    └── ui/                           # User interface
        ├── LoginFrame.java           # Login/Signup (NEW)
        ├── MainFrame.java            # Main window (enhanced)
        ├── PatientPanel.java
        ├── DoctorPanel.java
        ├── AppointmentPanel.java
        ├── MedicalRecordPanel.java
        ├── BillingPanel.java
        ├── ReportsPanel.java
        └── UIUtils.java
```

### Design Patterns
- **Singleton**: DatabaseConnection
- **DAO**: Data access abstraction (6 DAOs)
- **MVC**: Model-View separation
- **Service Layer**: Business logic abstraction

---

## Data Validation

### Implemented Validations

| Field | Validation | Example |
|-------|-----------|---------|
| **Email** | Regex pattern | user@domain.com ✓ |
| **Phone** | 7-15 digits + symbols | 555-1234 ✓ |
| **ID** | Numeric only | P001 ✓, P-001 ✗ |
| **Name** | Letters + spaces | John Doe ✓, John@123 ✗ |
| **Age** | 1-150 range | 30 ✓, 200 ✗ |
| **Username** | 3-20 chars, alphanumeric + underscore | john_doe ✓ |
| **Password** | 8+ chars (upper, lower, digit) | Pass@123 ✓ |

### Validation Methods (ValidationUtil)
```java
ValidationUtil.isValidEmail(email)
ValidationUtil.isValidPhone(phone)
ValidationUtil.isValidNumericId(id)
ValidationUtil.isValidName(name)
ValidationUtil.isValidUsername(username)
ValidationUtil.isStrongPassword(password)
ValidationUtil.sanitizeInput(input)  // Anti-SQL injection
```

---

## Database

### Technology
- **Primary**: SQLite (embedded, no server)
- **Alternative**: MySQL (requires server)
- **Connection**: JDBC with PreparedStatement (SQL injection safe)

### Database Configuration

**Edit `DatabaseConnection.java`:**
```java
// Line 23-24
private static final String DB_TYPE = "sqlite";  // or "mysql"

// For MySQL, also update:
private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/hms_db";
private static final String MYSQL_USER = "root";
private static final String MYSQL_PASSWORD = "";
```

### Tables

| Table | Purpose | Key Fields |
|-------|---------|-----------|
| **users** | System users | user_id, username, password_hash, role |
| **patients** | Patient records | patient_id, name, age, gender, phone, address |
| **doctors** | Doctor records | doctor_id, name, specialization, consultation_fee |
| **appointments** | Appointments | appointment_id, patient_id, doctor_id, status |
| **medical_records** | Medical history | record_id, appointment_id, diagnosis |
| **prescription_items** | Medications | item_id, record_id, medicine_name, dosage |
| **bills** | Billing records | bill_id, patient_id, total_amount, is_paid |
| **bill_items** | Bill details | item_id, bill_id, description, amount |

### Security Features
- **PreparedStatement**: All queries use prepared statements (no SQL injection)
- **Password Hashing**: SHA-256 with salt
- **Input Sanitization**: Anti-SQL injection validation
- **Foreign Keys**: Referential integrity enforcement

---

## API Reference

### Authentication Service

```java
AuthenticationService authService = new AuthenticationService(userDAO);

// Login
User user = authService.login("admin", "Admin@123");
if (user != null) {
    System.out.println("Welcome " + user.getUsername());
}

// Signup
User newUser = authService.signup("john", "Pass@1234", 
                                   "john@email.com", User.Role.USER);

// Check current user
if (authService.isLoggedIn()) {
    System.out.println("Logged in as: " + 
                      authService.getCurrentUser().getUsername());
}

// Check role
if (authService.isAdmin()) {
    // Show admin features
}

// Logout
authService.logout();
```

### Patient DAO

```java
PatientDAO patientDAO = new PatientDAO();

// Create
Patient p = new Patient("P001", "John Doe", "555-1234", 30, "M", "123 St");
patientDAO.createPatient(p);

// Read
Patient found = patientDAO.findById("P001");

// Update
p.setAge(31);
patientDAO.updatePatient(p);

// Search
List<Patient> results = patientDAO.searchByName("John");

// List all
List<Patient> all = patientDAO.findAll();

// Delete
patientDAO.deletePatient("P001");
```

### Validation

```java
// Email validation
if (!ValidationUtil.isValidEmail(email)) {
    ExceptionHandler.handleValidationError(parent, "Invalid email");
}

// Phone validation
if (!ValidationUtil.isValidPhone(phone)) {
    ExceptionHandler.showWarningMessage(parent, "Invalid phone format");
}

// Strong password
if (!ValidationUtil.isStrongPassword(pwd)) {
    ExceptionHandler.handleValidationError(parent, 
        "Password must have uppercase, lowercase, and number");
}
```

### Report Generation

```java
// Export patient report
Patient p = patientDAO.findById("P001");
PDFExporter.exportPatientReport(p, "reports/patient_P001.txt");

// Export billing report
Bill b = billDAO.findById("B001");
PDFExporter.exportBillingReport(b, patient, "reports/bill_B001.txt");

// Export system statistics
PDFExporter.exportSystemReport(50, 20, 150, "reports/stats.txt");
```

---

## Error Handling

### Exception Handler Methods

```java
try {
    // Database operation
    patientDAO.createPatient(patient);
    ExceptionHandler.showSuccessMessage(this, "Patient added!");
} catch (SQLException e) {
    ExceptionHandler.handleDatabaseException(this, e, "adding patient");
} catch (IllegalArgumentException e) {
    ExceptionHandler.handleValidationError(this, e.getMessage());
}
```

### Dialog Types
- `handleDatabaseException()` - Database errors
- `handleValidationError()` - Input validation
- `handleAuthenticationError()` - Login failures
- `showSuccessMessage()` - Success confirmation
- `showWarningMessage()` - Warnings
- `showConfirmDialog()` - Yes/No confirmation

---

## Sample Data

Click **"📦 Load Samples"** button in the app to load:
- 10 sample patients
- 5 sample doctors
- 10 sample appointments

Sample data is skipped if entities already exist.

---

## Troubleshooting

### Database Issues

**Problem**: "SQLite driver not found"
- **Solution**: Ensure SQLite JDBC is in classpath
  ```bash
  # Add to compilation
  javac -cp "libs/sqlite-jdbc-3.44.0.0.jar" ...
  ```

**Problem**: "Users table doesn't exist"
- **Solution**: Run app - `DatabaseInitializer` runs automatically on startup
  - Or manually call: `DatabaseInitializer.initializeDatabase();`

**Problem**: "Cannot connect to MySQL"
- **Solution**: 
  1. Verify MySQL running: `mysql -u root -p`
  2. Create database: `CREATE DATABASE hms_db;`
  3. Update `DatabaseConnection.java` credentials
  4. Set `DB_TYPE = "mysql"`

### Authentication Issues

**Problem**: "Login fails with correct credentials"
- **Solution**: Password hashing issue
  1. Check password format: 8+ chars with upper, lower, digit
  2. Verify `PasswordUtil.verifyPassword()` in AuthenticationService
  3. Try demo account: admin/Admin@123

**Problem**: "Signup fails"
- **Solution**: Check validation rules
  - Username: 3-20 chars
  - Email: valid format
  - Password: 8+ chars with complexity
  - No duplicate username

### Compilation Issues

**Problem**: "Class not found: java.sql.PreparedStatement"
- **Solution**: Add import:
  ```java
  import java.sql.PreparedStatement;
  ```

**Problem**: "Cannot find symbol: DatabaseConnection"
- **Solution**: Verify import path:
  ```java
  import hms.database.DatabaseConnection;
  ```

---

## Production Deployment

### Before Deployment Checklist

- [ ] Change demo password (admin/Admin@123)
- [ ] Configure production database
- [ ] Enable SSL for database connection
- [ ] Implement proper user logging
- [ ] Run security audit
- [ ] Test with production data volume
- [ ] Set up automated backups
- [ ] Document deployment process
- [ ] Train users
- [ ] Create disaster recovery plan

### Security Hardening

1. **Update PasswordUtil**
   - Implement BCrypt instead of SHA-256
   - Add: `org.mindrot:jbcrypt:0.4`

2. **Configure Database**
   - Use MySQL with SSL
   - Create read-only user for queries
   - Enable audit logging

3. **Application Security**
   - HTTPS support
   - Session timeout
   - Rate limiting on login
   - Encrypted configuration

---

## Development Guide

### Adding New Entity

1. **Create Model Class** (hms/model/Entity.java)
   ```java
   public class Entity implements Serializable {
       private String id;
       private String name;
       // Getters/setters with validation
   }
   ```

2. **Create DAO Class** (hms/dao/EntityDAO.java)
   ```java
   public class EntityDAO extends BaseDAO {
       public void createEntity(Entity entity) throws SQLException {
           // INSERT query using PreparedStatement
       }
       // Other CRUD methods...
   }
   ```

3. **Create UI Panel** (hms/ui/EntityPanel.java)
   ```java
   public class EntityPanel extends JPanel {
       // Add table, search, CRUD buttons
   }
   ```

4. **Register in MainFrame**
   ```java
   contentArea.add(new EntityPanel(system), "Entities");
   ```

### Testing Checklist

- [ ] Unit test each DAO method
- [ ] Integration test DB operations
- [ ] Test all validation rules
- [ ] Test authentication flows
- [ ] Test error handling
- [ ] Performance test with large datasets
- [ ] UI responsiveness test

---

## License & Credits

**Project**: Hospital Management System (HMS) v2.0
**Author**: HMS Development Team
**Version**: 2.0 (Enhanced with JDBC, Authentication, DAO Pattern)
**Java Version**: JDK 8+

---

## Contact & Support

For issues, questions, or contributions:
1. Check troubleshooting section
2. Review IMPLEMENTATION_GUIDE.md
3. Check REFACTORING_IMPLEMENTATION.md

---
