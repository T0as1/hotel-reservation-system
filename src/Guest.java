import enums.Gender;
import interfaces.Payable;
import org.w3c.dom.ls.LSOutput;

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

    //password setter

    public void setPassword(String password) {
        this.password = password;
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

    // get invoice amount and deduct from balance, handle balance exceptions
    @Override
    public boolean pay(double amount) throws InvalidAmountException, InsufficientBalanceException  {
        if(amount <= 0){
            throw new InvalidAmountException("Amount must be positive");
        }
        if(this.balance < amount){
            throw new InsufficientBalanceException("Balance is insufficient");
        }
        this.balance = this.balance - amount;
        return true;
    }

    public boolean Register(String username,String password, LocalDate dob, String address, Gender gender){
        return false;
    }
}
