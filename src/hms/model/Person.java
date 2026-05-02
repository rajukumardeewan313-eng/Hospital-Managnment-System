package hms.model;

import java.io.Serializable;

/**
 * Abstract base class demonstrating ABSTRACTION and ENCAPSULATION.
 * Patient and Doctor INHERIT from this class (INHERITANCE pillar).
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Private fields — ENCAPSULATION
    private String id;
    private String name;
    private String phone;

    public Person(String id, String name, String phone) {
        setId(id);
        setName(name);
        setPhone(phone);
    }

    // Abstract method — ABSTRACTION (forces subclasses to implement)
    public abstract String displayInfo();

    // Getters & Setters with validation
    public String getId() { return id; }
    public void setId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("ID cannot be empty.");
        this.id = id.trim();
    }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("Phone cannot be empty.");
        this.phone = phone.trim();
    }

    @Override
    public String toString() {
        return displayInfo();
    }
}
