package hms.billing;

import java.io.Serializable;

/**
 * ABSTRACTION: BillItem interface defines the contract for all billing entries.
 * POLYMORPHISM: Different implementations are stored in ArrayList<BillItem>
 * and getCost() is called polymorphically to compute the total.
 */
public interface BillItem extends Serializable {
    double getCost();
    String getDescription();
}
