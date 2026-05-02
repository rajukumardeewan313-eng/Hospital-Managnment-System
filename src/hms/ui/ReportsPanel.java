package hms.ui;

import hms.billing.Bill;
import hms.model.Appointment;
import hms.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Reports panel — stats dashboard with coloured stat cards.
 */
public class ReportsPanel extends JPanel {
    private final HospitalSystem system;

    private JLabel lblPatients, lblDoctors, lblBooked, lblCompleted, lblUnpaid, lblRevenue;
    private JTable unpaidTable, doctorApptTable;
    private DefaultTableModel unpaidModel, doctorApptModel;
    private JTextField tfDoctorId;

    public ReportsPanel(HospitalSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 70));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("\uD83D\uDCCA  Reports & Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("System-wide statistics and quick reports");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 200, 230));
        JPanel txt = new JPanel(new GridLayout(2,1,0,2));
        txt.setOpaque(false); txt.add(title); txt.add(sub);
        hdr.add(txt, BorderLayout.CENTER);

        JButton btnRefresh = UIUtils.makeButton("🔄 Refresh All", UIUtils.TEAL);
        btnRefresh.addActionListener(e -> refresh());
        hdr.add(btnRefresh, BorderLayout.EAST);
        hdr.setPreferredSize(new Dimension(0, 68));
        return hdr;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setOpaque(false);
        body.add(buildStatCards(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildUnpaidCard(), buildDoctorCard());
        split.setDividerLocation(500);
        split.setBackground(UIUtils.BG_MAIN);
        body.add(split, BorderLayout.CENTER);
        return body;
    }

    // ── Stat cards row ─────────────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 6, 10, 0));
        row.setOpaque(false);

        lblPatients  = new JLabel("0", SwingConstants.CENTER);
        lblDoctors   = new JLabel("0", SwingConstants.CENTER);
        lblBooked    = new JLabel("0", SwingConstants.CENTER);
        lblCompleted = new JLabel("0", SwingConstants.CENTER);
        lblUnpaid    = new JLabel("0", SwingConstants.CENTER);
        lblRevenue   = new JLabel("$0", SwingConstants.CENTER);

        row.add(statCard("Patients",     lblPatients,  UIUtils.TEAL));
        row.add(statCard("Doctors",      lblDoctors,   UIUtils.INDIGO));
        row.add(statCard("Booked",       lblBooked,    UIUtils.AMBER_DARK));
        row.add(statCard("Completed",    lblCompleted, UIUtils.GREEN));
        row.add(statCard("Unpaid Bills", lblUnpaid,    UIUtils.CORAL));
        row.add(statCard("Revenue",      lblRevenue,   new Color(30, 40, 70)));

        row.setPreferredSize(new Dimension(0, 100));
        return row;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLbl.setForeground(UIUtils.TEXT_MID);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(accent);

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Unpaid Bills card ──────────────────────────────────────────────────
    private JPanel buildUnpaidCard() {
        JPanel card = UIUtils.card("Unpaid Bills", UIUtils.CORAL);
        card.setLayout(new BorderLayout(6,6));

        String[] cols = {"Bill ID","Appointment ID","Amount ($)"};
        unpaidTable = UIUtils.makeTable(cols);
        unpaidModel = (DefaultTableModel) unpaidTable.getModel();
        card.add(UIUtils.scroll(unpaidTable), BorderLayout.CENTER);
        return card;
    }

    // ── Doctor Schedule card ───────────────────────────────────────────────
    private JPanel buildDoctorCard() {
        JPanel card = UIUtils.card("Doctor-wise Appointment Schedule", UIUtils.INDIGO);
        card.setLayout(new BorderLayout(6,6));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setOpaque(false);
        tfDoctorId = UIUtils.makeField(12);
        JButton btn = UIUtils.makeButton("🔍 Load Schedule", UIUtils.INDIGO);
        searchRow.add(UIUtils.fieldLabel("Doctor ID:")); searchRow.add(tfDoctorId);
        searchRow.add(btn);
        btn.addActionListener(e -> loadDoctorSchedule());

        String[] cols = {"Appt ID","Patient ID","Date & Time","Status"};
        doctorApptTable = UIUtils.makeTable(cols);
        doctorApptModel = (DefaultTableModel) doctorApptTable.getModel();

        card.add(searchRow,                   BorderLayout.NORTH);
        card.add(UIUtils.scroll(doctorApptTable), BorderLayout.CENTER);
        return card;
    }

    // ── Data refresh ───────────────────────────────────────────────────────
    private void refresh() {
        int patients  = system.listPatients().size();
        int doctors   = system.listDoctors().size();
        List<Appointment> appts = system.listAppointments();
        long booked    = appts.stream().filter(a -> a.getStatus() == Appointment.Status.BOOKED).count();
        long completed = appts.stream().filter(a -> a.getStatus() == Appointment.Status.COMPLETED).count();
        List<Bill> unpaid = system.listUnpaidBills();
        double revenue = system.listBills().stream()
                .filter(b -> b.getStatus() == Bill.Status.PAID)
                .mapToDouble(Bill::getTotalAmount).sum();

        lblPatients.setText(String.valueOf(patients));
        lblDoctors.setText(String.valueOf(doctors));
        lblBooked.setText(String.valueOf(booked));
        lblCompleted.setText(String.valueOf(completed));
        lblUnpaid.setText(String.valueOf(unpaid.size()));
        lblRevenue.setText("$" + String.format("%.0f", revenue));

        unpaidModel.setRowCount(0);
        for (Bill b : unpaid)
            unpaidModel.addRow(new Object[]{
                b.getBillId(), b.getAppointmentId(),
                String.format("%.2f", b.getTotalAmount())
            });
    }

    private void loadDoctorSchedule() {
        String did = tfDoctorId.getText().trim();
        if (did.isEmpty()) { UIUtils.showError(this, "Enter a Doctor ID."); return; }
        if (system.findDoctorById(did) == null) {
            UIUtils.showError(this, "Doctor not found: " + did); return; }
        doctorApptModel.setRowCount(0);
        system.listAppointmentsForDoctor(did).forEach(a ->
            doctorApptModel.addRow(new Object[]{
                a.getAppointmentId(), a.getPatientId(),
                a.getDateTimeFormatted(), a.getStatus().name()
            })
        );
    }
}
