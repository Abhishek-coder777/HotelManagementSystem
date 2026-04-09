// models/Guest.java
package models;

public class Guest {
    private int guestId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String idProof;
    private String nationality;

    // Constructors
    public Guest() {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.idProof = "";
        this.nationality = "";
    }

    public Guest(String firstName, String lastName, String email, String phone, String address, String idProof,
            String nationality) {
        this.firstName = (firstName != null) ? firstName : "";
        this.lastName = (lastName != null) ? lastName : "";
        this.email = (email != null) ? email : "";
        this.phone = (phone != null) ? phone : "";
        this.address = (address != null) ? address : "";
        this.idProof = (idProof != null) ? idProof : "";
        this.nationality = (nationality != null) ? nationality : "";
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName : "";
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = (lastName != null) ? lastName : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email : "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone : "";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address : "";
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = (idProof != null) ? idProof : "";
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = (nationality != null) ? nationality : "";
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + email + ")";
    }
}