package hms.model;

import java.io.Serializable;

/**
 * User entity for authentication and authorization.
 * Represents a system user with role-based access control.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * User roles in the system.
     */
    public enum Role {
        ADMIN("Administrator - Full system access"),
        USER("Regular User - Limited access");

        private final String description;

        Role(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private int userId;
    private String username;
    private String passwordHash;  // Never store plain text passwords
    private String email;
    private Role role;
    private boolean active;

    /**
     * Constructs a User object.
     * 
     * @param userId unique identifier
     * @param username unique username
     * @param passwordHash hashed password
     * @param email user email
     * @param role user role (ADMIN or USER)
     */
    public User(int userId, String username, String passwordHash, String email, Role role) {
        this.userId = userId;
        setUsername(username);
        setPasswordHash(passwordHash);
        setEmail(email);
        setRole(role);
        this.active = true;
    }

    // ── Getters & Setters ────────────────────────────────────────

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters.");
        }
        this.username = username.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be empty.");
        }
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        this.email = email.trim();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
