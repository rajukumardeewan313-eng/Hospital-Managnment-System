package hms.ui;

import hms.billing.Bill;
import hms.billing.BillItem;
import hms.billing.BillItemFactory;
import hms.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Billing panel — coral / green accent theme.
 * POLYMORPHISM is demonstrated: different BillItem types stored and totalled together.
 */
public class BillingPanel extends JPanel {
    private final HospitalSystem system;

    private JTextField tfBillId, tfApptId;
    private JTextField tfBillIdItem, tfP1, tfP2, tfP3;
    private JTextField tfBillIdPay;
    private JComboBox<String> cbItemType;

    private JTable billTable, itemTable;
    private DefaultTableModel billModel, itemModel;

    public BillingPanel(HospitalSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildTopForms(), buildBottomTables());
        split.setDividerLocation(240);
        split.setBackground(UIUtils.BG_MAIN);
        add(split, BorderLayout.CENTER);
        refreshBillTable();
    }

    // ── Header ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.CORAL_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("\uD83D\uDCB0  Billing & Payments");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Generate bills, add items (polymorphism demo), and mark paid");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 200, 200));
        JPanel txt = new JPanel(new GridLayout(2,1,0,2));
        txt.setOpaque(false); txt.add(title); txt.add(sub);
        hdr.add(txt, BorderLayout.CENTER);
        hdr.setPreferredSize(new Dimension(0, 68));
        return hdr;
    }

    // ── Top forms (3 cards in a row) ───────────────────────────────────────
    private JPanel buildTopForms() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setOpaque(false);
        row.add(buildGenCard());
        row.add(buildAddItemCard());
        row.add(buildPayCard());
        return row;
    }

    private JPanel buildGenCard() {
        JPanel card = UIUtils.card("1.  Generate Bill", UIUtils.CORAL);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,8,6,8); g.fill = GridBagConstraints.HORIZONTAL;

        tfBillId = UIUtils.makeField(12);
        tfApptId = UIUtils.makeField(12);

        g.gridy=0; g.gridx=0; card.add(UIUtils.fieldLabel("Bill ID:"), g);
        g.gridx=1; card.add(tfBillId, g);
        g.gridy=1; g.gridx=0; card.add(UIUtils.fieldLabel("Appointment ID:"), g);
        g.gridx=1; card.add(tfApptId, g);
        g.gridy=2; g.gridx=0; g.gridwidth=2;
        JButton btn = UIUtils.makeButton("🧾 Generate Bill", UIUtils.CORAL);
        card.add(btn, g);
        btn.addActionListener(e -> generateBill());
        return card;
    }

    private JPanel buildAddItemCard() {
        JPanel card = UIUtils.card("2.  Add Bill Item  (Polymorphism)", UIUtils.AMBER_DARK);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,8,5,8); g.fill = GridBagConstraints.HORIZONTAL;

        tfBillIdItem = UIUtils.makeField(10);
        cbItemType   = UIUtils.makeCombo(new String[]{"Consultation Fee","Lab Test","Medicine Charge"});
        tfP1 = UIUtils.makeField(12);
        tfP2 = UIUtils.makeField(8);
        tfP3 = UIUtils.makeField(5);
        tfP3.setEnabled(false);
        tfP3.setToolTipText("Quantity — only for Medicine Charge");

        JLabel lblP1 = UIUtils.fieldLabel("Doctor Name:");
        JLabel lblP2 = UIUtils.fieldLabel("Fee ($):");
        JLabel lblP3 = UIUtils.fieldLabel("Qty:");

        cbItemType.addActionListener(e -> {
            String t = (String) cbItemType.getSelectedItem();
            switch (t) {
                case "Consultation Fee":
                    lblP1.setText("Doctor Name:"); lblP2.setText("Fee ($):"); tfP3.setEnabled(false); break;
                case "Lab Test":
                    lblP1.setText("Test Name:");   lblP2.setText("Cost ($):"); tfP3.setEnabled(false); break;
                case "Medicine Charge":
                    lblP1.setText("Medicine:");    lblP2.setText("Unit Price ($):"); tfP3.setEnabled(true); break;
            }
        });

        g.gridy=0; g.gridx=0; card.add(UIUtils.fieldLabel("Bill ID:"), g);
        g.gridx=1; card.add(tfBillIdItem, g);
        g.gridx=2; card.add(UIUtils.fieldLabel("Item Type:"), g);
        g.gridx=3; card.add(cbItemType, g);

        g.gridy=1; g.gridx=0; card.add(lblP1, g); g.gridx=1; card.add(tfP1, g);
        g.gridx=2; card.add(lblP2, g); g.gridx=3; card.add(tfP2, g);
        g.gridx=4; card.add(lblP3, g); g.gridx=5; card.add(tfP3, g);

        g.gridy=2; g.gridx=0; g.gridwidth=6;
        JButton btn = UIUtils.makeButton("➕ Add Item", UIUtils.AMBER_DARK);
        card.add(btn, g);
        btn.addActionListener(e -> addItem());
        return card;
    }

    private JPanel buildPayCard() {
        JPanel card = UIUtils.card("3.  Mark Bill Paid", UIUtils.GREEN);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,8,6,8); g.fill = GridBagConstraints.HORIZONTAL;

        tfBillIdPay = UIUtils.makeField(12);

        g.gridy=0; g.gridx=0; card.add(UIUtils.fieldLabel("Bill ID:"), g);
        g.gridx=1; card.add(tfBillIdPay, g);

        g.gridy=1; g.gridx=0; g.gridwidth=2;
        JButton btnPay   = UIUtils.makeButton("✅ Mark as PAID",    UIUtils.GREEN);
        card.add(btnPay, g);

        g.gridy=2;
        JButton btnItems = UIUtils.makeButton("👁 View Bill Items",  UIUtils.INDIGO);
        card.add(btnItems, g);

        btnPay.addActionListener(e   -> markPaid());
        btnItems.addActionListener(e -> viewItems());
        return card;
    }

    // ── Bottom tables ──────────────────────────────────────────────────────
    private JPanel buildBottomTables() {
        // Bills table
        String[] billCols = {"Bill ID","Appointment ID","Total ($)","Status"};
        billTable = UIUtils.makeTable(billCols);
        billModel = (DefaultTableModel) billTable.getModel();

        // Status colour renderer for bills
        billTable.getColumnModel().getColumn(3).setCellRenderer((t, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel(String.valueOf(val));
            lbl.setOpaque(true); lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            if ("PAID".equals(val)) {
                lbl.setBackground(new Color(200,240,210)); lbl.setForeground(UIUtils.GREEN);
            } else {
                lbl.setBackground(new Color(255,220,200)); lbl.setForeground(UIUtils.CORAL_DARK);
            }
            if (sel) { lbl.setBackground(new Color(0,168,168,55)); lbl.setForeground(UIUtils.TEAL_DARK); }
            return lbl;
        });

        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && billTable.getSelectedRow() >= 0) {
                String bid = (String) billModel.getValueAt(billTable.getSelectedRow(), 0);
                tfBillIdItem.setText(bid);
                tfBillIdPay.setText(bid);
                loadItemsForBill(bid);
            }
        });

        // Items table
        String[] itemCols = {"Description","Cost ($)"};
        itemTable = UIUtils.makeTable(itemCols);
        itemModel = (DefaultTableModel) itemTable.getModel();

        // Cards wrapping tables
        JPanel billCard = UIUtils.card("Bills  (click a row to view its items)", UIUtils.CORAL);
        billCard.setLayout(new BorderLayout(4,4));
        billCard.add(UIUtils.scroll(billTable), BorderLayout.CENTER);
        JButton btnRefresh = UIUtils.makeButton("🔄 Refresh", UIUtils.CORAL);
        btnRefresh.addActionListener(e -> refreshBillTable());
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT,6,4));
        bot.setOpaque(false); bot.add(btnRefresh);
        billCard.add(bot, BorderLayout.SOUTH);

        JPanel itemCard = UIUtils.card("Bill Items & Total", UIUtils.AMBER_DARK);
        itemCard.setLayout(new BorderLayout());
        itemCard.add(UIUtils.scroll(itemTable), BorderLayout.CENTER);

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, billCard, itemCard);
        sp.setDividerLocation(550);
        sp.setBackground(UIUtils.BG_MAIN);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Actions ────────────────────────────────────────────────────────────
    private void generateBill() {
        try {
            String bid  = UIUtils.requireText(tfBillId, "Bill ID");
            String appt = UIUtils.requireText(tfApptId, "Appointment ID");
            system.generateBillForAppointment(bid, appt);
            UIUtils.showInfo(this, "Bill " + bid + " generated.");
            tfBillId.setText(""); tfApptId.setText("");
            refreshBillTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void addItem() {
        try {
            String bid  = UIUtils.requireText(tfBillIdItem, "Bill ID");
            String type = (String) cbItemType.getSelectedItem();
            String p1   = UIUtils.requireText(tfP1, "Description/Name");
            double p2   = UIUtils.parseDouble(tfP2, "Fee/Cost");

            BillItem item;
            switch (type) {
                case "Consultation Fee": item = BillItemFactory.createConsultationFee(p1, p2); break;
                case "Lab Test":         item = BillItemFactory.createLabTest(p1, p2); break;
                default:
                    int qty = UIUtils.parseInt(tfP3, "Quantity");
                    item = BillItemFactory.createMedicineCharge(p1, p2, qty);
            }

            system.addBillItem(bid, item);
            Bill b = system.findBillById(bid);
            UIUtils.showInfo(this, "Item added.  New total: $" +
                    String.format("%.2f", b.getTotalAmount()));
            tfP1.setText(""); tfP2.setText(""); tfP3.setText("");
            refreshBillTable();
            loadItemsForBill(bid);
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void markPaid() {
        try {
            String bid = UIUtils.requireText(tfBillIdPay, "Bill ID");
            if (!UIUtils.confirm(this, "Mark bill " + bid + " as PAID?")) return;
            system.markBillPaid(bid);
            UIUtils.showInfo(this, "Bill " + bid + " marked PAID.");
            refreshBillTable();
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void viewItems() {
        try {
            loadItemsForBill(UIUtils.requireText(tfBillIdPay, "Bill ID"));
        } catch (Exception e) { UIUtils.showError(this, e.getMessage()); }
    }

    private void loadItemsForBill(String billId) {
        itemModel.setRowCount(0);
        Bill b = system.findBillById(billId);
        if (b == null) return;
        for (BillItem item : b.getItems())
            itemModel.addRow(new Object[]{
                item.getDescription(), String.format("%.2f", item.getCost())
            });
        itemModel.addRow(new Object[]{"────────────────────────────", "──────────"});
        itemModel.addRow(new Object[]{"TOTAL", String.format("%.2f", b.getTotalAmount())});
    }

    private void refreshBillTable() {
        billModel.setRowCount(0);
        system.listBills().forEach(b -> billModel.addRow(new Object[]{
            b.getBillId(), b.getAppointmentId(),
            String.format("%.2f", b.getTotalAmount()), b.getStatus().name()
        }));
    }
}
