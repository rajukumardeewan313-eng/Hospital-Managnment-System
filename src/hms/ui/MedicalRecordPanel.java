package hms.ui;

import hms.model.Appointment;
import hms.model.MedicalRecord;
import hms.model.PrescriptionItem;
import hms.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Medical Records panel — purple / green accent theme.
 */
public class MedicalRecordPanel extends JPanel {
    private final HospitalSystem system;

    private JTextField tfRecordId, tfApptId, tfDiagnosis, tfNotes;
    private JTextField tfMed, tfDose, tfDays, tfInstr;
    private JTextField tfSearchPid;

    private JTable recordTable, prescTable;
    private DefaultTableModel recordModel, prescModel;
    private ArrayList<PrescriptionItem> pendingPrescs = new ArrayList<>();

    public MedicalRecordPanel(HospitalSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(440);
        split.setBackground(UIUtils.BG_MAIN);
        add(split, BorderLayout.CENTER);
        refreshRecordTable();
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("\uD83D\uDCCB  Medical Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Add diagnosis, prescriptions and view patient history");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(220, 200, 255));
        JPanel txt = new JPanel(new GridLayout(2,1,0,2));
        txt.setOpaque(false); txt.add(title); txt.add(sub);
        hdr.add(txt, BorderLayout.CENTER);
        hdr.setPreferredSize(new Dimension(0, 68));
        return hdr;
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(UIUtils.BG_MAIN);

