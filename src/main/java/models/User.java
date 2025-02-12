package models;

/**
 * Represents a user entity containing details such as ID, first name, last name,
 * email, phone number, address, city, postal code, country, password, and role.
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String password; // Stores the hashed password
    private String role; // "customer" or "admin"

    /**
     * Default constructor for User.
     */
    public User() {
    }

    /**
     * Constructs a User with the specified details.
     *
     * @param id The unique identifier of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email of the user.
     * @param phoneNumber The phone number of the user.
     * @param address The address of the user.
     * @param city The city of the user.
     * @param postalCode The postal code of the user.
     * @param country The country of the user.
     * @param password The hashed password of the user.
     * @param role The role of the user (customer or admin).
     */
    public User(int id, String firstName, String lastName, String email, String phoneNumber, String address,
                String city, String postalCode, String country, String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Constructs a simplified User for registration and validation.
     *
     * @param id The unique identifier of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email of the user.
     * @param password The hashed password of the user.
     * @param role The role of the user (customer or admin).
     */
    public User(int id, String firstName, String lastName, String email, String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id The user ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's email.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number.
     *
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber The phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's address.
     *
     * @return The address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the user's address.
     *
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the user's city.
     *
     * @return The city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the user's city.
     *
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the user's postal code.
     *
     * @return The postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the user's postal code.
     *
     * @param postalCode The postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the user's country.
     *
     * @return The country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the user's country.
     *
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the user's password hash.
     *
     * @return The hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password hash.
     *
     * @param password The hashed password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's role.
     *
     * @return The role (customer or admin).
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role The role to set.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Checks if the user is an administrator.
     *
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}
