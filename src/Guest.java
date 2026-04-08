import enums.Gender;
import interfaces.Payable;

import java.time.LocalDate;

public class Guest implements Payable {
    // data fields
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private double balance;
    private String Address;
    private Gender gender;
    private String roomPreferences;

    // setters & getters, password shouldn't have a getter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getRoomPreferences() {
        return roomPreferences;
    }

    public void setRoomPreferences(String roomPreferences) {
        this.roomPreferences = roomPreferences;
    }

    @Override
    public boolean pay(double amount) {
        // get invoice amount and deduct from balance, handle balance exceptions

        return false; //logic not implemented yet
    }
}
