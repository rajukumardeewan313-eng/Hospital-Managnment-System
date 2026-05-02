package hms.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITION: Bill contains ArrayList<BillItem>.
 * POLYMORPHISM: getCost() called on each BillItem to compute total.
 */
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { PAID, UNPAID }

    private String billId;
    private String appointmentId;
    private String patientId;
    private double totalAmount;
    private double paidAmount;
    private Status status;

    // OBJECT COMPOSITION — holds different BillItem implementations
    private ArrayList<BillItem> items;

    public Bill(String billId, String appointmentId, String patientId, double totalAmount) {
        setBillId(billId);
        setAppointmentId(appointmentId);
        setPatientId(patientId);
        this.totalAmount = totalAmount;
        this.paidAmount = 0.0;
        this.status = Status.UNPAID;
        this.items = new ArrayList<>();
    }

    public void addItem(BillItem item) {
        if (item == null) throw new IllegalArgumentException("Bill item cannot be null.");
        items.add(item);
    }

    /**
     * POLYMORPHISM: iterates ArrayList<BillItem> and calls getCost() on each.
     * Each implementation computes cost differently.
     */
    public double getTotalAmount() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getCost();
        }
        return total;
    }

    public List<BillItem> getItems() {
        return new ArrayList<>(items); // defensive copy
    }

    public String getBillId() { return billId; }
    public void setBillId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Bill ID cannot be empty.");
        this.billId = id.trim();
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("Appointment ID cannot be empty.");
        this.appointmentId = id.trim();
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String pid) {
        if (pid == null || pid.trim().isEmpty())
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        this.patientId = pid.trim();
    }

    public double getTotalAmountValue() { return totalAmount; }
    public void setTotalAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
        this.totalAmount = amount;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Paid amount cannot be negative.");
        this.paidAmount = Math.min(amount, totalAmount);
    }

    public boolean isPaid() { return status == Status.PAID && paidAmount >= totalAmount; }

    public List<BillItem> getBillItems() { return new ArrayList<>(items); }

    @Override
    public String toString() {
        return String.format("Bill[%s] Appt:%s Total:$%.2f [%s]",
                billId, appointmentId, getTotalAmount(), status);
    }
}
