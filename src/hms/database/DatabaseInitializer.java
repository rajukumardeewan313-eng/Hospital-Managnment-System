package hms.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Initializes the HMS database schema.
 * Creates all necessary tables for the Hospital Management System.
 * Call once on first run: DatabaseInitializer.initializeDatabase();
 * 
 * @author HMS Team
 * @version 1.0
 */
public class DatabaseInitializer {

    /**
     * Initializes the entire database schema.
     * Creates tables if they don't exist.
     * 
     * @throws SQLException if initialization fails
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement stmt = conn.createStatement();

        try {
            // Create Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
                    active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Users table created/verified");

            // Create Patients table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS patients (
                    patient_id VARCHAR(20) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20) NOT NULL,
                    age INT NOT NULL,
                    gender VARCHAR(20) NOT NULL,
                    address VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Patients table created/verified");

            // Create Doctors table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS doctors (
                    doctor_id VARCHAR(20) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20) NOT NULL,
                    specialization VARCHAR(100) NOT NULL,
                    consultation_fee DECIMAL(10,2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Doctors table created/verified");

            // Create Appointments table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS appointments (
                    appointment_id VARCHAR(20) PRIMARY KEY,
                    patient_id VARCHAR(20) NOT NULL,
                    doctor_id VARCHAR(20) NOT NULL,
                    appointment_date DATETIME NOT NULL,
                    status ENUM('BOOKED', 'CANCELLED', 'COMPLETED') DEFAULT 'BOOKED',
                    notes TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
                    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
                )
            """);
            System.out.println("✓ Appointments table created/verified");

            // Create Medical Records table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medical_records (
                    record_id VARCHAR(20) PRIMARY KEY,
                    appointment_id VARCHAR(20) NOT NULL,
                    patient_id VARCHAR(20) NOT NULL,
                    diagnosis VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
                    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
                )
            """);
            System.out.println("✓ Medical Records table created/verified");

            // Create Prescription Items table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prescription_items (
                    item_id VARCHAR(20) PRIMARY KEY,
                    record_id VARCHAR(20) NOT NULL,
                    medicine_name VARCHAR(100) NOT NULL,
                    dosage VARCHAR(100) NOT NULL,
                    duration VARCHAR(100) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (record_id) REFERENCES medical_records(record_id)
                )
            """);
            System.out.println("✓ Prescription Items table created/verified");

            // Create Bills table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bills (
                    bill_id VARCHAR(20) PRIMARY KEY,
                    appointment_id VARCHAR(20),
                    patient_id VARCHAR(20) NOT NULL,
                    total_amount DECIMAL(10,2) NOT NULL,
                    paid_amount DECIMAL(10,2) DEFAULT 0,
                    is_paid BOOLEAN DEFAULT FALSE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
                    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
                )
            """);
            System.out.println("✓ Bills table created/verified");

            // Create Bill Items table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bill_items (
                    item_id VARCHAR(20) PRIMARY KEY,
                    bill_id VARCHAR(20) NOT NULL,
                    item_type ENUM('CONSULTATION', 'LAB', 'MEDICINE') NOT NULL,
                    description VARCHAR(255) NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (bill_id) REFERENCES bills(bill_id)
                )
            """);
            System.out.println("✓ Bill Items table created/verified");

            System.out.println("\n✓✓✓ DATABASE INITIALIZED SUCCESSFULLY ✓✓✓\n");

        } catch (SQLException e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            throw e;
        } finally {
            stmt.close();
        }
    }

    /**
     * Drops all tables (use with caution - for testing/reset only).
     * 
     * @throws SQLException if drop fails
     */
    public static void dropAllTables() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement stmt = conn.createStatement();

        try {
            stmt.execute("DROP TABLE IF EXISTS bill_items");
            stmt.execute("DROP TABLE IF EXISTS bills");
            stmt.execute("DROP TABLE IF EXISTS prescription_items");
            stmt.execute("DROP TABLE IF EXISTS medical_records");
            stmt.execute("DROP TABLE IF EXISTS appointments");
            stmt.execute("DROP TABLE IF EXISTS doctors");
            stmt.execute("DROP TABLE IF EXISTS patients");
            stmt.execute("DROP TABLE IF EXISTS users");
            System.out.println("✓ All tables dropped");
        } finally {
            stmt.close();
        }
    }
}