        // Record form card
        JPanel recCard = UIUtils.card("New Medical Record", UIUtils.PURPLE);
        recCard.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,8,5,8); g.fill = GridBagConstraints.HORIZONTAL;

        tfRecordId  = UIUtils.makeField(10);
        tfApptId    = UIUtils.makeField(10);
        tfDiagnosis = UIUtils.makeField(22);
        tfNotes     = UIUtils.makeField(22);

        g.gridy=0; addLF(recCard,g,0,"Record ID",    tfRecordId);
                   addLF(recCard,g,2,"Appointment ID",tfApptId);
        g.gridy=2; addLF(recCard,g,0,"Diagnosis",     tfDiagnosis);
        g.gridy=4; addLF(recCard,g,0,"Clinical Notes",tfNotes);

        // Prescription form card
        JPanel prescCard = UIUtils.card("Add Prescription Item", UIUtils.GREEN);
        prescCard.setLayout(new GridBagLayout());
        GridBagConstraints gp = new GridBagConstraints();
        gp.insets = new Insets(5,8,5,8); gp.fill = GridBagConstraints.HORIZONTAL;

        tfMed   = UIUtils.makeField(14);
        tfDose  = UIUtils.makeField(8);
        tfDays  = UIUtils.makeField(5);
        tfInstr = UIUtils.makeField(16);

        gp.gridy=0; addLF(prescCard,gp,0,"Medicine Name", tfMed);
                    addLF(prescCard,gp,2,"Dose",           tfDose);
        gp.gridy=2; addLF(prescCard,gp,0,"Days",          tfDays);
                    addLF(prescCard,gp,2,"Instructions",   tfInstr);

        gp.gridy=4; gp.gridx=0;
        JButton btnAddPresc = UIUtils.makeButton("➕ Add to Prescription List", UIUtils.GREEN);
        prescCard.add(btnAddPresc, gp);
        btnAddPresc.addActionListener(e -> addPrescription());

        // Pending prescription preview
        JPanel prevCard = UIUtils.card("Prescriptions in this Record", UIUtils.GREEN);
        prevCard.setLayout(new BorderLayout(4,4));
        String[] pcols = {"Medicine","Dose","Days","Instructions"};
        prescTable = UIUtils.makeTable(pcols);
        prescModel = (DefaultTableModel) prescTable.getModel();
        JScrollPane prescScroll = UIUtils.scroll(prescTable);
        prescScroll.setPreferredSize(new Dimension(0, 110));
        prevCard.add(prescScroll, BorderLayout.CENTER);

        // Save & Clear buttons
        JButton btnSave  = UIUtils.makeButton("💾 Save Medical Record", UIUtils.PURPLE);
        JButton btnClear = UIUtils.makeButton("✖ Clear All",             UIUtils.TEXT_MID);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setOpaque(false); btns.add(btnSave); btns.add(btnClear);
        btnSave.addActionListener(e  -> saveMedicalRecord());
        btnClear.addActionListener(e -> clearAll());

        left.add(recCard,   BorderLayout.NORTH);
        JPanel mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setOpaque(false);
        mid.add(prescCard); mid.add(Box.createVerticalStrut(6)); mid.add(prevCard);
        left.add(mid,  BorderLayout.CENTER);
        left.add(btns, BorderLayout.SOUTH);
        return left;
    }

    private void addLF(JPanel p, GridBagConstraints g, int col, String lbl, JTextField tf) {
        g.gridx=col;   p.add(UIUtils.fieldLabel(lbl), g);
        g.gridx=col+1; p.add(tf, g);
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(UIUtils.BG_MAIN);

        JPanel searchCard = UIUtils.card("View Records by Patient", UIUtils.PURPLE);
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        tfSearchPid = UIUtils.makeField(14);
        JButton btnSearch = UIUtils.makeButton("🔍 Search", UIUtils.PURPLE);
        JButton btnAll    = UIUtils.makeButton("📋 Show All", UIUtils.TEXT_MID);
        searchCard.add(UIUtils.fieldLabel("Patient ID:")); searchCard.add(tfSearchPid);
        searchCard.add(btnSearch); searchCard.add(btnAll);
        btnSearch.addActionListener(e -> searchByPatient());
        btnAll.addActionListener(e    -> refreshRecordTable());

        String[] cols = {"Record ID","Appt ID","Diagnosis","Notes","Rx Items"};
        recordTable = UIUtils.makeTable(cols);
        recordModel = (DefaultTableModel) recordTable.getModel();

        JPanel tableCard = UIUtils.card("All Medical Records", UIUtils.PURPLE);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(UIUtils.scroll(recordTable), BorderLayout.CENTER);

        right.add(searchCard, BorderLayout.NORTH);
        right.add(tableCard,  BorderLayout.CENTER);
        return right;
    }

    private void addPrescription() {
        try {
            String med   = UIUtils.requireText(tfMed,  "Medicine name");
            String dose  = UIUtils.requireText(tfDose, "Dose");
            int    days  = UIUtils.parseInt(tfDays,    "Days");
            String instr = tfInstr.getText().trim();
            PrescriptionItem pi = new PrescriptionItem(med, dose, days, instr);
            pendingPrescs.add(pi);
            prescModel.addRow(new Object[]{med, dose, days, instr});
            for (JTextField tf : new JTextField[]{tfMed,tfDose,tfDays,tfInstr}) tf.setText("");
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void saveMedicalRecord() {
        try {
            String apptId = UIUtils.requireText(tfApptId, "Appointment ID");
            Appointment appt = system.findAppointmentById(apptId);
            if (appt == null) throw new IllegalArgumentException("Appointment not found.");
            
            MedicalRecord rec = new MedicalRecord(
                UIUtils.requireText(tfRecordId,  "Record ID"),
                apptId,
                appt.getPatientId(),
                UIUtils.requireText(tfDiagnosis, "Diagnosis"),
                tfNotes.getText().trim()
            );
            for (PrescriptionItem pi : pendingPrescs) rec.addPrescription(pi);
            system.addMedicalRecord(rec);
            UIUtils.showInfo(this, "Medical record saved successfully.");
            clearAll(); refreshRecordTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void searchByPatient() {
        String pid = tfSearchPid.getText().trim();
        if (pid.isEmpty()) { refreshRecordTable(); return; }
        recordModel.setRowCount(0);
        List<MedicalRecord> recs = system.listRecordsByPatient(pid);
        if (recs.isEmpty()) UIUtils.showError(this, "No records for patient: " + pid);
        else recs.forEach(this::addRecordRow);
    }

    private void refreshRecordTable() {
        recordModel.setRowCount(0);
        system.listAllRecords().forEach(this::addRecordRow);
    }

    private void addRecordRow(MedicalRecord r) {
        recordModel.addRow(new Object[]{
            r.getRecordId(), r.getAppointmentId(), r.getDiagnosis(),
            r.getNotes(), r.getPrescriptions().size() + " item(s)"
        });
    }

    private void clearAll() {
        for (JTextField tf : new JTextField[]{tfRecordId,tfApptId,tfDiagnosis,
                                              tfNotes,tfMed,tfDose,tfDays,tfInstr})
            tf.setText("");
        pendingPrescs.clear();
        prescModel.setRowCount(0);
    }
}
