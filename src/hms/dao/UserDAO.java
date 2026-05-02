package hms.dao;

import hms.model.User;
import hms.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for User entity.
 * Handles all database operations for users (authentication, user management).
 * USES PREPAREDSTATEMENT ONLY - prevents SQL injection.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class UserDAO extends BaseDAO {

    /**
     * Creates a new user in the database.
     * Password is automatically hashed before storage.
     * 
     * @param user the User object to create
     * @throws SQLException if database operation fails
     */
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email, role, active) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole().name());
            pstmt.setBoolean(5, user.isActive());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds a user by ID.
     * 
     * @param userId the user ID to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return List of all User objects
     * @throws SQLException if database operation fails
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    /**
     * Updates an existing user in the database.
     * 
     * @param user the User object with updated values
     * @throws SQLException if database operation fails
     */
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, role = ?, active = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setInt(5, user.getUserId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates a user's password in the database.
     * Password is automatically hashed.
     * 
     * @param userId the user ID
     * @param newPassword the new plain-text password
     * @throws SQLException if database operation fails
     */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a user from the database.
     * 
     * @param userId the user ID to delete
     * @throws SQLException if database operation fails
     */
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if a username already exists in the database.
     * 
     * @param username the username to check
     * @return true if exists, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a User object.
     * 
     * @param rs the ResultSet to map from
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String email = rs.getString("email");
        String roleString = rs.getString("role");
        boolean active = rs.getBoolean("active");

        User.Role role = User.Role.valueOf(roleString);
        User user = new User(userId, username, passwordHash, email, role);
        user.setActive(active);

        return user;
    }
}
