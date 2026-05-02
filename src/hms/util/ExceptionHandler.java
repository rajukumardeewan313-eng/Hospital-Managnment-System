package hms.util;

import javax.swing.*;

/**
 * Utility class for centralized exception handling and error reporting.
 * Provides user-friendly error messages via JOptionPane.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class ExceptionHandler {

    /**
     * Handles database exceptions and shows user-friendly error message.
     * 
     * @param parent parent component for dialog
     * @param exception the SQLException
     * @param context context description (e.g., "loading patients")
     */
    public static void handleDatabaseException(java.awt.Component parent, Exception exception, String context) {
        String message;
        
        if (exception instanceof java.sql.SQLException) {
            message = "Database error occurred while " + context + ".\n\n" +
                     "Please check your database connection and try again.";
        } else {
            message = "An error occurred while " + context + ":\n\n" + exception.getMessage();
        }

        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
        
        // Log to console for debugging
        System.err.println("[ERROR] Exception during " + context + ":");
        exception.printStackTrace();
    }

    /**
     * Handles validation exceptions and shows error message.
     * 
     * @param parent parent component for dialog
     * @param message the error message
     */
    public static void handleValidationError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Handles authentication exceptions and shows error message.
     * 
     * @param parent parent component for dialog
     * @param message the error message
     */
    public static void handleAuthenticationError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Authentication Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message to the user.
     * 
     * @param parent parent component for dialog
     * @param message the success message
     */
    public static void showSuccessMessage(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a warning message to the user.
     * 
     * @param parent parent component for dialog
     * @param message the warning message
     */
    public static void showWarningMessage(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows a confirmation dialog and returns user's choice.
     * 
     * @param parent parent component for dialog
     * @param message the confirmation message
     * @param title the dialog title
     * @return JOptionPane.YES_OPTION or JOptionPane.NO_OPTION
     */
    public static int showConfirmDialog(java.awt.Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
    }
}
