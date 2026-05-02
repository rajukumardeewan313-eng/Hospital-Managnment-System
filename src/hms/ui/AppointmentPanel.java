package hms.ui;

import hms.model.Appointment;
import hms.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Appointment scheduling panel — amber / warm accent theme.
 */
public class AppointmentPanel extends JPanel {
    private final HospitalSystem system;

    private JTextField tfApptId, tfPatientId, tfDoctorId, tfDateTime, tfNotes, tfSearch;
    private JComboBox<String> cbFilter;
    private JTable table;
    private DefaultTableModel tableModel;

    public AppointmentPanel(HospitalSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildFormCard(),  BorderLayout.CENTER);
        add(buildTableCard(), BorderLayout.SOUTH);
        refreshTable();
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.AMBER_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("\uD83D\uDCC5  Appointment Scheduling");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Book, cancel, or complete appointments  |  Date format: yyyy-MM-dd HH:mm");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 235, 180));
        JPanel txt = new JPanel(new GridLayout(2,1,0,2));
        txt.setOpaque(false); txt.add(title); txt.add(sub);
        hdr.add(txt, BorderLayout.CENTER);
        hdr.setPreferredSize(new Dimension(0, 68));
        return hdr;
    }

    private JPanel buildFormCard() {
        JPanel card = UIUtils.card("Appointment Details", UIUtils.AMBER_DARK);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        tfApptId    = UIUtils.makeField(10);
        tfPatientId = UIUtils.makeField(10);
        tfDoctorId  = UIUtils.makeField(10);
        tfDateTime  = UIUtils.makeField(16);
        tfDateTime.setToolTipText("yyyy-MM-dd HH:mm  e.g. 2026-09-15 10:30");
        tfNotes     = UIUtils.makeField(25);
        tfSearch    = UIUtils.makeField(14);

        g.gridy=0;
        addLF(card,g,0,"Appointment ID",  tfApptId);
        addLF(card,g,2,"Patient ID",      tfPatientId);
        addLF(card,g,4,"Doctor ID",       tfDoctorId);
        g.gridy=2;
        addLF(card,g,0,"Date & Time",     tfDateTime);
        addLF(card,g,2,"Notes (optional)",tfNotes);

        // Action buttons
        g.gridy=4; g.gridx=0;
        JButton btnBook     = UIUtils.makeButton("📅 Book",           UIUtils.GREEN);
        JButton btnCancel   = UIUtils.makeButton("❌ Cancel Selected", UIUtils.CORAL);
        JButton btnComplete = UIUtils.makeButton("✅ Mark Complete",   UIUtils.TEAL);
        JButton btnClear    = UIUtils.makeButton("✖ Clear",            UIUtils.TEXT_MID);
        JButton btnSearch   = UIUtils.makeButton("🔍 Search",           UIUtils.AMBER_DARK);

        card.add(btnBook, g);     g.gridx=1;
        card.add(btnCancel, g);   g.gridx=2;
        card.add(btnComplete, g); g.gridx=3;
        card.add(btnClear, g);    g.gridx=4;
        card.add(UIUtils.fieldLabel("Search ID:"), g); g.gridx=5;
        card.add(tfSearch, g);

        g.gridy=5; g.gridx=5;
        card.add(btnSearch, g);

        btnBook.addActionListener(e     -> bookAppointment());
        btnCancel.addActionListener(e   -> cancelSelected());
        btnComplete.addActionListener(e -> completeSelected());
        btnClear.addActionListener(e    -> clearForm());
        btnSearch.addActionListener(e   -> searchAppt());
        return card;
    }

    private void addLF(JPanel p, GridBagConstraints g, int col, String lbl, JTextField tf) {
        g.gridx=col;   p.add(UIUtils.fieldLabel(lbl), g);
        g.gridx=col+1; p.add(tf, g);
    }

    private JPanel buildTableCard() {
        JPanel card = UIUtils.card("Appointment List", UIUtils.AMBER_DARK);
        card.setLayout(new BorderLayout(6, 6));

        String[] cols = {"Appt ID","Patient ID","Doctor ID","Date & Time","Status","Notes"};
        table = UIUtils.makeTable(cols);
        tableModel = (DefaultTableModel) table.getModel();

        // Status colour renderer
        table.getColumnModel().getColumn(4).setCellRenderer((t, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel(String.valueOf(val));
            lbl.setOpaque(true); lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            String s = String.valueOf(val);
            switch (s) {
                case "BOOKED":    lbl.setBackground(new Color(200,230,255)); lbl.setForeground(UIUtils.INDIGO); break;
                case "COMPLETED": lbl.setBackground(new Color(200,240,210)); lbl.setForeground(UIUtils.GREEN);  break;
                case "CANCELLED": lbl.setBackground(new Color(255,220,220)); lbl.setForeground(UIUtils.CORAL_DARK); break;
                default:          lbl.setBackground(Color.WHITE);
            }
            if (sel) { lbl.setBackground(new Color(0,168,168,55)); lbl.setForeground(UIUtils.TEAL_DARK); }
            return lbl;
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                tfApptId.setText((String)    tableModel.getValueAt(r,0));
                tfPatientId.setText((String) tableModel.getValueAt(r,1));
                tfDoctorId.setText((String)  tableModel.getValueAt(r,2));
                tfDateTime.setText((String)  tableModel.getValueAt(r,3));
                tfNotes.setText((String)     tableModel.getValueAt(r,5));
            }
        });

        JScrollPane sp = UIUtils.scroll(table);
        sp.setPreferredSize(new Dimension(0, 200));
        card.add(sp, BorderLayout.CENTER);

        cbFilter = UIUtils.makeCombo(new String[]{"All","BOOKED","CANCELLED","COMPLETED"});
        cbFilter.addActionListener(e -> refreshTable());
        JButton btnRefresh = UIUtils.makeButton("🔄 Refresh", UIUtils.AMBER_DARK);
        btnRefresh.addActionListener(e -> refreshTable());

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bot.setOpaque(false);
        bot.add(UIUtils.fieldLabel("Filter:"));
        bot.add(cbFilter); bot.add(btnRefresh);
        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    // ── Actions ────────────────────────────────────────────────────────────
    private void bookAppointment() {
        try {
            String id    = UIUtils.requireText(tfApptId,    "Appointment ID");
            String pid   = UIUtils.requireText(tfPatientId, "Patient ID");
            String did   = UIUtils.requireText(tfDoctorId,  "Doctor ID");
            String dtStr = UIUtils.requireText(tfDateTime,  "Date & Time");
            LocalDateTime dt = LocalDateTime.parse(dtStr, Appointment.FORMATTER);
            Appointment a = new Appointment(id, pid, did, dt);
            a.setNotes(tfNotes.getText().trim());
            system.bookAppointment(a);
            UIUtils.showInfo(this, "Appointment booked: " + id);
            clearForm(); refreshTable();
        } catch (DateTimeParseException e) {
            UIUtils.showError(this, "Invalid date format. Use: yyyy-MM-dd HH:mm");
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void cancelSelected() {
        String id = selectedId(); if (id == null) return;
        if (!UIUtils.confirm(this, "Cancel appointment " + id + "?")) return;
        try { system.cancelAppointment(id); refreshTable(); }
        catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void completeSelected() {
        String id = selectedId(); if (id == null) return;
        if (!UIUtils.confirm(this, "Mark " + id + " as COMPLETED?")) return;
        try { system.completeAppointment(id); refreshTable(); }
        catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void searchAppt() {
        String q = tfSearch.getText().trim();
        if (q.isEmpty()) { refreshTable(); return; }
        tableModel.setRowCount(0);
        Appointment a = system.findAppointmentById(q);
        if (a == null) UIUtils.showError(this, "No appointment found: " + q);
        else addRow(a);
    }

    private String selectedId() {
        int r = table.getSelectedRow();
        if (r < 0) { UIUtils.showError(this, "Select an appointment from the table."); return null; }
        return (String) tableModel.getValueAt(r, 0);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String f = (String) cbFilter.getSelectedItem();
        for (Appointment a : system.listAppointments())
            if ("All".equals(f) || a.getStatus().name().equals(f)) addRow(a);
    }

    private void addRow(Appointment a) {
        tableModel.addRow(new Object[]{
            a.getAppointmentId(), a.getPatientId(), a.getDoctorId(),
            a.getDateTimeFormatted(), a.getStatus().name(), a.getNotes()
        });
    }

    private void clearForm() {
        for (JTextField tf : new JTextField[]{tfApptId,tfPatientId,tfDoctorId,tfDateTime,tfNotes,tfSearch})
            tf.setText("");
        table.clearSelection();
    }
}
