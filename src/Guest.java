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

    public void setUsername(String username) throws EmptyUserNameException {
        this.username = username;
        if (username.isBlank()) {
           throw new EmptyUserNameException("Username cannot be empty");
        }
    }

    //password setter

    public void setPassword(String password) throws EmptyPasswordException {
        this.password = password;
        if (password.isBlank()) {
            throw new EmptyPasswordException("Password cannot be empty");
        }
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) throws dobException {
        this.dateOfBirth = dateOfBirth;
        if (dateOfBirth.getYear() < 1900 || dateOfBirth.getYear() > 2026)
            throw new dobException("Date of birth is invalid");
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
        this.setUsername(username);
        this.setPassword(password);
        this.setDateOfBirth(dob);
        this.setAddress(address);
        this.setGender(gender);
        // adding guest should be called here
        return true;
    }
}
