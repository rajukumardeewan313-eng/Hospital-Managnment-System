package hms.service;

import hms.billing.Bill;
import hms.billing.BillItem;
import hms.model.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central business-logic class.
 * COMPOSITION requirement: contains multiple ArrayList collections of domain objects.
 * All validation happens here — UI delegates to this class.
 */
public class HospitalSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    // OBJECT COMPOSITION — HospitalSystem contains collections of other objects
    private ArrayList<User>         users        = new ArrayList<>();
    private ArrayList<Patient>      patients     = new ArrayList<>();
    private ArrayList<Doctor>       doctors      = new ArrayList<>();
    private ArrayList<Appointment>  appointments = new ArrayList<>();
    private ArrayList<MedicalRecord> records     = new ArrayList<>();
    private ArrayList<Bill>         bills        = new ArrayList<>();

    // ─── USER OPERATIONS ──────────────────────────────────────────────────────
    
    public void addUser(User user) {
        if (findUserByUsername(user.getUsername()) != null)
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        users.add(user);
    }

    public User findUserByUsername(String username) {
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        return null;
    }

    public List<User> listUsers() { return new ArrayList<>(users); }

    // ─── PATIENT OPERATIONS ──────────────────────────────────────────────────

    public void addPatient(Patient p) {
        if (findPatientById(p.getId()) != null)
            throw new IllegalArgumentException("Patient ID already exists: " + p.getId());
        patients.add(p);
    }

    public void updatePatient(Patient updated) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(updated.getId())) {
                patients.set(i, updated);
                return;
            }
        }
        throw new IllegalArgumentException("Patient not found: " + updated.getId());
    }

    public Patient findPatientById(String id) {
        for (Patient p : patients)
            if (p.getId().equalsIgnoreCase(id)) return p;
        return null;
    }

    public List<Patient> searchPatientsByName(String name) {
        String lower = name.toLowerCase();
        return patients.stream()
                .filter(p -> p.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Patient> listPatients() { return new ArrayList<>(patients); }

    // ─── DOCTOR OPERATIONS ───────────────────────────────────────────────────

    public void addDoctor(Doctor d) {
        if (findDoctorById(d.getId()) != null)
            throw new IllegalArgumentException("Doctor ID already exists: " + d.getId());
        doctors.add(d);
    }

    public void updateDoctor(Doctor updated) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equals(updated.getId())) {
                doctors.set(i, updated);
                return;
            }
        }
        throw new IllegalArgumentException("Doctor not found: " + updated.getId());
    }

    public Doctor findDoctorById(String id) {
        for (Doctor d : doctors)
            if (d.getId().equalsIgnoreCase(id)) return d;
        return null;
    }

    public List<Doctor> searchDoctorsByName(String name) {
        String lower = name.toLowerCase();
        return doctors.stream()
                .filter(d -> d.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Doctor> listDoctors() { return new ArrayList<>(doctors); }

    // ─── APPOINTMENT OPERATIONS ──────────────────────────────────────────────

    public void bookAppointment(Appointment appt) {
        // Validate IDs exist
        if (findPatientById(appt.getPatientId()) == null)
            throw new IllegalArgumentException("Patient not found: " + appt.getPatientId());
        if (findDoctorById(appt.getDoctorId()) == null)
            throw new IllegalArgumentException("Doctor not found: " + appt.getDoctorId());

        // No duplicate appointment ID
        if (findAppointmentById(appt.getAppointmentId()) != null)
            throw new IllegalArgumentException("Appointment ID already exists: " + appt.getAppointmentId());

        // Cannot book in the past
        if (appt.getDateTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Appointment date/time cannot be in the past.");

        // Clash check: same doctor + same dateTime while BOOKED
        for (Appointment existing : appointments) {
            if (existing.getDoctorId().equalsIgnoreCase(appt.getDoctorId())
                    && existing.getDateTime().equals(appt.getDateTime())
                    && existing.getStatus() == Appointment.Status.BOOKED) {
                throw new IllegalArgumentException(
                        "Doctor already has a BOOKED appointment at " + appt.getDateTimeFormatted());
            }
        }

        appointments.add(appt);
    }

    public void cancelAppointment(String apptId) {
        Appointment a = getAppointmentOrThrow(apptId);
        if (a.getStatus() == Appointment.Status.CANCELLED)
            throw new IllegalStateException("Appointment is already cancelled.");
        if (a.getStatus() == Appointment.Status.COMPLETED)
            throw new IllegalStateException("Cannot cancel a completed appointment.");
        a.setStatus(Appointment.Status.CANCELLED);
    }

    public void completeAppointment(String apptId) {
        Appointment a = getAppointmentOrThrow(apptId);
        if (a.getStatus() == Appointment.Status.CANCELLED)
            throw new IllegalStateException("Cannot complete a cancelled appointment.");
        if (a.getStatus() == Appointment.Status.COMPLETED)
            throw new IllegalStateException("Appointment is already completed.");
        a.setStatus(Appointment.Status.COMPLETED);
    }

    public Appointment findAppointmentById(String id) {
        for (Appointment a : appointments)
            if (a.getAppointmentId().equalsIgnoreCase(id)) return a;
        return null;
    }

    public List<Appointment> listAppointments() { return new ArrayList<>(appointments); }

    public List<Appointment> listAppointmentsForDoctor(String doctorId) {
        return appointments.stream()
                .filter(a -> a.getDoctorId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
    }

    private Appointment getAppointmentOrThrow(String id) {
        Appointment a = findAppointmentById(id);
        if (a == null) throw new IllegalArgumentException("Appointment not found: " + id);
        return a;
    }

    // ─── MEDICAL RECORDS ─────────────────────────────────────────────────────

    public void addMedicalRecord(MedicalRecord record) {
        // Appointment must exist and be completed
        Appointment a = findAppointmentById(record.getAppointmentId());
        if (a == null)
            throw new IllegalArgumentException("Appointment not found: " + record.getAppointmentId());
        if (a.getStatus() != Appointment.Status.COMPLETED)
            throw new IllegalStateException("Medical records can only be added for COMPLETED appointments.");

        // No duplicate record for same appointment
        for (MedicalRecord r : records) {
            if (r.getAppointmentId().equalsIgnoreCase(record.getAppointmentId()))
                throw new IllegalArgumentException(
                        "A medical record already exists for appointment: " + record.getAppointmentId());
        }

        records.add(record);
    }

    public List<MedicalRecord> listRecordsByPatient(String patientId) {
        // Direct search by patientId stored in MedicalRecord
        return records.stream()
                .filter(r -> r.getPatientId() != null && r.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }

    public MedicalRecord findRecordByAppointment(String apptId) {
        for (MedicalRecord r : records)
            if (r.getAppointmentId().equalsIgnoreCase(apptId)) return r;
        return null;
    }

    public List<MedicalRecord> listAllRecords() { return new ArrayList<>(records); }

    // ─── BILLING ─────────────────────────────────────────────────────────────

    public Bill generateBillForAppointment(String billId, String apptId) {
        Appointment appt = findAppointmentById(apptId);
        if (appt == null)
            throw new IllegalArgumentException("Appointment not found: " + apptId);
        if (findBillById(billId) != null)
            throw new IllegalArgumentException("Bill ID already exists: " + billId);
        // Allow one bill per appointment
        for (Bill b : bills)
            if (b.getAppointmentId().equalsIgnoreCase(apptId))
                throw new IllegalArgumentException("A bill already exists for appointment: " + apptId);

        Bill bill = new Bill(billId, apptId, appt.getPatientId(), 0.0);
        bills.add(bill);
        return bill;
    }

    public void addBillItem(String billId, BillItem item) {
        Bill b = getBillOrThrow(billId);
        if (b.getStatus() == Bill.Status.PAID)
            throw new IllegalStateException("Cannot modify a PAID bill.");
        b.addItem(item);
    }

    public void markBillPaid(String billId) {
        Bill b = getBillOrThrow(billId);
        if (b.getStatus() == Bill.Status.PAID)
            throw new IllegalStateException("Bill is already marked as PAID.");
        b.setStatus(Bill.Status.PAID);
    }

    public Bill findBillById(String id) {
        for (Bill b : bills)
            if (b.getBillId().equalsIgnoreCase(id)) return b;
        return null;
    }

    public Bill findBillByAppointment(String apptId) {
        for (Bill b : bills)
            if (b.getAppointmentId().equalsIgnoreCase(apptId)) return b;
        return null;
    }

    public List<Bill> listBills() { return new ArrayList<>(bills); }

    public List<Bill> listUnpaidBills() {
        return bills.stream()
                .filter(b -> b.getStatus() == Bill.Status.UNPAID)
                .collect(Collectors.toList());
    }

    private Bill getBillOrThrow(String id) {
        Bill b = findBillById(id);
        if (b == null) throw new IllegalArgumentException("Bill not found: " + id);
        return b;
    }

    // ─── SAMPLE DATA ─────────────────────────────────────────────────────────

    /**
     * Loads sample data for demonstration purposes.
     */
    public void loadSampleData() {
        try {
            addPatient(new Patient("P001", "Alice Johnson", "555-1001", 34, "Female", "123 Main St"));
            addPatient(new Patient("P002", "Bob Smith",    "555-1002", 45, "Male",   "456 Oak Ave"));
            addPatient(new Patient("P003", "Carol White",  "555-1003", 28, "Female", "789 Pine Rd"));

            addDoctor(new Doctor("D001", "Dr. Emma Stone",   "555-2001", "Cardiology",   200.0));
            addDoctor(new Doctor("D002", "Dr. James Brown",  "555-2002", "Orthopedics",  180.0));
            addDoctor(new Doctor("D003", "Dr. Sarah Connor", "555-2003", "Dermatology",  150.0));
        } catch (Exception e) {
            // Ignore if data already exists
        }
    }
}
