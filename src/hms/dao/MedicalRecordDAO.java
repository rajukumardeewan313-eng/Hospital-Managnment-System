package hms.dao;

import hms.model.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for MedicalRecord entity.
 * Handles all database operations for medical records.
 * USES PREPAREDSTATEMENT ONLY - prevents SQL injection.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class MedicalRecordDAO extends BaseDAO {

    /**
     * Creates a new medical record in the database.
     * 
     * @param record the MedicalRecord object to create
     * @throws SQLException if database operation fails
     */
    public void createMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "INSERT INTO medical_records (record_id, appointment_id, patient_id, diagnosis) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, record.getRecordId());
            pstmt.setString(2, record.getAppointmentId());
            pstmt.setString(3, record.getPatientId());
            pstmt.setString(4, record.getDiagnosis());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Finds a medical record by ID.
     * 
     * @param recordId the record ID to search for
     * @return MedicalRecord object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public MedicalRecord findById(String recordId) throws SQLException {
        String sql = "SELECT * FROM medical_records WHERE record_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, recordId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicalRecord(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds all medical records for a specific patient.
     * 
     * @param patientId the patient ID
     * @return List of MedicalRecord objects
     * @throws SQLException if database operation fails
     */
    public List<MedicalRecord> findByPatientId(String patientId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM medical_records WHERE patient_id = ? ORDER BY record_id DESC";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToMedicalRecord(rs));
                }
            }
        }
        return records;
    }

    /**
     * Finds a medical record by appointment ID.
     * 
     * @param appointmentId the appointment ID
     * @return MedicalRecord if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public MedicalRecord findByAppointmentId(String appointmentId) throws SQLException {
        String sql = "SELECT * FROM medical_records WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicalRecord(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all medical records from the database.
     * 
     * @return List of all MedicalRecord objects
     * @throws SQLException if database operation fails
     */
    public List<MedicalRecord> findAll() throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM medical_records ORDER BY record_id DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                records.add(mapResultSetToMedicalRecord(rs));
            }
        }
        return records;
    }

    /**
     * Updates an existing medical record in the database.
     * 
     * @param record the MedicalRecord object with updated values
     * @throws SQLException if database operation fails
     */
    public void updateMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "UPDATE medical_records SET appointment_id = ?, patient_id = ?, diagnosis = ? WHERE record_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, record.getAppointmentId());
            pstmt.setString(2, record.getPatientId());
            pstmt.setString(3, record.getDiagnosis());
            pstmt.setString(4, record.getRecordId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a medical record from the database.
     * 
     * @param recordId the record ID to delete
     * @throws SQLException if database operation fails
     */
    public void deleteMedicalRecord(String recordId) throws SQLException {
        String sql = "DELETE FROM medical_records WHERE record_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, recordId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a MedicalRecord object.
     * 
     * @param rs the ResultSet to map from
     * @return MedicalRecord object
     * @throws SQLException if mapping fails
     */
    private MedicalRecord mapResultSetToMedicalRecord(ResultSet rs) throws SQLException {
        String recordId = rs.getString("record_id");
        String appointmentId = rs.getString("appointment_id");
        String patientId = rs.getString("patient_id");
        String diagnosis = rs.getString("diagnosis");
        String notes = rs.getString("notes");

        return new MedicalRecord(recordId, appointmentId, patientId, diagnosis, notes);
    }
}
