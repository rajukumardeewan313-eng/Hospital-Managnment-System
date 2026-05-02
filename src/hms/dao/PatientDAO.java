package hms.dao;

import hms.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Patient entity.
 * Handles all database operations for patients.
 * USES PREPAREDSTATEMENT ONLY - prevents SQL injection.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class PatientDAO extends BaseDAO {

    /**
     * Creates a new patient in the database.
     * 
     * @param patient the Patient object to create
     * @throws SQLException if database operation fails
     */
    public void createPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (patient_id, name, phone, age, gender, address) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patient.getId());
            pstmt.setString(2, patient.getName());
            pstmt.setString(3, patient.getPhone());
            pstmt.setInt(4, patient.getAge());
            pstmt.setString(5, patient.getGender());
            pstmt.setString(6, patient.getAddress());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Finds a patient by ID.
     * 
     * @param patientId the patient ID to search for
     * @return Patient object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Patient findById(String patientId) throws SQLException {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all patients from the database.
     * 
     * @return List of all Patient objects
     * @throws SQLException if database operation fails
     */
    public List<Patient> findAll() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        }
        return patients;
    }

    /**
     * Searches for patients by name (case-insensitive).
     * 
     * @param name the name to search for
     * @return List of matching Patient objects
     * @throws SQLException if database operation fails
     */
    public List<Patient> searchByName(String name) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        }
        return patients;
    }

    /**
     * Updates an existing patient in the database.
     * 
     * @param patient the Patient object with updated values
     * @throws SQLException if database operation fails
     */
    public void updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET name = ?, phone = ?, age = ?, gender = ?, address = ? WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getPhone());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getAddress());
            pstmt.setString(6, patient.getId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a patient from the database.
     * 
     * @param patientId the patient ID to delete
     * @throws SQLException if database operation fails
     */
    public void deletePatient(String patientId) throws SQLException {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if a patient ID already exists in the database.
     * 
     * @param patientId the patient ID to check
     * @return true if exists, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean patientIdExists(String patientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM patients WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Patient object.
     * 
     * @param rs the ResultSet to map from
     * @return Patient object
     * @throws SQLException if mapping fails
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        String id = rs.getString("patient_id");
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String address = rs.getString("address");

        return new Patient(id, name, phone, age, gender, address);
    }
}
