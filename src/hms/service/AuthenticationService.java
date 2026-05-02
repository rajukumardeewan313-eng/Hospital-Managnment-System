package hms.service;

import hms.model.User;
import hms.util.PasswordUtil;
import hms.util.ValidationUtil;

/**
 * Service class for authentication and user management.
 * Handles login, signup, and user verification.
 * Works with HospitalSystem for in-memory persistence.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class AuthenticationService {

    private final HospitalSystem system;
    private User currentUser;  // Currently logged-in user

    /**
     * Constructs AuthenticationService with a HospitalSystem instance.
     * 
     * @param system the HospitalSystem instance
     */
    public AuthenticationService(HospitalSystem system) {
        this.system = system;
        this.currentUser = null;
    }

    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username
     * @param password the plain-text password
     * @return User object if authentication succeeds, null otherwise
     */
    public User login(String username, String password) {
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        User user = system.findUserByUsername(username);

        if (user == null) {
            return null;  // User not found
        }

        if (!user.isActive()) {
            throw new IllegalStateException("User account is inactive");
        }

        // Verify password
        if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            this.currentUser = user;
            return user;
        }

        return null;  // Password incorrect
    }

    /**
     * Registers a new user (signup).
     * 
     * @param username the username
     * @param password the plain-text password
     * @param email the email address
     * @param role the user role (ADMIN or USER)
     * @return User object if signup succeeds
     * @throws IllegalArgumentException if validation fails
     */
    public User signup(String username, String password, String email, User.Role role) 
            throws IllegalArgumentException {
        
        // Validation
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username. Must be 3-20 characters (letters, numbers, underscores)");
        }

        if (!ValidationUtil.isStrongPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters with uppercase, lowercase, and numbers");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check if username already exists
        if (system.findUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Hash password and create user
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(0, username, hashedPassword, email, role);
        
        system.addUser(newUser);
        return newUser;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Gets the currently logged-in user.
     * 
     * @return User object if logged in, null otherwise
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if current user is an admin.
     * 
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }

    /**
     * Checks if current user is a regular user.
     * 
     * @return true if regular user, false otherwise
     */
    public boolean isUser() {
        return currentUser != null && currentUser.getRole() == User.Role.USER;
    }
}
