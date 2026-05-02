package hms.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * Provides methods for validating email, phone, ID, and other fields.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class ValidationUtil {

    // Regex patterns for validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^[0-9\\-\\+\\(\\)\\s]{7,15}$";
    private static final String ID_REGEX = "^[0-9]+$";
    private static final String NAME_REGEX = "^[A-Za-z\\s]+$";
    private static final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    /**
     * Validates if a string is a valid email address.
     * 
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if a string is a valid phone number.
     * Accepts numbers, hyphens, parentheses, and plus sign.
     * Length: 7-15 characters
     * 
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates if a string contains only numeric digits.
     * Used for ID validation.
     * 
     * @param id the ID to validate
     * @return true if valid (numeric only), false otherwise
     */
    public static boolean isValidNumericId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return ID_PATTERN.matcher(id).matches();
    }

    /**
     * Validates if a string contains only letters and spaces.
     * Used for name validation.
     * 
     * @param name the name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validates if a string is a valid username.
     * Allows: letters, numbers, underscores
     * Length: 3-20 characters
     * 
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates if a field is not empty.
     * 
     * @param field the field to check
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }

    /**
     * Validates if a numeric value is within a range.
     * 
     * @param value the value to check
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return true if within range, false otherwise
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validates if a numeric value is positive.
     * 
     * @param value the value to check
     * @return true if positive, false otherwise
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Validates password strength.
     * Requirements:
     * - At least 8 characters
     * - Contains at least one uppercase letter
     * - Contains at least one lowercase letter
     * - Contains at least one digit
     * 
     * @param password the password to validate
     * @return true if strong, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Sanitizes input to prevent SQL injection.
     * Removes or escapes dangerous characters.
     * 
     * @param input the input to sanitize
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove or escape SQL dangerous characters
        return input.replace("'", "''")
                    .replace("\"", "\\\"")
                    .replace(";", "")
                    .replace("--", "");
    }

    /**
     * Validates field length.
     * 
     * @param field the field to check
     * @param maxLength maximum allowed length
     * @return true if within length, false otherwise
     */
    public static boolean isValidLength(String field, int maxLength) {
        if (field == null) {
            return false;
        }
        return field.length() <= maxLength;
    }
}
