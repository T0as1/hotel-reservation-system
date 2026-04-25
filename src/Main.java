import data.HotelDatabase;
import models.*;
import enums.*;
import exceptions.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        User currentUser;
        System.out.println("==========================================");
        System.out.println("              HOTEL MENU                  ");
        System.out.println("==========================================\n");
        while (true) {
            System.out.println("1.Guest Registration\n2.Login\n3.Exit\nChoice: ");
            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    handleRegistration(input);
                    break;
                case "2":
                    User loggedinUser = handleLogin(input);
                    if(loggedinUser != null) {
                        if(loggedinUser instanceof Guest){
                            Guest g = (Guest) loggedinUser;
                            g.showDashboard(input);
                        }
                        else if(loggedinUser instanceof Receptionist){
                            Receptionist rec = (Receptionist) loggedinUser;
                            rec.showDashboard(input);
                        }
                        else if(loggedinUser instanceof Admin){
                            Admin a = (Admin) loggedinUser;
                            a.showDashboard(input);
                        }
                    }
                    break;

                case "3":
                    System.out.println("Thanks for visiting us!");
                    return;
                default:
                    System.out.println("Invalid choice, try again");
            }
        }

    }

    public static void handleRegistration(Scanner s) {
        System.out.println("\n---Registration---");
        Guest newGuest = new Guest();

        // Username Loop
        while (true) {
            try {
                System.out.print("Enter Username: ");
                String username = s.nextLine();
                newGuest.setUsername(username); // Throws EmptyUserNameException if invalid
                break; // If no exception, break the loop and move on
            } catch (InvalidUsernameException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Registration failed: Unexpected input");
            }
        }
        // Password loop
        while (true) {
            try {
                System.out.println("Password requires: 1 letter, 1 digit, 1 special char, min 5 characters");
                System.out.println("Enter Password: ");
                String pass = s.nextLine();
                newGuest.setPassword(pass);
                break;
            } catch (InvalidPasswordException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Registration failed: Unexpected input");
            }
        }

        // dob loop
        while (true) {
            try {
                System.out.println("Enter Date of Birth (YYYY-MM-DD): ");
                String dobInput = s.nextLine();
                LocalDate dob = LocalDate.parse(dobInput); // Converts String to LocalDate
                newGuest.setDateOfBirth(dob); // Throws dobException if invalid year
                break;
            } catch (dobException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        System.out.print("Enter Address: ");
        newGuest.setAddress(s.nextLine());

        // Gender Loop (Handling Enums)
        while (true) {
            try {
                System.out.print("Enter Gender (MALE/FEMALE): ");
                String genderInput = s.nextLine().toUpperCase();
                Gender gender = Gender.valueOf(genderInput);
                newGuest.setGender(gender);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Please enter either MALE or FEMALE.");
            }
        }

        System.out.println("Enter room preferences: ");
        newGuest.setRoomPreferences(s.nextLine());

        //save to database
        HotelDatabase.addGuest(newGuest);
        System.out.println("\nRegistration successful! Welcome, " + newGuest.getUsername());
    }

    public static User handleLogin(Scanner s) {
        System.out.println("---Login---");
        User currentUser = null;
        while (true) {
            try {
                System.out.println("0.Go back\nEnter Username: ");
                String u = s.nextLine();
                if(u.equals("0"))
                    return null;
                System.out.println("Password requires: 1 letter, 1 digit, 1 special char, min 5 characters");
                System.out.println("Enter Password: ");
                String p = s.nextLine();
                currentUser = HotelDatabase.findUser(u, p);
                if(currentUser == null)
                {
                    System.out.println("Username or password is incorrect ");
                    continue;
                }
                break;
            } catch (InvalidUsernameException e) {
                System.out.println("Error" + e.getMessage());
            } catch (InvalidPasswordException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Login failed: Unexpected input");
            }

        }
        return currentUser;
    }
}