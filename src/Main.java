import data.HotelDatabase;
import models.*;
import enums.*;
import exceptions.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("              HOTEL MENU                  ");
        System.out.println("==========================================");

        while (true) {
            System.out.println("""
                    
                    1. Guest Registration
                    2. Login
                    3. Exit
                    """);
            System.out.print("Choice: ");

            String choice = input.nextLine();

            switch (choice) {
                case "1":
                    Guest newGuest = handleRegistration(input);
                    newGuest.showDashboard(input);
                    break;

                case "2":
                    User loggedInUser = handleLogin(input);

                    if (loggedInUser instanceof Guest guest) {
                        guest.showDashboard(input);
                    } else if (loggedInUser instanceof Receptionist receptionist) {
                        receptionist.showDashboard(input);
                    } else if (loggedInUser instanceof Admin admin) {
                        admin.showDashboard(input);
                    }
                    break;

                case "3":
                    System.out.println("Thanks for visiting us!");
                    input.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    public static Guest handleRegistration(Scanner input) {
        System.out.println("\n--- Guest Registration ---");

        Guest newGuest = new Guest();

        while (true) {
            try {
                System.out.print("Enter Username: ");
                String username = input.nextLine();

                if (HotelDatabase.findGuestByUsername(username) != null) {
                    System.out.println("Error: Username already exists.");
                    continue;
                }

                newGuest.setUsername(username);
                break;

            } catch (InvalidUsernameException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.println("Password requires: 1 letter, 1 digit, 1 special character, minimum 5 characters.");
                System.out.print("Enter Password: ");

                String password = input.nextLine();
                newGuest.setPassword(password);
                break;

            } catch (InvalidPasswordException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        while (true) {
            try {
                System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
                LocalDate dob = LocalDate.parse(input.nextLine());

                newGuest.setDateOfBirth(dob);
                break;

            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date format. Use YYYY-MM-DD.");
            } catch (DobException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.print("Enter Address: ");
        newGuest.setAddress(input.nextLine());

        while (true) {
            try {
                System.out.print("Enter Gender (MALE/FEMALE): ");
                Gender gender = Gender.valueOf(input.nextLine().toUpperCase());

                newGuest.setGender(gender);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println("Error: Please enter MALE or FEMALE.");
            }
        }

        System.out.print("Enter Room Preferences: ");
        newGuest.setRoomPreferences(input.nextLine());

        newGuest.setBalance(2000.0);

        HotelDatabase.addGuest(newGuest);

        System.out.println("\nRegistration successful. Welcome, " + newGuest.getUsername() + "!");
        return newGuest;
    }

    public static User handleLogin(Scanner input) {
        System.out.println("\n--- Login ---");

        while (true) {
            System.out.print("0. Go back\nEnter Username: ");
            String username = input.nextLine();

            if (username.equals("0")) {
                return null;
            }

            System.out.print("Enter Password: ");
            String password = input.nextLine();

            User user = HotelDatabase.findUser(username, password);

            if (user == null) {
                System.out.println("Username or password is incorrect.");
                continue;
            }

            System.out.println("Login successful. Welcome, " + user.getUsername() + "!");
            return user;
        }
    }
}