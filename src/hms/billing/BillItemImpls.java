package hms.billing;

/**
 * POLYMORPHISM implementations of BillItem.
 * All three are stored together as ArrayList<BillItem> in Bill.
 */

// --- Consultation Fee ---
class ConsultationFeeItem implements BillItem {
    private static final long serialVersionUID = 1L;
    private final double fee;
    private final String doctorName;

    public ConsultationFeeItem(String doctorName, double fee) {
        if (fee < 0) throw new IllegalArgumentException("Consultation fee cannot be negative.");
        this.doctorName = doctorName;
        this.fee = fee;
    }

    @Override public double getCost() { return fee; }
    @Override public String getDescription() {
        return "Consultation Fee – Dr. " + doctorName;
    }
}

// --- Lab Test ---
class LabTestItem implements BillItem {
    private static final long serialVersionUID = 1L;
    private final String testName;
    private final double cost;

    public LabTestItem(String testName, double cost) {
        if (testName == null || testName.trim().isEmpty())
            throw new IllegalArgumentException("Test name cannot be empty.");
        if (cost < 0) throw new IllegalArgumentException("Lab test cost cannot be negative.");
        this.testName = testName.trim();
        this.cost = cost;
    }

    @Override public double getCost() { return cost; }
    @Override public String getDescription() { return "Lab Test – " + testName; }
}

// --- Medicine Charge ---
class MedicineChargeItem implements BillItem {
    private static final long serialVersionUID = 1L;
    private final String medicineName;
    private final double unitPrice;
    private final int quantity;

    public MedicineChargeItem(String medicineName, double unitPrice, int quantity) {
        if (medicineName == null || medicineName.trim().isEmpty())
            throw new IllegalArgumentException("Medicine name cannot be empty.");
        if (unitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        this.medicineName = medicineName.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    @Override public double getCost() { return unitPrice * quantity; }
    @Override public String getDescription() {
        return String.format("Medicine – %s x%d @ $%.2f", medicineName, quantity, unitPrice);
    }
}
