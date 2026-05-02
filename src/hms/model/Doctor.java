package hms.model;

/**
 * INHERITANCE: Doctor extends Person.
 */
public class Doctor extends Person {
    private static final long serialVersionUID = 1L;

    private String specialization;
    private double consultationFee;

    public Doctor(String id, String name, String phone, String specialization, double consultationFee) {
        super(id, name, phone);
        setSpecialization(specialization);
        setConsultationFee(consultationFee);
    }

    // POLYMORPHISM: overrides abstract displayInfo()
    @Override
    public String displayInfo() {
        return String.format("[Doctor] ID: %s | Name: %s | Specialization: %s | Fee: $%.2f | Phone: %s",
                getId(), getName(), specialization, consultationFee, getPhone());
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty())
            throw new IllegalArgumentException("Specialization cannot be empty.");
        this.specialization = specialization.trim();
    }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double fee) {
        if (fee < 0)
            throw new IllegalArgumentException("Consultation fee cannot be negative.");
        this.consultationFee = fee;
    }
}
