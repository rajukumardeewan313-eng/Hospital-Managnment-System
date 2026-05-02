package hms.billing;

/**
 * Factory to create BillItem instances. Keeps UI decoupled from concrete classes.
 */
public class BillItemFactory {

    public static BillItem createConsultationFee(String doctorName, double fee) {
        return new ConsultationFeeItem(doctorName, fee);
    }

    public static BillItem createLabTest(String testName, double cost) {
        return new LabTestItem(testName, cost);
    }

    public static BillItem createMedicineCharge(String medicineName, double unitPrice, int quantity) {
        return new MedicineChargeItem(medicineName, unitPrice, quantity);
    }
}
