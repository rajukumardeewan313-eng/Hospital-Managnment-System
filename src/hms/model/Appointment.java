package hms.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a scheduled appointment between a patient and doctor.
 */
public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { BOOKED, CANCELLED, COMPLETED }

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime dateTime;
    private Status status;
    private String notes; // optional booking notes

    public Appointment(String appointmentId, String patientId, String doctorId,
                       LocalDateTime dateTime) {
        setAppointmentId(appointmentId);
        setPatientId(patientId);
        setDoctorId(doctorId);
        setDateTime(dateTime);
        this.status = Status.BOOKED;
        this.notes = "";
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

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String did) {
        if (did == null || did.trim().isEmpty())
            throw new IllegalArgumentException("Doctor ID cannot be empty.");
        this.doctorId = did.trim();
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dt) {
        if (dt == null)
            throw new IllegalArgumentException("Date/time cannot be null.");
        this.dateTime = dt;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes == null ? "" : notes; }

    public String getDateTimeFormatted() {
        return dateTime.format(FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("Appt[%s] Patient:%s Doctor:%s @ %s [%s]",
                appointmentId, patientId, doctorId, getDateTimeFormatted(), status);
    }
}
