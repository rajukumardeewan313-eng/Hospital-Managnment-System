package hms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITION: MedicalRecord contains ArrayList<PrescriptionItem>.
 * Linked to a completed Appointment.
 */
public class MedicalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String appointmentId;
    private String patientId;
    private String diagnosis;
    private String notes;

    // OBJECT COMPOSITION — contains a collection of PrescriptionItem objects
    private ArrayList<PrescriptionItem> prescriptions;

    public MedicalRecord(String recordId, String appointmentId, String patientId, String diagnosis, String notes) {
        setRecordId(recordId);
        setAppointmentId(appointmentId);
        setPatientId(patientId);
        setDiagnosis(diagnosis);
        setNotes(notes);
        this.prescriptions = new ArrayList<>();
    }

    public void addPrescription(PrescriptionItem item) {
        if (item == null) throw new IllegalArgumentException("Prescription item cannot be null.");
        prescriptions.add(item);
    }

    public List<PrescriptionItem> getPrescriptions() {
        return new ArrayList<>(prescriptions); // defensive copy
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Record ID cannot be empty.");
        this.recordId = id.trim();
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Appointment ID cannot be empty.");
        this.appointmentId = id.trim();
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String pid) {
        if (pid == null || pid.trim().isEmpty())
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        this.patientId = pid.trim();
    }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) {
        if (diagnosis == null || diagnosis.trim().isEmpty())
            throw new IllegalArgumentException("Diagnosis cannot be empty.");
        this.diagnosis = diagnosis.trim();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes == null ? "" : notes.trim(); }

    @Override
    public String toString() {
        return String.format("Record[%s] Appt:%s Diagnosis: %s", recordId, appointmentId, diagnosis);
    }
}
