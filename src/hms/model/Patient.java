package hms.model;

/**
 * INHERITANCE: Patient extends Person.
 * ENCAPSULATION: all fields private with getters/setters + validation.
 */
public class Patient extends Person {
    private static final long serialVersionUID = 1L;

    private int age;
    private String gender;
    private String address;

    public Patient(String id, String name, String phone, int age, String gender, String address) {
        super(id, name, phone);
        setAge(age);
        setGender(gender);
        setAddress(address);
    }

    // POLYMORPHISM: overrides abstract displayInfo()
    @Override
    public String displayInfo() {
        return String.format("[Patient] ID: %s | Name: %s | Age: %d | Gender: %s | Phone: %s | Address: %s",
                getId(), getName(), age, gender, getPhone(), address);
    }

    public int getAge() { return age; }
    public void setAge(int age) {
        if (age <= 0 || age > 150)
            throw new IllegalArgumentException("Age must be between 1 and 150.");
        this.age = age;
    }

    public String getGender() { return gender; }
    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty())
            throw new IllegalArgumentException("Gender cannot be empty.");
        this.gender = gender.trim();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty())
            throw new IllegalArgumentException("Address cannot be empty.");
        this.address = address.trim();
    }
}
