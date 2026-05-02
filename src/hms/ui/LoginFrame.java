package hms.ui;

import hms.model.User;
import hms.service.AuthenticationService;
import hms.util.ExceptionHandler;
import hms.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Login/Signup Frame for HMS authentication.
 * Allows users to login or create a new account.
 * 
 * @author HMS Team
 * @version 1.0
 */
public class LoginFrame extends JFrame {

    private final AuthenticationService authService;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    // Login panel components
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Signup panel components
    private JTextField signupUsernameField;
    private JPasswordField signupPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<String> roleCombo;
    
    private User loggedInUser;

    /**
     * Constructs LoginFrame with authentication service.
     * 
     * @param authService the AuthenticationService instance
     */
    public LoginFrame(AuthenticationService authService) {
        this.authService = authService;
        this.loggedInUser = null;
        
        setTitle("HMS - Login / Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        buildUI();
        setVisible(true);
    }

    /**
     * Builds the UI with login and signup panels.
     */
    private void buildUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(240, 242, 245));
        
        cardPanel.add(buildLoginPanel(), "login");
        cardPanel.add(buildSignupPanel(), "signup");
        
        add(cardPanel);
        cardLayout.show(cardPanel, "login");
    }

    /**
     * Builds the login panel.
     */
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 40, 70), 
                                                          getWidth(), getHeight(), new Color(50, 80, 120));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // White card
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(30, 80, getWidth() - 60, 380, 15, 15);
            }
        };
        panel.setLayout(null);
        
        // Title
        JLabel titleLabel = new JLabel("Hospital Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 20, 400, 30);
        panel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 220, 240));
        subtitleLabel.setBounds(50, 55, 400, 20);
        panel.add(subtitleLabel);
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        usernameLabel.setBounds(60, 120, 100, 20);
        usernameLabel.setForeground(new Color(60, 80, 100));
        panel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usernameField.setBounds(60, 145, 350, 35);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true));
        panel.add(usernameField);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passwordLabel.setBounds(60, 190, 100, 20);
        passwordLabel.setForeground(new Color(60, 80, 100));
        panel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passwordField.setBounds(60, 215, 350, 35);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginAction();
                }
            }
        });
        panel.add(passwordField);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBounds(60, 280, 350, 40);
        loginButton.setBackground(new Color(30, 120, 160));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> loginAction());
        panel.add(loginButton);
        
        // Signup link
        JLabel signupLink = new JLabel("<html><u>Don't have an account? Sign up</u></html>");
        signupLink.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signupLink.setBounds(60, 340, 350, 20);
        signupLink.setForeground(new Color(30, 120, 160));
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchToSignup();
            }
        });
        panel.add(signupLink);
        
        // Demo credentials hint
        JLabel demoLabel = new JLabel("Demo: admin / Admin@123");
        demoLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        demoLabel.setBounds(60, 365, 350, 15);
        demoLabel.setForeground(new Color(150, 150, 150));
        panel.add(demoLabel);
        
        return panel;
    }

    /**
     * Builds the signup panel.
     */
    private JPanel buildSignupPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 40, 70), 
                                                          getWidth(), getHeight(), new Color(50, 80, 120));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(30, 50, getWidth() - 60, 440, 15, 15);
            }
        };
        panel.setLayout(null);
        
        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 15, 400, 30);
        panel.add(titleLabel);
        
        int yPos = 75;
        int fieldHeight = 30;
        int gap = 5;
        
        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        usernameLabel.setBounds(50, yPos, 100, 15);
        usernameLabel.setForeground(new Color(60, 80, 100));
        panel.add(usernameLabel);
        yPos += 20;
        
        signupUsernameField = new JTextField();
        signupUsernameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signupUsernameField.setBounds(50, yPos, 340, fieldHeight);
        signupUsernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true));
        panel.add(signupUsernameField);
        yPos += fieldHeight + gap + 10;
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        emailLabel.setBounds(50, yPos, 100, 15);
        emailLabel.setForeground(new Color(60, 80, 100));
        panel.add(emailLabel);
        yPos += 20;
        
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        emailField.setBounds(50, yPos, 340, fieldHeight);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true));
        panel.add(emailField);
        yPos += fieldHeight + gap + 10;
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        passwordLabel.setBounds(50, yPos, 100, 15);
        passwordLabel.setForeground(new Color(60, 80, 100));
        panel.add(passwordLabel);
        yPos += 20;
        
        signupPasswordField = new JPasswordField();
        signupPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signupPasswordField.setBounds(50, yPos, 340, fieldHeight);
        signupPasswordField.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true));
        panel.add(signupPasswordField);
        yPos += fieldHeight + gap + 10;
        
        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        confirmLabel.setBounds(50, yPos, 100, 15);
        confirmLabel.setForeground(new Color(60, 80, 100));
        panel.add(confirmLabel);
        yPos += 20;
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        confirmPasswordField.setBounds(50, yPos, 340, fieldHeight);
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true));
        panel.add(confirmPasswordField);
        yPos += fieldHeight + gap + 10;
        
        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        roleLabel.setBounds(50, yPos, 100, 15);
        roleLabel.setForeground(new Color(60, 80, 100));
        panel.add(roleLabel);
        yPos += 20;
        
        roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roleCombo.setBounds(50, yPos, 340, fieldHeight);
        panel.add(roleCombo);
        yPos += fieldHeight + gap + 25;
        
        // Signup button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        signupButton.setBounds(50, yPos, 340, 38);
        signupButton.setBackground(new Color(30, 120, 160));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBorderPainted(false);
        signupButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupButton.addActionListener(e -> signupAction());
        panel.add(signupButton);
        yPos += 50;
        
        // Back to login link
        JLabel backLink = new JLabel("<html><u>Already have an account? Login</u></html>");
        backLink.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backLink.setBounds(50, yPos, 340, 20);
        backLink.setForeground(new Color(30, 120, 160));
        backLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchToLogin();
            }
        });
        panel.add(backLink);
        
        return panel;
    }

    /**
     * Handles login action.
     */
    private void loginAction() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            ExceptionHandler.handleValidationError(this, "Username and password cannot be empty");
            return;
        }
        
        try {
            User user = authService.login(username, password);
            if (user != null) {
                loggedInUser = user;
                ExceptionHandler.showSuccessMessage(this, "Welcome " + user.getUsername() + "!");
                dispose();
            } else {
                ExceptionHandler.handleAuthenticationError(this, "Invalid username or password");
            }
        } catch (Exception e) {
            ExceptionHandler.handleAuthenticationError(this, e.getMessage());
        }
    }

    /**
     * Handles signup action.
     */
    private void signupAction() {
        String username = signupUsernameField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password) || 
            !ValidationUtil.isNotEmpty(confirmPass) || !ValidationUtil.isNotEmpty(email)) {
            ExceptionHandler.handleValidationError(this, "All fields are required");
            return;
        }
        
        if (!password.equals(confirmPass)) {
            ExceptionHandler.handleValidationError(this, "Passwords do not match");
            return;
        }
        
        try {
            User.Role userRole = User.Role.valueOf(role);
            authService.signup(username, password, email, userRole);
            ExceptionHandler.showSuccessMessage(this, "Account created successfully! You can now login.");
            switchToLogin();
            clearSignupFields();
        } catch (IllegalArgumentException e) {
            ExceptionHandler.handleValidationError(this, e.getMessage());
        }
    }

    /**
     * Switches to login panel.
     */
    private void switchToLogin() {
        cardLayout.show(cardPanel, "login");
        clearLoginFields();
    }

    /**
     * Switches to signup panel.
     */
    private void switchToSignup() {
        cardLayout.show(cardPanel, "signup");
        clearSignupFields();
    }

    /**
     * Clears login fields.
     */
    private void clearLoginFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    /**
     * Clears signup fields.
     */
    private void clearSignupFields() {
        signupUsernameField.setText("");
        signupPasswordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        roleCombo.setSelectedIndex(0);
    }

    /**
     * Gets the logged-in user.
     * 
     * @return User object if logged in, null otherwise
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
