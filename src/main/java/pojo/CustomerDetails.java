package pojo;

public class CustomerDetails {

    private final String firstName;
    private final String lastName;
    private final String postalCode;

    public CustomerDetails(String firstName, String lastName, String postalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return "CustomerDetails{firstName='" + firstName + "', lastName='" + lastName + "', postalCode='" + postalCode + "'}";
    }
}
