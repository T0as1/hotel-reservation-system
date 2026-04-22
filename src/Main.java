import booking.*;
import data.HotelDatabase;
import enums.*;
import models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static Guest currentGuest = null;

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin123!";

    static final String RECEPTIONIST_USERNAME = "rec";
    static final String RECEPTIONIST_PASSWORD = "rec123";

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n========= HOTEL SYSTEM =========");
            System.out.println("1. Register");
            System.out.println("2. Login as Guest");
            System.out.println("3. Admin Panel");
            System.out.println("4. Receptionist Panel");
            System.out.println("0. Exit");

            int choice = getInt();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> adminLogin();
                case 4 -> receptionistLogin();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ========================= HELPER =========================

    static int getInt() {
        while (!sc.hasNextInt()) {
            System.out.println("Enter a valid number:");
            sc.next();
        }
        return sc.nextInt();
    }

    // ========================= GUEST =========================

    static void register() {
        try {
            sc.nextLine();

            System.out.print("Username: ");
            String username = sc.nextLine();

            System.out.println("Password must contain letter, digit, special char, min 5 chars.");
            System.out.print("Password: ");
            String password = sc.nextLine();

            System.out.print("DOB (YYYY-MM-DD): ");
            LocalDate dob = LocalDate.parse(sc.nextLine());

            System.out.print("Address: ");
            String address = sc.nextLine();

            System.out.print("Gender (MALE/FEMALE): ");
            Gender gender = Gender.valueOf(sc.nextLine().toUpperCase());

            Guest g = new Guest();
            g.register(username, password, dob, address, gender);

            currentGuest = HotelDatabase.findGuestByUsername(username);

            System.out.println("✅ Registered successfully and logged in successfully!");
            System.out.println("Welcome, " + currentGuest.getUsername());

            guestMenu(); // 🔥 go directly to guest menu

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    static void login() {
        sc.nextLine();

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        Guest g = new Guest();
        if (g.login(username, password)) {
            currentGuest = HotelDatabase.findGuestByUsername(username);
            System.out.println("✅ Login successful!");
            guestMenu();
        } else {
            System.out.println("❌ Login failed.");
        }
    }

    static void guestMenu() {
        while (true) {
            System.out.println("\n===== GUEST MENU =====");
            System.out.println("1. View All Rooms");
            System.out.println("2. View Available Rooms");
            System.out.println("3. Make Reservation");
            System.out.println("4. My Reservations");
            System.out.println("5. Cancel Reservation");
            System.out.println("6. Checkout & Pay");
            System.out.println("7. View Balance");
            System.out.println("0. Logout");

            int choice = getInt();

            switch (choice) {
                case 1 -> viewAllRooms();
                case 2 -> viewAvailableRooms();
                case 3 -> makeReservation();
                case 4 -> viewMyReservations();
                case 5 -> cancelReservation();
                case 6 -> checkout();
                case 7 -> System.out.println("Balance: $" + currentGuest.getBalance());
                case 0 -> {
                    currentGuest = null;
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void viewAllRooms() {
        List<Room> rooms = HotelDatabase.getAllRooms();
        for (Room r : rooms) {
            System.out.println("Room " + r.getRoomNumber()
                    + " | Type: " + r.getRoomType().getName()
                    + " | Price: $" + r.getRoomType().getPricePerNight()
                    + " | Available: " + r.isAvailable());
        }
    }

    static void viewAvailableRooms() {
        List<Room> rooms = HotelDatabase.getAvailableRooms();
        if (rooms.isEmpty()) {
            System.out.println("No available rooms.");
            return;
        }

        for (Room r : rooms) {
            System.out.println("Room " + r.getRoomNumber()
                    + " | Type: " + r.getRoomType().getName()
                    + " | Price: $" + r.getRoomType().getPricePerNight());
        }
    }

    static void makeReservation() {
        try {
            System.out.print("Enter Room Number: ");
            int roomNum = getInt();

            Room room = HotelDatabase.findRoomByNumber(roomNum);
            if (room == null) {
                System.out.println("❌ Room not found.");
                return;
            }

            sc.nextLine();
            System.out.print("Check-in (YYYY-MM-DD): ");
            LocalDate in = LocalDate.parse(sc.nextLine());

            System.out.print("Check-out (YYYY-MM-DD): ");
            LocalDate out = LocalDate.parse(sc.nextLine());

            List<Amenity> allAmenities = HotelDatabase.getAllAmenities();
            List<Amenity> chosenAmenities = new java.util.ArrayList<>();

            if (!allAmenities.isEmpty()) {
                System.out.println("Available amenities:");
                for (int i = 0; i < allAmenities.size(); i++) {
                    Amenity a = allAmenities.get(i);
                    System.out.println((i + 1) + ". " + a.getName() + " - $" + a.getAdditionalCost() + " per night");
                }

                System.out.println("Enter amenity numbers separated by spaces, or 0 for none:");
                String input = sc.nextLine().trim();

                if (!input.equals("0") && !input.isEmpty()) {
                    String[] parts = input.split("\\s+");
                    for (String part : parts) {
                        int index = Integer.parseInt(part) - 1;
                        if (index >= 0 && index < allAmenities.size()) {
                            Amenity selected = allAmenities.get(index);
                            if (!chosenAmenities.contains(selected)) {
                                chosenAmenities.add(selected);
                            }
                        }
                    }
                }
            }

            Reservation tempRes = new Reservation(
                    currentGuest,
                    room,
                    in,
                    out,
                    chosenAmenities
            );

            System.out.println("\n===== BOOKING SUMMARY =====");
            System.out.println("Guest: " + currentGuest.getUsername());
            System.out.println("Room: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType().getName());
            System.out.println("Check-in: " + in);
            System.out.println("Check-out: " + out);
            System.out.println("Duration: " + tempRes.calculateDuration() + " nights");

            System.out.println("Amenities:");
            if (chosenAmenities.isEmpty()) {
                System.out.println("None");
            } else {
                for (Amenity a : chosenAmenities) {
                    System.out.println("- " + a.getName() + " ($" + a.getAdditionalCost() + " per night)");
                }
            }

            System.out.println("Total cost: $" + tempRes.calculateTotalCost());

            System.out.print("Confirm booking? (yes/no): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (!confirm.equals("yes")) {
                System.out.println("Booking cancelled.");
                return;
            }

            currentGuest.makeReservation(tempRes);

            System.out.println("✅ Reservation created! ID: " + tempRes.getReservationID());

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    static void viewMyReservations() {
        List<Reservation> list = currentGuest.viewMyReservations();

        if (list.isEmpty()) {
            System.out.println("No reservations.");
            return;
        }

        for (Reservation r : list) {
            System.out.println("ID: " + r.getReservationID()
                    + " | Room: " + r.getRoom().getRoomNumber()
                    + " | Status: " + r.getStatus());
        }
    }

    static void cancelReservation() {
        System.out.print("Reservation ID: ");
        String id = sc.next();

        if (currentGuest.cancelReservation(id)) {
            System.out.println("✅ Reservation cancelled.");
        } else {
            System.out.println("❌ Reservation not found.");
        }
    }

    static void checkout() {
        try {
            System.out.print("Reservation ID: ");
            int id = getInt();

            for (Reservation r : HotelDatabase.getAllReservations()) {
                if (r.getReservationID() == id) {

                    if (!r.getGuest().getUsername().equals(currentGuest.getUsername())) {
                        System.out.println("❌ You can only pay for your own reservation.");
                        return;
                    }
                    if (r.getStatus() == ReservationStatus.CONFIRMED ||
                            r.getStatus() == ReservationStatus.CHECKED_IN ||
                            r.getStatus() == ReservationStatus.CHECKED_OUT ||
                            r.getStatus() == ReservationStatus.COMPLETED) {
                        System.out.println("❌ This reservation has already been paid.");
                        return;
                    }

                    Invoice inv = currentGuest.checkout(r);
                    inv.printInvoice();

                    System.out.print("Payment Method (CASH/CREDIT_CARD/ONLINE): ");
                    String paymentInput = sc.next().toUpperCase();

                    PaymentMethod method;
                    try {
                        method = PaymentMethod.valueOf(paymentInput);
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ Invalid payment method.");
                        return;
                    }

                    currentGuest.pay(inv.getAmount());
                    inv.processPayment(method);
                    r.setStatus(ReservationStatus.CONFIRMED);

                    System.out.println("✅ Payment successful!");
                    System.out.println("Remaining balance: $" + currentGuest.getBalance());
                    return;
                }
            }

            System.out.println("❌ Reservation not found.");

        } catch (Exception e) {
            System.out.println("❌ Payment failed: " + e.getMessage());
        }
    }

    // ========================= ADMIN =========================

    static void adminLogin() {
        sc.nextLine();

        System.out.print("Admin username: ");
        String username = sc.nextLine();

        System.out.print("Admin password: ");
        String password = sc.nextLine();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("✅ Admin login successful!");
            adminPanel();
        } else {
            System.out.println("❌ Invalid admin credentials.");
        }
    }

    static void adminPanel() {
        Admin admin = new Admin("admin", "admin123!", Role.ADMIN,
                LocalDate.of(1990, 1, 1), 8);

        while (true) {
            System.out.println("\n===== ADMIN PANEL =====");
            System.out.println("1. View Rooms");
            System.out.println("2. Add Room");
            System.out.println("3. Delete Room");
            System.out.println("4. Manage Room Types");
            System.out.println("5. Manage Amenities");
            System.out.println("0. Back");

            int c = getInt();

            switch (c) {
                case 1 -> viewAllRooms();

                case 2 -> {
                    System.out.print("Room number: ");
                    int num = getInt();

                    System.out.print("Floor: ");
                    int floor = getInt();

                    List<RoomType> types = HotelDatabase.getAllRoomTypes();
                    for (int i = 0; i < types.size(); i++) {
                        System.out.println(i + ": " + types.get(i).getName());
                    }

                    System.out.print("Choose type index: ");
                    int idx = getInt();

                    admin.createRoom(num, floor, types.get(idx));
                    System.out.println("✅ Room added.");
                }

                case 3 -> {
                    System.out.print("Room number: ");
                    admin.deleteRoom(getInt());
                    System.out.println("Room deleted.");
                }

                case 4 -> manageRoomTypes(admin);
                case 5 -> manageAmenities(admin);

                case 0 -> {
                    return;
                }
            }
        }
    }

    static void manageRoomTypes(Admin admin) {
        System.out.println("1. Add | 2. View | 3. Delete");
        int c = getInt();

        switch (c) {
            case 1 -> {
                sc.nextLine();
                System.out.print("Name: ");
                String name = sc.nextLine();

                System.out.print("Price: ");
                double price = sc.nextDouble();

                System.out.print("Capacity: ");
                int cap = sc.nextInt();

                sc.nextLine();
                System.out.print("Description: ");
                String desc = sc.nextLine();

                admin.createRoomType(name, price, cap, desc);
            }

            case 2 -> {
                for (RoomType rt : admin.readRoomTypes()) {
                    System.out.println(rt.getName() + " - $" + rt.getPricePerNight());
                }
            }

            case 3 -> {
                sc.nextLine();
                System.out.print("Name: ");
                admin.deleteRoomType(sc.nextLine());
            }
        }
    }

    static void manageAmenities(Admin admin) {
        System.out.println("1. Add | 2. View | 3. Delete");
        int c = getInt();

        switch (c) {
            case 1 -> {
                sc.nextLine();
                System.out.print("Name: ");
                String name = sc.nextLine();

                System.out.print("Description: ");
                String desc = sc.nextLine();

                System.out.print("Cost: ");
                double cost = sc.nextDouble();

                admin.createAmenity(name, desc, cost);
            }

            case 2 -> {
                for (Amenity a : admin.readAmenities()) {
                    System.out.println(a.getName() + " - $" + a.getAdditionalCost());
                }
            }

            case 3 -> {
                sc.nextLine();
                System.out.print("Name: ");
                admin.deleteAmenity(sc.nextLine());
            }
        }
    }

    // ========================= RECEPTIONIST =========================




    static void receptionistLogin() {
        sc.nextLine();

        System.out.print("Receptionist username: ");
        String username = sc.nextLine();

        System.out.print("Receptionist password: ");
        String password = sc.nextLine();

        if (username.equals(RECEPTIONIST_USERNAME) && password.equals(RECEPTIONIST_PASSWORD)) {
            System.out.println("✅ Receptionist login successful!");
            receptionistPanel();
        } else {
            System.out.println("❌ Invalid receptionist credentials.");
        }
    }
    static void receptionistPanel() {
        Receptionist rcp = new Receptionist(
                "rec", "rec123", Role.RECEPTIONIST,
                8, LocalDate.of(1995, 5, 5));

        while (true) {
            System.out.println("\n===== RECEPTIONIST =====");
            System.out.println("1. Check-in");
            System.out.println("2. Check-out");
            System.out.println("3. View Reservations");
            System.out.println("0. Back");

            int c = getInt();

            switch (c) {
                case 1 -> processCheck(rcp, true);
                case 2 -> processCheck(rcp, false);

                case 3 -> {
                    for (Reservation r : HotelDatabase.getAllReservations()) {
                        System.out.println("ID: " + r.getReservationID()
                                + " | Guest: " + r.getGuest().getUsername()
                                + " | Status: " + r.getStatus());
                    }
                }

                case 0 -> {
                    return;
                }
            }
        }
    }

    static void processCheck(Receptionist rcp, boolean isCheckIn) {
        System.out.print("Reservation ID: ");
        int id = getInt();

        for (Reservation r : HotelDatabase.getAllReservations()) {
            if (r.getReservationID() == id) {
                if (isCheckIn)
                    rcp.checkIn(r);
                else
                    rcp.checkOut(r);
                return;
            }
        }

        System.out.println("Reservation not found.");
    }
}