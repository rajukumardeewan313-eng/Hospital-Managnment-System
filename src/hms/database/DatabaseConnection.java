package hms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton DatabaseConnection class for managing database connections.
 * Supports both SQLite and MySQL databases.
 * 
 * SQLite: Uses embedded SQLite driver (no external DB server needed)
 * MySQL: Requires MySQL server running on localhost:3306
 * 
 * @author HMS Team
 * @version 1.0
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Database configuration
    private static final String DB_TYPE = "h2";  // "h2" (pure Java), "sqlite" or "mysql"
    private static final String H2_URL = "jdbc:h2:./data/hms";
    private static final String SQLITE_URL = "jdbc:sqlite:data/hms.dat";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/hms_db";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    /**
     * Private constructor to prevent instantiation.
     * Use getInstance() to get the singleton instance.
     */
    private DatabaseConnection() {
    }

    /**
     * Gets the singleton instance of DatabaseConnection.
     * Initializes the database connection if not already done.
     * 
     * @return the singleton DatabaseConnection instance
     * @throws SQLException if connection fails
     */
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
            instance.connect();
        }
        return instance;
    }

    /**
     * Establishes the database connection based on configured DB_TYPE.
     * 
     * @throws SQLException if connection fails
     */
    private void connect() throws SQLException {
        try {
            if (DB_TYPE.equalsIgnoreCase("h2")) {
                // H2 Database - pure Java, auto-loaded by DriverManager
                try {
                    Class.forName("org.h2.Driver");
                } catch (ClassNotFoundException e) {
                    // If H2 not available, fall back to Derby
                    System.out.println("H2 not available, trying Derby...");
                    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                }
                connection = DriverManager.getConnection(H2_URL, "sa", "");
                System.out.println("✓ H2 Connection Established: " + H2_URL);
            } else if (DB_TYPE.equalsIgnoreCase("sqlite")) {
                // Load SQLite driver
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(SQLITE_URL);
                System.out.println("✓ SQLite Connection Established: " + SQLITE_URL);
            } else if (DB_TYPE.equalsIgnoreCase("mysql")) {
                // Load MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                System.out.println("✓ MySQL Connection Established: " + MYSQL_URL);
            }
            // Enable foreign keys for SQLite
            if (DB_TYPE.equalsIgnoreCase("sqlite")) {
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the active database connection.
     * 
     * @return the Connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection.
     * 
     * @throws SQLException if closing fails
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("✓ Database Connection Closed");
        }
    }

    /**
     * Checks if the connection is still active.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Reconnects to the database if connection is lost.
     * 
     * @throws SQLException if reconnection fails
     */
    public void reconnect() throws SQLException {
        closeConnection();
        connection = null;
        connect();
    }
}
