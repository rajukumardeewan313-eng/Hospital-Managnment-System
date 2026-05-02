package hms.dao;

import hms.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Doctor entity.
 * Handles all database operations for doctors.
 * USES PREPAREDSTATEMENT ONLY - prevents SQL injection.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class DoctorDAO extends BaseDAO {

    /**
     * Creates a new doctor in the database.
     * 
     * @param doctor the Doctor object to create
     * @throws SQLException if database operation fails
     */
    public void createDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (doctor_id, name, phone, specialization, consultation_fee) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, doctor.getId());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getSpecialization());
            pstmt.setDouble(5, doctor.getConsultationFee());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Finds a doctor by ID.
     * 
     * @param doctorId the doctor ID to search for
     * @return Doctor object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Doctor findById(String doctorId) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all doctors from the database.
     * 
     * @return List of all Doctor objects
     * @throws SQLException if database operation fails
     */
    public List<Doctor> findAll() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        }
        return doctors;
    }

    /**
     * Searches for doctors by name (case-insensitive).
     * 
     * @param name the name to search for
     * @return List of matching Doctor objects
     * @throws SQLException if database operation fails
     */
    public List<Doctor> searchByName(String name) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        }
        return doctors;
    }

    /**
     * Finds all doctors by specialization.
     * 
     * @param specialization the specialization to search for
     * @return List of matching Doctor objects
     * @throws SQLException if database operation fails
     */
    public List<Doctor> findBySpecialization(String specialization) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE LOWER(specialization) LIKE LOWER(?) ORDER BY name";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + specialization + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        }
        return doctors;
    }

    /**
     * Updates an existing doctor in the database.
     * 
     * @param doctor the Doctor object with updated values
     * @throws SQLException if database operation fails
     */
    public void updateDoctor(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET name = ?, phone = ?, specialization = ?, consultation_fee = ? WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getPhone());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setDouble(4, doctor.getConsultationFee());
            pstmt.setString(5, doctor.getId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a doctor from the database.
     * 
     * @param doctorId the doctor ID to delete
     * @throws SQLException if database operation fails
     */
    public void deleteDoctor(String doctorId) throws SQLException {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, doctorId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if a doctor ID already exists in the database.
     * 
     * @param doctorId the doctor ID to check
     * @return true if exists, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean doctorIdExists(String doctorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM doctors WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a Doctor object.
     * 
     * @param rs the ResultSet to map from
     * @return Doctor object
     * @throws SQLException if mapping fails
     */
    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        String id = rs.getString("doctor_id");
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        String specialization = rs.getString("specialization");
        double consultationFee = rs.getDouble("consultation_fee");

        return new Doctor(id, name, phone, specialization, consultationFee);
    }
}
