import hms.service.HospitalSystem;
import hms.service.AuthenticationService;
import hms.model.*;
 

/**
 * Database Initialization Utility
 * Initializes the HMS database with demo data
 */
public class InitializeDB {
    public static void main(String[] args) {
        try {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  Hospital Management System - Database Initialization");
            System.out.println("═══════════════════════════════════════════════════════════\n");

            // Step 1: Load persistent data
            System.out.println("[1/3] Loading application data...");
            HospitalSystem system = hms.persistence.DataStore.load(null);
            System.out.println("✓ Data loaded successfully\n");

            // Step 2: Create demo users if system is empty
            System.out.println("[2/3] Setting up demo users...");
            AuthenticationService authService = new AuthenticationService(system);
            
            // Add demo users
            try {
                authService.signup("admin", "Admin@123", "admin@hms.com", User.Role.ADMIN);
                System.out.println("  ✓ Admin user created");
            } catch (Exception e) {
                System.out.println("  ℹ Admin user already exists");
            }
            
            try {
                authService.signup("doctor1", "Doctor@123", "doctor@hms.com", User.Role.USER);
                System.out.println("  ✓ Doctor user created");
            } catch (Exception e) {
                System.out.println("  ℹ Doctor user already exists");
            }
            
            try {
                authService.signup("patient1", "Patient@123", "patient@hms.com", User.Role.USER);
                System.out.println("  ✓ Patient user created\n");
            } catch (Exception e) {
                System.out.println("  ℹ Patient user already exists\n");
            }

            // Step 3: Save data
            System.out.println("[3/3] Saving data...");
            hms.persistence.DataStore.save(system, null);
            System.out.println("✓ Data saved successfully!\n");
            
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("  Setup Complete! Ready to Run");
            System.out.println("═══════════════════════════════════════════════════════════\n");

            System.out.println("To launch the application, use one of these commands:\n");
            System.out.println("  Windows (Batch):  run.bat");
            System.out.println("  Windows (PowerShell):  .\\run.ps1");
            System.out.println("  Or manually:  java -cp \"out;lib/*\" App\n");

            System.out.println("Login credentials:");
            System.out.println("  Username: admin");
            System.out.println("  Password: Admin@123");
            System.out.println("  (Role: Admin)\n");

        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
