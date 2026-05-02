package hms.dao;

import hms.model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Appointment entity.
 * Handles all database operations for appointments.
 * USES PREPAREDSTATEMENT ONLY - prevents SQL injection.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class AppointmentDAO extends BaseDAO {

    /**
     * Creates a new appointment in the database.
     * 
     * @param appointment the Appointment object to create
     * @throws SQLException if database operation fails
     */
    public void createAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, status, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, appointment.getAppointmentId());
            pstmt.setString(2, appointment.getPatientId());
            pstmt.setString(3, appointment.getDoctorId());
            pstmt.setTimestamp(4, Timestamp.valueOf(appointment.getDateTime()));
            pstmt.setString(5, appointment.getStatus().name());
            pstmt.setString(6, appointment.getNotes());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Finds an appointment by ID.
     * 
     * @param appointmentId the appointment ID to search for
     * @return Appointment object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Appointment findById(String appointmentId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all appointments from the database.
     * 
     * @return List of all Appointment objects
     * @throws SQLException if database operation fails
     */
    public List<Appointment> findAll() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    /**
     * Finds all appointments for a specific patient.
     * 
     * @param patientId the patient ID
     * @return List of matching Appointment objects
     * @throws SQLException if database operation fails
     */
    public List<Appointment> findByPatientId(String patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    /**
     * Finds all appointments for a specific doctor.
     * 
     * @param doctorId the doctor ID
     * @return List of matching Appointment objects
     * @throws SQLException if database operation fails
     */
    public List<Appointment> findByDoctorId(String doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_date DESC";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    /**
     * Finds appointments by status.
     * 
     * @param status the appointment status (BOOKED, CANCELLED, COMPLETED)
     * @return List of matching Appointment objects
     * @throws SQLException if database operation fails
     */
    public List<Appointment> findByStatus(Appointment.Status status) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_date DESC";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        }
        return appointments;
    }

    /**
     * Updates an existing appointment in the database.
     * 
     * @param appointment the Appointment object with updated values
     * @throws SQLException if database operation fails
     */
    public void updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ?, status = ?, notes = ? " +
                    "WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));
            pstmt.setString(4, appointment.getStatus().name());
            pstmt.setString(5, appointment.getNotes());
            pstmt.setString(6, appointment.getAppointmentId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes an appointment from the database.
     * 
     * @param appointmentId the appointment ID to delete
     * @throws SQLException if database operation fails
     */
    public void deleteAppointment(String appointmentId) throws SQLException {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, appointmentId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if an appointment ID already exists in the database.
     * 
     * @param appointmentId the appointment ID to check
     * @return true if exists, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean appointmentIdExists(String appointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to an Appointment object.
     * 
     * @param rs the ResultSet to map from
     * @return Appointment object
     * @throws SQLException if mapping fails
     */
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        String appointmentId = rs.getString("appointment_id");
        String patientId = rs.getString("patient_id");
        String doctorId = rs.getString("doctor_id");
        LocalDateTime dateTime = rs.getTimestamp("appointment_date").toLocalDateTime();
        String statusString = rs.getString("status");
        String notes = rs.getString("notes");

        Appointment appointment = new Appointment(appointmentId, patientId, doctorId, dateTime);
        appointment.setStatus(Appointment.Status.valueOf(statusString));
        appointment.setNotes(notes != null ? notes : "");

        return appointment;
    }
}
