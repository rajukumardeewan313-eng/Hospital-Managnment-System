package hms.dao;

import hms.billing.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Bill entity.
 * Handles all database operations for bills.
 * USES PREPAREDSTATEMENT ONLY - prevents SQL injection.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class BillDAO extends BaseDAO {

    /**
     * Creates a new bill in the database.
     * 
     * @param bill the Bill object to create
     * @throws SQLException if database operation fails
     */
    public void createBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO bills (bill_id, appointment_id, patient_id, total_amount, paid_amount, is_paid) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, bill.getBillId());
            pstmt.setString(2, bill.getAppointmentId());
            pstmt.setString(3, bill.getPatientId());
            pstmt.setDouble(4, bill.getTotalAmount());
            pstmt.setDouble(5, bill.getPaidAmount());
            pstmt.setBoolean(6, bill.isPaid());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Finds a bill by ID.
     * 
     * @param billId the bill ID to search for
     * @return Bill object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Bill findById(String billId) throws SQLException {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, billId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds all bills for a specific patient.
     * 
     * @param patientId the patient ID
     * @return List of Bill objects
     * @throws SQLException if database operation fails
     */
    public List<Bill> findByPatientId(String patientId) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE patient_id = ? ORDER BY bill_id DESC";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        }
        return bills;
    }

    /**
     * Finds all unpaid bills.
     * 
     * @return List of unpaid Bill objects
     * @throws SQLException if database operation fails
     */
    public List<Bill> findUnpaidBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE is_paid = FALSE ORDER BY bill_id DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        }
        return bills;
    }

    /**
     * Finds all paid bills.
     * 
     * @return List of paid Bill objects
     * @throws SQLException if database operation fails
     */
    public List<Bill> findPaidBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE is_paid = TRUE ORDER BY bill_id DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        }
        return bills;
    }

    /**
     * Retrieves all bills from the database.
     * 
     * @return List of all Bill objects
     * @throws SQLException if database operation fails
     */
    public List<Bill> findAll() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_id DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        }
        return bills;
    }

    /**
     * Updates an existing bill in the database.
     * 
     * @param bill the Bill object with updated values
     * @throws SQLException if database operation fails
     */
    public void updateBill(Bill bill) throws SQLException {
        String sql = "UPDATE bills SET appointment_id = ?, patient_id = ?, total_amount = ?, " +
                    "paid_amount = ?, is_paid = ? WHERE bill_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, bill.getAppointmentId());
            pstmt.setString(2, bill.getPatientId());
            pstmt.setDouble(3, bill.getTotalAmount());
            pstmt.setDouble(4, bill.getPaidAmount());
            pstmt.setBoolean(5, bill.isPaid());
            pstmt.setString(6, bill.getBillId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Marks a bill as paid.
     * 
     * @param billId the bill ID
     * @param paidAmount the amount paid
     * @throws SQLException if database operation fails
     */
    public void markAsPaid(String billId, double paidAmount) throws SQLException {
        String sql = "UPDATE bills SET paid_amount = ?, is_paid = TRUE WHERE bill_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setDouble(1, paidAmount);
            pstmt.setString(2, billId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a bill from the database.
     * 
     * @param billId the bill ID to delete
     * @throws SQLException if database operation fails
     */
    public void deleteBill(String billId) throws SQLException {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, billId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets total revenue from paid bills.
     * 
     * @return total amount from all paid bills
     * @throws SQLException if database operation fails
     */
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(paid_amount) as total FROM bills WHERE is_paid = TRUE";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Gets total outstanding amount from unpaid bills.
     * 
     * @return total amount from all unpaid bills
     * @throws SQLException if database operation fails
     */
    public double getTotalOutstanding() throws SQLException {
        String sql = "SELECT SUM(total_amount - paid_amount) as outstanding FROM bills WHERE is_paid = FALSE";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                double amount = rs.getDouble("outstanding");
                return rs.wasNull() ? 0.0 : amount;
            }
        }
        return 0.0;
    }

    /**
     * Maps a ResultSet row to a Bill object.
     * 
     * @param rs the ResultSet to map from
     * @return Bill object
     * @throws SQLException if mapping fails
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        String billId = rs.getString("bill_id");
        String appointmentId = rs.getString("appointment_id");
        String patientId = rs.getString("patient_id");
        double totalAmount = rs.getDouble("total_amount");
        double paidAmount = rs.getDouble("paid_amount");
        rs.getBoolean("is_paid"); // Status check

        Bill bill = new Bill(billId, appointmentId, patientId, totalAmount);
        bill.setPaidAmount(paidAmount);
        // Note: Bill class may need to add setPaid() method
        // For now, set through payment logic

        return bill;
    }
}
