package hms.ui;

import hms.model.Patient;
import hms.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Patient management panel — teal accent theme.
 */
public class PatientPanel extends JPanel {
    private final HospitalSystem system;

    private JTextField tfId, tfName, tfPhone, tfAge, tfGender, tfAddress, tfSearch;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientPanel(HospitalSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildFormCard(),  BorderLayout.CENTER);
        add(buildTableCard(), BorderLayout.SOUTH);
        refreshTable();
    }

    // ── Coloured header bar ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.TEAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("\uD83D\uDC65  Patient Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Register, update and search patients");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(200, 240, 240));
        JPanel txt = new JPanel(new GridLayout(2,1,0,2));
        txt.setOpaque(false);
        txt.add(title); txt.add(sub);
        hdr.add(txt, BorderLayout.CENTER);
        hdr.setPreferredSize(new Dimension(0, 68));
        return hdr;
    }

    // ── Form card ─────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = UIUtils.card("Patient Details", UIUtils.TEAL);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        tfId      = UIUtils.makeField(10);
        tfName    = UIUtils.makeField(16);
        tfPhone   = UIUtils.makeField(12);
        tfAge     = UIUtils.makeField(5);
        tfGender  = UIUtils.makeField(8);
        tfAddress = UIUtils.makeField(22);
        tfSearch  = UIUtils.makeField(14);

        // Row 0 — labels
        addLabelAndField(card, g, 0, 0, "Patient ID",    tfId);
        addLabelAndField(card, g, 0, 2, "Full Name",     tfName);
        addLabelAndField(card, g, 0, 4, "Phone",         tfPhone);
        // Row 2
        addLabelAndField(card, g, 2, 0, "Age",           tfAge);
        addLabelAndField(card, g, 2, 2, "Gender",        tfGender);
        addLabelAndField(card, g, 2, 4, "Address",       tfAddress);

        // Buttons row
        g.gridy=4; g.gridx=0; g.gridwidth=1;
        JButton btnAdd    = UIUtils.makeButton("➕ Add Patient",    UIUtils.GREEN);
        JButton btnUpdate = UIUtils.makeButton("✏ Update",          UIUtils.INDIGO);
        JButton btnClear  = UIUtils.makeButton("✖ Clear",           UIUtils.TEXT_MID);
        JButton btnSearch = UIUtils.makeButton("🔍 Search",          UIUtils.AMBER);

        card.add(btnAdd,  g); g.gridx=1;
        card.add(btnUpdate, g); g.gridx=2;
        card.add(btnClear, g); g.gridx=3;
        card.add(UIUtils.fieldLabel("Search ID/Name:"), g); g.gridx=4;
        card.add(tfSearch, g); g.gridx=5;
        card.add(btnSearch, g);

        btnAdd.addActionListener(e    -> addPatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnClear.addActionListener(e  -> clearForm());
        btnSearch.addActionListener(e -> searchPatient());

        return card;
    }

    private void addLabelAndField(JPanel p, GridBagConstraints g,
                                  int row, int col, String lbl, JTextField tf) {
        g.gridy=row;   g.gridx=col;   g.gridwidth=1;
        p.add(UIUtils.fieldLabel(lbl), g);
        g.gridx=col+1;
        p.add(tf, g);
    }

    // ── Table card ────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = UIUtils.card("Patient List", UIUtils.TEAL);
        card.setLayout(new BorderLayout(6, 6));

        String[] cols = {"ID","Name","Age","Gender","Phone","Address"};
        table = UIUtils.makeTable(cols);
        tableModel = (DefaultTableModel) table.getModel();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                tfId.setText((String)  tableModel.getValueAt(r,0));
                tfName.setText((String) tableModel.getValueAt(r,1));
                tfAge.setText(String.valueOf(tableModel.getValueAt(r,2)));
                tfGender.setText((String)  tableModel.getValueAt(r,3));
                tfPhone.setText((String)   tableModel.getValueAt(r,4));
                tfAddress.setText((String) tableModel.getValueAt(r,5));
            }
        });

        JScrollPane sp = UIUtils.scroll(table);
        sp.setPreferredSize(new Dimension(0, 200));
        card.add(sp, BorderLayout.CENTER);

        JButton btnRefresh = UIUtils.makeButton("🔄 Refresh", UIUtils.TEAL);
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        bot.setOpaque(false); bot.add(btnRefresh);
        card.add(bot, BorderLayout.SOUTH);
        btnRefresh.addActionListener(e -> refreshTable());

        return card;
    }

    // ── Actions ────────────────────────────────────────────────────────────
    private void addPatient() {
        try {
            system.addPatient(buildFromForm());
            UIUtils.showInfo(this, "Patient added successfully.");
            clearForm(); refreshTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void updatePatient() {
        try {
            system.updatePatient(buildFromForm());
            UIUtils.showInfo(this, "Patient updated successfully.");
            clearForm(); refreshTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private Patient buildFromForm() {
        return new Patient(
            UIUtils.requireText(tfId,      "Patient ID"),
            UIUtils.requireText(tfName,    "Name"),
            UIUtils.requireText(tfPhone,   "Phone"),
            UIUtils.parseInt(tfAge,        "Age"),
            UIUtils.requireText(tfGender,  "Gender"),
            UIUtils.requireText(tfAddress, "Address")
        );
    }

    private void searchPatient() {
        String q = tfSearch.getText().trim();
        if (q.isEmpty()) { refreshTable(); return; }
        tableModel.setRowCount(0);
        Patient p = system.findPatientById(q);
        if (p != null) { addRow(p); return; }
        List<Patient> found = system.searchPatientsByName(q);
        if (found.isEmpty()) UIUtils.showError(this, "No patients found for: " + q);
        else found.forEach(this::addRow);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        system.listPatients().forEach(this::addRow);
    }

    private void addRow(Patient p) {
        tableModel.addRow(new Object[]{
            p.getId(), p.getName(), p.getAge(), p.getGender(), p.getPhone(), p.getAddress()
        });
    }

    private void clearForm() {
        for (JTextField tf : new JTextField[]{tfId,tfName,tfPhone,tfAge,tfGender,tfAddress,tfSearch})
            tf.setText("");
        table.clearSelection();
    }
}
