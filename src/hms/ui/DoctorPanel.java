package hms.ui;

import hms.model.Doctor;
import hms.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Doctor management panel — indigo/purple accent theme.
 */
public class DoctorPanel extends JPanel {
    private final HospitalSystem system;

    private JTextField tfId, tfName, tfPhone, tfSpec, tfFee, tfSearch;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorPanel(HospitalSystem system) {
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
                g2.setColor(UIUtils.INDIGO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("\uD83D\uDC68\u200D\u2695\uFE0F  Doctor Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Register and manage hospital doctors");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(200, 210, 255));
        JPanel txt = new JPanel(new GridLayout(2,1,0,2));
        txt.setOpaque(false); txt.add(title); txt.add(sub);
        hdr.add(txt, BorderLayout.CENTER);
        hdr.setPreferredSize(new Dimension(0, 68));
        return hdr;
    }

    private JPanel buildFormCard() {
        JPanel card = UIUtils.card("Doctor Details", UIUtils.INDIGO);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        tfId     = UIUtils.makeField(10);
        tfName   = UIUtils.makeField(16);
        tfPhone  = UIUtils.makeField(12);
        tfSpec   = UIUtils.makeField(16);
        tfFee    = UIUtils.makeField(10);
        tfSearch = UIUtils.makeField(14);

        g.gridy=0;
        addLF(card,g,0,"Doctor ID",       tfId);
        addLF(card,g,2,"Full Name",        tfName);
        addLF(card,g,4,"Phone",            tfPhone);
        g.gridy=2;
        addLF(card,g,0,"Specialization",   tfSpec);
        addLF(card,g,2,"Consultation Fee ($)", tfFee);

        g.gridy=4; g.gridx=0;
        JButton btnAdd    = UIUtils.makeButton("➕ Add Doctor",    UIUtils.GREEN);
        JButton btnUpdate = UIUtils.makeButton("✏ Update",          UIUtils.INDIGO);
        JButton btnClear  = UIUtils.makeButton("✖ Clear",           UIUtils.TEXT_MID);
        JButton btnSearch = UIUtils.makeButton("🔍 Search",          UIUtils.AMBER);

        card.add(btnAdd, g);    g.gridx=1;
        card.add(btnUpdate, g); g.gridx=2;
        card.add(btnClear, g);  g.gridx=3;
        card.add(UIUtils.fieldLabel("Search:"), g); g.gridx=4;
        card.add(tfSearch, g);  g.gridx=5;
        card.add(btnSearch, g);

        btnAdd.addActionListener(e    -> addDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnClear.addActionListener(e  -> clearForm());
        btnSearch.addActionListener(e -> searchDoctor());
        return card;
    }

    private void addLF(JPanel p, GridBagConstraints g, int col, String lbl, JTextField tf) {
        g.gridx=col;   g.gridwidth=1; p.add(UIUtils.fieldLabel(lbl), g);
        g.gridx=col+1;               p.add(tf, g);
    }

    private JPanel buildTableCard() {
        JPanel card = UIUtils.card("Doctor List", UIUtils.INDIGO);
        card.setLayout(new BorderLayout(6, 6));

        String[] cols = {"ID","Name","Specialization","Fee ($)","Phone"};
        table = UIUtils.makeTable(cols);
        tableModel = (DefaultTableModel) table.getModel();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                tfId.setText((String)    tableModel.getValueAt(r,0));
                tfName.setText((String)  tableModel.getValueAt(r,1));
                tfSpec.setText((String)  tableModel.getValueAt(r,2));
                tfFee.setText(String.valueOf(tableModel.getValueAt(r,3)));
                tfPhone.setText((String) tableModel.getValueAt(r,4));
            }
        });

        JScrollPane sp = UIUtils.scroll(table);
        sp.setPreferredSize(new Dimension(0, 200));
        card.add(sp, BorderLayout.CENTER);

        JButton btnRefresh = UIUtils.makeButton("🔄 Refresh", UIUtils.INDIGO);
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        bot.setOpaque(false); bot.add(btnRefresh);
        card.add(bot, BorderLayout.SOUTH);
        btnRefresh.addActionListener(e -> refreshTable());
        return card;
    }

    private void addDoctor() {
        try {
            system.addDoctor(buildFromForm());
            UIUtils.showInfo(this, "Doctor added successfully.");
            clearForm(); refreshTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void updateDoctor() {
        try {
            system.updateDoctor(buildFromForm());
            UIUtils.showInfo(this, "Doctor updated successfully.");
            clearForm(); refreshTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private Doctor buildFromForm() {
        return new Doctor(
            UIUtils.requireText(tfId,    "Doctor ID"),
            UIUtils.requireText(tfName,  "Name"),
            UIUtils.requireText(tfPhone, "Phone"),
            UIUtils.requireText(tfSpec,  "Specialization"),
            UIUtils.parseDouble(tfFee,   "Consultation Fee")
        );
    }

    private void searchDoctor() {
        String q = tfSearch.getText().trim();
        if (q.isEmpty()) { refreshTable(); return; }
        tableModel.setRowCount(0);
        Doctor d = system.findDoctorById(q);
        if (d != null) { addRow(d); return; }
        List<Doctor> found = system.searchDoctorsByName(q);
        if (found.isEmpty()) UIUtils.showError(this, "No doctors found for: " + q);
        else found.forEach(this::addRow);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        system.listDoctors().forEach(this::addRow);
    }

    private void addRow(Doctor d) {
        tableModel.addRow(new Object[]{
            d.getId(), d.getName(), d.getSpecialization(),
            String.format("%.2f", d.getConsultationFee()), d.getPhone()
        });
    }

    private void clearForm() {
        for (JTextField tf : new JTextField[]{tfId,tfName,tfPhone,tfSpec,tfFee,tfSearch})
            tf.setText("");
        table.clearSelection();
    }
}
