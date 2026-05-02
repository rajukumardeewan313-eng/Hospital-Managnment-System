package hms.ui;

import hms.persistence.DataStore;
import hms.service.AuthenticationService;
import hms.service.HospitalSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window with role-based dashboard.
 * Displays different content based on user role (Admin vs User).
 * 
 * @author HMS Team
 * @version 2.0
 */
public class MainFrame extends JFrame {

    private final HospitalSystem system;
    private final AuthenticationService authService;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea   = new JPanel(cardLayout);

    // Sidebar nav buttons
    private static final String[] TABS = {
        "Dashboard", "Patients", "Doctors",
        "Appointments", "Medical Records", "Billing"
    };
    private static final String[] ICONS = {
        "\uD83D\uDCCA", "\uD83D\uDC65", "\uD83D\uDC68\u200D\u2695\uFE0F",
        "\uD83D\uDCC5", "\uD83D\uDCCB", "\uD83D\uDCB0"
    };
    private static final Color[] TAB_COLORS = {
        new Color(30, 40, 70),
        UIUtils.TEAL_DARK,
        UIUtils.INDIGO,
        UIUtils.AMBER_DARK,
        UIUtils.PURPLE,
        UIUtils.CORAL_DARK
    };
    private JButton[] navButtons;
    private int activeTab = 0;
    private JLabel userInfoLabel;

    /**
     * Constructs MainFrame with system and authentication service.
     * 
     * @param system the HospitalSystem instance
     * @param authService the AuthenticationService instance
     */
    public MainFrame(HospitalSystem system, AuthenticationService authService) {
        this.system = system;
        this.authService = authService;
        
        setTitle("Hospital Management System — HMS v2.0 [" + authService.getCurrentUser().getRole() + "]");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { onClose(); }
        });

        buildUI();
        selectTab(0);
        setVisible(true);
    }

    /**
     * Builds the main UI with sidebar and content area.
     */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIUtils.BG_MAIN);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(contentArea,   BorderLayout.CENTER);
        setContentPane(root);

        // Register panels (all panels available to all roles for now)
        contentArea.add(new ReportsPanel(system),       "Dashboard");
        contentArea.add(new PatientPanel(system),       "Patients");
        contentArea.add(new DoctorPanel(system),        "Doctors");
        contentArea.add(new AppointmentPanel(system),   "Appointments");
        contentArea.add(new MedicalRecordPanel(system), "Medical Records");
        contentArea.add(new BillingPanel(system),       "Billing");
    }

    /**
     * Builds the sidebar with navigation buttons.
     * 
     * @return the sidebar panel
     */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(18, 24, 45));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(190, 0));

        // Logo area
        JPanel logo = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(12, 18, 38));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        logo.setOpaque(false);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        JLabel logoIcon = new JLabel("\uD83C\uDFE5", SwingConstants.LEFT);
        logoIcon.setFont(new Font("SansSerif", Font.PLAIN, 30));
        JLabel logoText = new JLabel("<html><b style='color:#00a8a8;font-size:13px;'>HMS</b><br>" +
                "<span style='color:#8899aa;font-size:10px;'>Hospital System</span></html>");
        logo.add(logoIcon, BorderLayout.WEST);
        logo.add(logoText, BorderLayout.CENTER);
        sidebar.add(logo, BorderLayout.NORTH);

        // User info area
        JPanel userPanel = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(12, 18, 38));
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));
        
        userInfoLabel = new JLabel("<html><span style='color:#a0c0d8;font-size:10px;'>Logged in as:</span><br>" +
                "<span style='color:#00d4d4;font-size:11px;font-weight:bold;'>" + 
                authService.getCurrentUser().getUsername() + "</span><br>" +
                "<span style='color:#808080;font-size:9px;'>" + 
                authService.getCurrentUser().getRole() + "</span></html>");
        userPanel.add(userInfoLabel, BorderLayout.NORTH);
        
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        logoutBtn.setBackground(new Color(200, 60, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        userPanel.add(logoutBtn, BorderLayout.SOUTH);
        
        sidebar.add(userPanel, BorderLayout.NORTH);

        // Nav buttons
        JPanel nav = new JPanel();
        nav.setLayout(new GridLayout(TABS.length, 1, 0, 4));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navButtons = new JButton[TABS.length];

        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            JButton btn = new JButton(ICONS[i] + "  " + TABS[i]) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    if (activeTab == idx) {
                        g2.setColor(TAB_COLORS[idx]);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(255,255,255,18));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setForeground(activeTab == i ? Color.WHITE : new Color(160, 180, 200));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
            btn.addActionListener(e -> selectTab(idx));
            navButtons[i] = btn;
            nav.add(btn);
        }
        sidebar.add(nav, BorderLayout.CENTER);

        // Bottom: Action buttons (Load Sample only - removed Save/Load for now)
        JPanel bottom = new JPanel(new GridLayout(1, 1, 0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(12, 18, 38)); g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 16, 10));

        JButton btnLoadSample = sidebarActionBtn("📦 Load Samples", UIUtils.INDIGO);
        btnLoadSample.addActionListener(e -> loadSample());
        bottom.add(btnLoadSample);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Creates a sidebar action button with custom styling.
     * 
     * @param text the button text
     * @param bg the background color
     * @return styled JButton
     */
    private JButton sidebarActionBtn(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Selects and displays a tab by index.
     * 
     * @param idx the tab index
     */
    private void selectTab(int idx) {
        activeTab = idx;
        cardLayout.show(contentArea, TABS[idx]);
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setForeground(i == idx ? Color.WHITE : new Color(150, 170, 190));
            navButtons[i].repaint();
        }
        // Force UI update
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * Loads sample data for testing/demo purposes.
     */
    private void loadSample() {
        if (!UIUtils.confirm(this, "Load sample patients and doctors? (skips if already exist)"))
            return;
        system.loadSampleData();
        UIUtils.showInfo(this, "Sample data loaded. Switch tabs to view.");
    }

    /**
     * Logs out the current user and closes the application.
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Save data before logout
            try {
                DataStore.save(system, null);
                System.out.println("✓ Data saved successfully on logout");
            } catch (Exception e) {
                System.err.println("⚠ Warning: Could not save data: " + e.getMessage());
            }
            authService.logout();
            dispose();
            System.exit(0);
        }
    }

    /**
     * Handles window closing event.
     */
    private void onClose() {
        logout();
    }
}
