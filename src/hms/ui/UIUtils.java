package hms.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * UI Utilities — vibrant teal / coral / amber hospital theme.
 * All buttons, fields, tables share this consistent color system.
 */
public class UIUtils {

    // ── Brand Palette ──────────────────────────────────────────────────────
    public static final Color TEAL        = new Color(0,  168, 168);
    public static final Color TEAL_DARK   = new Color(0,  120, 120);
    public static final Color TEAL_LIGHT  = new Color(178,235, 242);

    public static final Color CORAL       = new Color(255, 87,  87);
    public static final Color CORAL_DARK  = new Color(200, 50,  50);

    public static final Color AMBER       = new Color(255, 160,   0);
    public static final Color AMBER_DARK  = new Color(200, 120,   0);

    public static final Color INDIGO      = new Color(63,  81,  181);
    public static final Color GREEN       = new Color(46,  160,  67);
    public static final Color PURPLE      = new Color(142,  36, 170);

    public static final Color BG_MAIN     = new Color(236, 246, 250);
    public static final Color BG_CARD     = Color.WHITE;

    public static final Color TEXT_DARK   = new Color(30,  40,  55);
    public static final Color TEXT_MID    = new Color(80,  100, 120);
    public static final Color BORDER_CLR  = new Color(200, 220, 235);

    // ── Buttons ────────────────────────────────────────────────────────────
    public static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? bg.darker().darker()
                        : getModel().isRollover() ? bg.darker()
                        : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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
        Dimension d = btn.getPreferredSize();
        btn.setPreferredSize(new Dimension(d.width + 24, 34));
        return btn;
    }

    // ── Text Fields ────────────────────────────────────────────────────────
    public static JTextField makeField(int cols) {
        JTextField tf = new JTextField(cols) {
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? TEAL : BORDER_CLR);
                g2.setStroke(new BasicStroke(hasFocus() ? 2f : 1.2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                g2.dispose();
            }
        };
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setForeground(TEXT_DARK);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 32));
        tf.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return tf;
    }

    public static JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_DARK);
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 32));
        return cb;
    }

    // ── Tables ─────────────────────────────────────────────────────────────
    public static JTable makeTable(String[] cols) {
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(0, 168, 168, 55));
                    c.setForeground(TEAL_DARK);
                } else {
                    c.setBackground(row % 2 == 0 ? BG_CARD : new Color(242, 250, 252));
                    c.setForeground(TEXT_DARK);
                }
                if (c instanceof JLabel) ((JLabel)c).setBorder(
                        BorderFactory.createEmptyBorder(0,8,0,8));
                return c;
            }
        };
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(TEAL_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(LEFT); setOpaque(true);
              setBackground(TEAL_DARK); setForeground(Color.WHITE);
              setFont(new Font("SansSerif", Font.BOLD, 12));
              setBorder(BorderFactory.createEmptyBorder(0,10,0,8)); }
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t,v,s,f,r,c); return this; }
        });
        return table;
    }

    public static JScrollPane scroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        sp.getViewport().setBackground(BG_CARD);
        return sp;
    }

    // ── Card / Section Panels ──────────────────────────────────────────────
    /** White rounded card with a colored left accent bar and titled border. */
    public static JPanel card(String title, Color accent) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                "  " + title + "  ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12), accent);
        p.setBorder(new CompoundBorder(
                new EmptyBorder(4, 4, 4, 4),
                new CompoundBorder(tb, new EmptyBorder(6, 12, 8, 12))));
        return p;
    }

    // ── Labels ─────────────────────────────────────────────────────────────
    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(TEXT_MID);
        return l;
    }

    public static JLabel boldLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(color);
        return l;
    }

    // ── Dialogs ────────────────────────────────────────────────────────────
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ── Validation ─────────────────────────────────────────────────────────
    public static int parseInt(JTextField f, String name) {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) {
            throw new NumberFormatException(name + " must be a whole number."); }
    }
    public static double parseDouble(JTextField f, String name) {
        try { return Double.parseDouble(f.getText().trim()); }
        catch (NumberFormatException e) {
            throw new NumberFormatException(name + " must be a valid number."); }
    }
    public static String requireText(JTextField f, String name) {
        String t = f.getText().trim();
        if (t.isEmpty()) throw new IllegalArgumentException(name + " cannot be empty.");
        return t;
    }
}
