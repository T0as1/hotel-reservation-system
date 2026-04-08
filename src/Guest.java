import enums.Gender;
import interfaces.Payable;

import java.time.LocalDate;

public class Guest implements Payable {
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private double balance;
    private String Address;
    private Gender gender;
    private String roomPreferences;

    @Override
    public boolean pay(double amount) {
        return false;
    }
}
