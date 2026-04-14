import enums.Role;
import String;
import java.time.LocalDate;
package enums.Role;
public abstract class Staff {
    private String username;
    private String password;
    private Role role;
    private int workingHours;
    private LocalDate dateOfBirth;
    public Staff(String username,String password,Role role,int workingHours,LocalDate dateOfBirth){
        setUsername(username);
        setPassword(password);
        setDateOfBirth(dateOfBirth);
        setRole(role);
        setWorkingHours(workingHours);

    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Invalid date of birth. It cannot be null");
        }
        this.dateOfBirth = dateOfBirth;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role must be specified as ADMIN or RECEPTIONIST.");
        }
        this.role = role;
    }
    public Role getRole() {
        return role;
    }
    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        if (workingHours < 0) {
            throw new IllegalArgumentException("Working hours cannot be negative");
        }
        this.workingHours = workingHours;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters");
        }
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(username==null){
            throw new IllegalArgumentException("Username cannot be null ");
        }
        else {
            this.username = username;}

    }

}