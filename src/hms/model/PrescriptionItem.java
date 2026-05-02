package hms.model;

import java.io.Serializable;

/**
 * A single medicine prescription entry inside a MedicalRecord.
 */
public class PrescriptionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medicineName;
    private String dose;
    private int days;
    private String instructions;

    public PrescriptionItem(String medicineName, String dose, int days, String instructions) {
        setMedicineName(medicineName);
        setDose(dose);
        setDays(days);
        setInstructions(instructions);
    }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Medicine name cannot be empty.");
        this.medicineName = name.trim();
    }

    public String getDose() { return dose; }
    public void setDose(String dose) {
        if (dose == null || dose.trim().isEmpty())
            throw new IllegalArgumentException("Dose cannot be empty.");
        this.dose = dose.trim();
    }

    public int getDays() { return days; }
    public void setDays(int days) {
        if (days <= 0)
            throw new IllegalArgumentException("Days must be positive.");
        this.days = days;
    }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) {
        this.instructions = instructions == null ? "" : instructions.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %d days | %s", medicineName, dose, days, instructions);
    }
}
