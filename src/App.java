import hms.model.User;
import hms.persistence.DataStore;
import hms.service.AuthenticationService;
import hms.service.HospitalSystem;
import hms.ui.LoginFrame;
import hms.ui.MainFrame;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Application entry point for Hospital Management System (HMS).
 * 
 * Responsibilities:
 * 1. Load HospitalSystem from persistent storage (serialization)
 * 2. Create demo admin user if system is empty
 * 3. Show login/authentication screen
 * 4. Launch main application after user authentication
 * 
 * @author HMS Team
 * @version 2.0
 */
public class App {

    public static void main(String[] args) {
        // Set UI properties for better rendering
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception ignored) {
        }


        // Show a lightweight splash immediately, run heavy init off the EDT
        SwingUtilities.invokeLater(() -> {
            final JFrame splash = new JFrame();
            splash.setUndecorated(true);
            splash.setSize(360, 120);
            splash.setLocationRelativeTo(null);
            splash.setLayout(new BorderLayout());
            JLabel lbl = new JLabel("Initializing HMS...", SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
            splash.add(lbl, BorderLayout.CENTER);
            splash.setVisible(true);

            // Background initialization thread
            new Thread(() -> {
                try {
                    initializeAndLaunchApp();
                    // Once init completes, dispose splash and show login on EDT
                    SwingUtilities.invokeLater(() -> {
                        splash.dispose();
                        try {
                            // initializeAndLaunchApp already launches the login
                        } catch (Exception ignored) {}
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        splash.dispose();
                        JOptionPane.showMessageDialog(null,
                                "Failed to initialize application:\n" + e.getMessage(),
                                "Initialization Error",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    });
                }
            }, "HMS-Init-Thread").start();
        });
    }

    /**
     * Initializes the application with persistent data and launches it.
     */
    private static void initializeAndLaunchApp() throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  Hospital Management System (HMS) v2.0 - STARTING");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // 1. Load persistent data
        System.out.println("[1/3] Loading application data...");
        HospitalSystem system = DataStore.load(null);
        System.out.println("✓ Data loaded successfully\n");

        // 2. Create demo users if system is empty
        System.out.println("[2/3] Checking for demo users...");
        createDemoUsersIfNeeded(system);
        System.out.println();

        // 3. Show login screen and launch main app
        System.out.println("[3/3] Launching authentication...\n");
        showLoginAndLaunchApp(system);
    }

    /**
     * Creates demo admin and user accounts if the system is empty.
     * Demo credentials:
     *   - Admin: username=admin, password=Admin@123
     *   - User: username=user, password=User@123
     */
    private static void createDemoUsersIfNeeded(HospitalSystem system) {
        // Check if admin user exists
        if (system.findUserByUsername("admin") == null) {
            try {
                User admin = new User(0, "admin", 
                    hms.util.PasswordUtil.hashPassword("Admin@123"), 
                    "admin@hms.local", User.Role.ADMIN);
                system.addUser(admin);
                System.out.println("✓ Demo admin created (username: admin, password: Admin@123)");
            } catch (Exception e) {
                System.err.println("⚠ Could not create demo admin: " + e.getMessage());
            }
        } else {
            System.out.println("✓ Admin user already exists");
        }
        
        // Check if regular user exists
        if (system.findUserByUsername("user") == null) {
            try {
                User user = new User(0, "user", 
                    hms.util.PasswordUtil.hashPassword("User@123"), 
                    "user@hms.local", User.Role.USER);
                system.addUser(user);
                System.out.println("✓ Demo user created (username: user, password: User@123)");
            } catch (Exception e) {
                System.err.println("⚠ Could not create demo user: " + e.getMessage());
            }
        } else {
            System.out.println("✓ Regular user already exists");
        }
        
        // Save updated system
        try {
            DataStore.save(system, null);
        } catch (Exception e) {
            System.err.println("Warning: Could not save data: " + e.getMessage());
        }
    }

    /**
     * Shows login screen and launches main application after authentication.
     */
    private static void showLoginAndLaunchApp(HospitalSystem system) throws Exception {
        AuthenticationService authService = new AuthenticationService(system);
        
        LoginFrame loginFrame = new LoginFrame(authService);
        
        // Wait for user to close login frame
        while (loginFrame.isDisplayable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Check if user successfully logged in
        User loggedInUser = loginFrame.getLoggedInUser();
        if (loggedInUser == null) {
            System.out.println("✗ User cancelled login. Exiting application.");
            System.exit(0);
        }
        
        System.out.println("✓ User authenticated: " + loggedInUser.getUsername());
        System.out.println("✓ Launching main application...\n");
        
        // Launch main application
        new MainFrame(system, authService);
    }
}
