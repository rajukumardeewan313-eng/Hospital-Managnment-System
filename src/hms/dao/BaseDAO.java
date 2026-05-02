package hms.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract base class for all DAO (Data Access Object) classes.
 * Provides common functionality for database operations.
 * 
 * @author HMS Team
 * @version 1.0
 */
public abstract class BaseDAO {

    /**
     * Gets the database connection.
     * 
     * @return the active Connection object
     * @throws SQLException if connection fails
     */
    protected Connection getConnection() throws SQLException {
        return hms.database.DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Template method for CRUD operations.
     * Subclasses should implement specific operations.
     */
}
