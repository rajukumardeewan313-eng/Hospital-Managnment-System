# Hospital Management System (HMS)

A Java Swing desktop application demonstrating all four OOP pillars with
a vibrant, colourful GUI and full data persistence.

---

## How to Compile & Run

### Prerequisites
- JDK 8 or JDK 17+
- No external libraries required

### Compile (from project root)
```bash
# Windows
javac -d out -sourcepath src src/App.java src/hms/model/*.java src/hms/billing/*.java src/hms/service/*.java src/hms/persistence/*.java src/hms/ui/*.java

# Linux / macOS
javac -d out -sourcepath src $(find src -name "*.java")
```

### Run
```bash
java -cp out App
```

Data is saved to `data/hms.dat` (created automatically).

---

## Features

| Panel          | What you can do                                                   |
|----------------|-------------------------------------------------------------------|
| Dashboard      | System-wide stats, unpaid bills report, doctor schedule lookup   |
| Patients       | Add / Update / Search (by ID or name) / List patients            |
| Doctors        | Add / Update / Search / List doctors                             |
| Appointments   | Book (clash-prevented) / Cancel / Complete + status filter       |
| Medical Records| Add diagnosis + multiple prescription items per appointment      |
| Billing        | Generate bill / Add consultation, lab, medicine items / Mark paid|

---

## OOP Pillars — Where They Are

### 1. ENCAPSULATION
- `Person`, `Patient`, `Doctor`, `Appointment`, `MedicalRecord`, `Bill`,
  `PrescriptionItem` — all fields are **private** with getters/setters.
- Setters validate input (empty strings, negative numbers, etc.) and throw
  `IllegalArgumentException` before setting the field.

### 2. ABSTRACTION
- `abstract class Person` (hms/model/Person.java) declares `abstract displayInfo()`.
- `interface BillItem` (hms/billing/BillItem.java) declares `getCost()` and
  `getDescription()` — callers never need to know the concrete type.

### 3. INHERITANCE
- `Patient extends Person` — inherits id, name, phone; adds age, gender, address.
- `Doctor  extends Person` — inherits id, name, phone; adds specialization, fee.

### 4. POLYMORPHISM
- `ConsultationFeeItem`, `LabTestItem`, `MedicineChargeItem` all implement `BillItem`.
- `Bill.getTotalAmount()` iterates `ArrayList<BillItem>` and calls `getCost()`
  on each — the JVM dispatches to the correct implementation at runtime.
- `displayInfo()` is overridden differently in `Patient` and `Doctor`.

---

## Object Composition

`HospitalSystem` contains:
```
ArrayList<Patient>       patients
ArrayList<Doctor>        doctors
ArrayList<Appointment>   appointments
ArrayList<MedicalRecord> records
ArrayList<Bill>          bills
```
`MedicalRecord` contains `ArrayList<PrescriptionItem>`.  
`Bill`          contains `ArrayList<BillItem>`.

---

## File I/O / Persistence

- **Class**: `hms/persistence/DataStore.java`
- **Mechanism**: Java Serialization (`ObjectOutputStream` / `ObjectInputStream`)
- **File**: `data/hms.dat`
- **When**: "Save Data" sidebar button + on window close (prompted)
- **On startup**: if `hms.dat` exists, user is asked whether to reload it.
- `IOException` and `ClassNotFoundException` are caught and shown via `JOptionPane`.

---

## Exception Handling

| Location                          | What is caught / validated                               |
|-----------------------------------|----------------------------------------------------------|
| All model setters                 | `IllegalArgumentException` for empty/invalid values     |
| `UIUtils.parseInt / parseDouble`  | `NumberFormatException` → friendly message              |
| `HospitalSystem.bookAppointment`  | Duplicate ID, past date, doctor clash, missing patient  |
| `HospitalSystem.addMedicalRecord` | Appointment not completed, duplicate record             |
| `DataStore.save / load`           | `IOException`, `ClassNotFoundException`                 |
| `AppointmentPanel`                | `DateTimeParseException` for bad date formats           |
| All UI action handlers            | Wrapped in try-catch → `UIUtils.showError` (never crash)|

---

## Test Cases

| Test                              | Expected Result                                               |
|-----------------------------------|---------------------------------------------------------------|
| Add patient with empty ID         | Error dialog: "Patient ID cannot be empty."                   |
| Add patient, age = -5             | Error dialog: "Age must be between 1 and 150."               |
| Add patient with duplicate ID     | Error dialog: "Patient ID already exists: P001"              |
| Add doctor with negative fee      | Error dialog: "Consultation fee cannot be negative."         |
| Book appointment in the past      | Error dialog: "Appointment date/time cannot be in the past." |
| Book same doctor + time twice     | Error dialog: "Doctor already has a BOOKED appointment at …" |
| Book with non-existent patient    | Error dialog: "Patient not found: …"                         |
| Invalid date format in Appt panel | Error dialog: "Invalid date format. Use: yyyy-MM-dd HH:mm"  |
| Add medical record to BOOKED appt | Error dialog: "Medical records can only be added for COMPLETED appointments." |
| Add item to PAID bill             | Error dialog: "Cannot modify a PAID bill."                   |
| Save → close → reopen → load      | All previously entered data is restored from hms.dat         |
| Non-numeric age field             | Error dialog: "Age must be a whole number."                  |

---

## Package Structure

```
src/
├── App.java                          ← main() entry point
└── hms/
    ├── model/
    │   ├── Person.java               ← abstract base (Abstraction + Inheritance)
    │   ├── Patient.java              ← extends Person
    │   ├── Doctor.java               ← extends Person
    │   ├── Appointment.java          ← booking entity with Status enum
    │   ├── MedicalRecord.java        ← has ArrayList<PrescriptionItem> (Composition)
    │   └── PrescriptionItem.java     ← single medicine entry
    ├── billing/
    │   ├── BillItem.java             ← interface (Abstraction)
    │   ├── BillItemImpls.java        ← ConsultationFeeItem, LabTestItem, MedicineChargeItem (Polymorphism)
    │   ├── BillItemFactory.java      ← factory for creating BillItem instances
    │   └── Bill.java                 ← has ArrayList<BillItem> (Composition + Polymorphism)
    ├── service/
    │   └── HospitalSystem.java       ← all business logic + validation (MVC Controller)
    ├── persistence/
    │   └── DataStore.java            ← File I/O via Java Serialization
    └── ui/
        ├── UIUtils.java              ← shared Swing helpers + colour theme
        ├── MainFrame.java            ← JFrame + sidebar navigation
        ├── PatientPanel.java         ← teal theme
        ├── DoctorPanel.java          ← indigo theme
        ├── AppointmentPanel.java     ← amber theme
        ├── MedicalRecordPanel.java   ← purple/green theme
        ├── BillingPanel.java         ← coral/green theme
        └── ReportsPanel.java         ← dark navy theme + stat cards
```
