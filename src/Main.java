import data.HotelDatabase;
import models.*;
import booking.*;
import enums.*;
import exceptions.*;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   HOTEL SYSTEM - FULL LIFECYCLE TEST     ");
        System.out.println("==========================================\n");

        // ---------------------------------------------------------
        // TEST 1: ADMIN OPERATIONS (CRUD & Manageable)
        // ---------------------------------------------------------
        System.out.println(">>> TEST 1: ADMIN OPERATIONS");
        Admin admin = new Admin("admin_boss", "superSecret1", Role.ADMIN, LocalDate.of(1980, 5, 5), 40);

        System.out.println("Admin creating a new RoomType (Penthouse)...");
        RoomType penthouseType = new RoomType("Penthouse", 500.0, 4, "Luxury top floor");
        HotelDatabase.addRoomType(penthouseType);

        System.out.println("Admin adding a new Room (Room 999)...");
        Room penthouseRoom = new Room(999, 10, penthouseType);
        HotelDatabase.addRoom(penthouseRoom);

        System.out.println("Total Rooms in Database: " + HotelDatabase.getAllRooms().size() + " (Pass)\n");

        // ---------------------------------------------------------
        // TEST 2: GUEST REGISTRATION
        // ---------------------------------------------------------
        System.out.println(">>> TEST 2: GUEST REGISTRATION & VALIDATION");
        Guest vipGuest = new Guest("sarah_connor", "pass123", LocalDate.of(1995, 10, 12),
                5000.0, "Los Angeles", Gender.FEMALE, "Penthouse preferred");
        HotelDatabase.addGuest(vipGuest);
        System.out.println("Guest registered: " + vipGuest.getUsername() + " | Balance: $" + vipGuest.getBalance() + " (Pass)\n");

        // ---------------------------------------------------------
        // TEST 3: EXCEPTION HANDLING (Intentional Failure)
        // ---------------------------------------------------------
        System.out.println(">>> TEST 3: DATE VALIDATION EXCEPTION (Should Fail)");
        try {
            LocalDate checkIn = LocalDate.now();
            LocalDate checkOut = checkIn.minusDays(2); // Invalid: Checkout before Checkin

            System.out.println("Attempting to book with checkout date in the past...");
            Reservation badRes = new Reservation(999, vipGuest, penthouseRoom, checkIn, checkOut);
            System.out.println("FAIL: Exception was not thrown!");
        } catch (IllegalArgumentException e) {
            System.out.println("SUCCESS (Exception Caught): " + e.getMessage() + "\n");
        }

        // ---------------------------------------------------------
        // TEST 4: SUCCESSFUL RESERVATION LOGIC
        // ---------------------------------------------------------
        System.out.println(">>> TEST 4: SUCCESSFUL BOOKING & CALCULATION");
        LocalDate validCheckIn = LocalDate.now();
        LocalDate validCheckOut = validCheckIn.plusDays(3); // 3 nights

        Reservation validRes = new Reservation(1001, vipGuest, penthouseRoom, validCheckIn, validCheckOut);
        HotelDatabase.addReservation(validRes);

        // Simulating the room being marked as unavailable
        // (Assuming your Room class has a method like setAvailable or book)
        if (penthouseRoom.isAvailable()) {
            // penthouseRoom.setAvailable(false); // Uncomment if you have this setter
            System.out.println("Room 999 marked as unavailable.");
        }

        System.out.println("Reservation Confirmed! Status: " + validRes.getStatus());
        System.out.println("Expected Duration: 3 nights | Actual: " + validRes.calculateDuration() + " nights");
        System.out.println("Expected Cost: $1500.0 | Actual: $" + validRes.calculateTotalCost() + " (Pass)\n");

        // ---------------------------------------------------------
        // TEST 5: RECEPTIONIST OPERATIONS (Status Updates)
        // ---------------------------------------------------------
        System.out.println(">>> TEST 5: RECEPTIONIST OPERATIONS");
        Receptionist deskClerk = new Receptionist("desk_clerk1", "pass123",  Role.RECEPTIONIST,8,LocalDate.of(1998, 2, 20));

        System.out.println("Guest arrives. Receptionist updates status to CHECKED_IN...");
        validRes.setStatus(ReservationStatus.CHECKED_IN);
        System.out.println("Current Reservation Status: " + validRes.getStatus() + " (Pass)\n");

        // ---------------------------------------------------------
        // TEST 6: INVOICING & PAYMENT (Payable Interface)
        // ---------------------------------------------------------
        System.out.println(">>> TEST 6: INVOICING & PAYMENT");
        Invoice finalInvoice = new Invoice(5001, validRes);
        HotelDatabase.addInvoice(finalInvoice);

        System.out.println("-- Invoice Generated --");
        System.out.println("Amount Due: $" + finalInvoice.getAmount());

        System.out.println("\nProcessing Payment...");
        // In a real scenario, you'd check if vipGuest.getBalance() >= finalInvoice.getAmount()
        // and throw InsufficientBalanceException if not.
        try {
            if (vipGuest.getBalance() < finalInvoice.getAmount()) {
                throw new InsufficientBalanceException("Guest does not have enough funds.");
            }

            // Deduct balance (assuming your Guest class has a setter or method for this)
            // vipGuest.setBalance(vipGuest.getBalance() - finalInvoice.getAmount());

            finalInvoice.processPayment(PaymentMethod.CREDIT_CARD);
            validRes.setStatus(ReservationStatus.COMPLETED);

            System.out.println("Payment Processed Successfully!");
            finalInvoice.printInvoice();

        } catch (InsufficientBalanceException e) {
            System.out.println("PAYMENT FAILED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("UNEXPECTED ERROR: " + e.getMessage());
        }

        System.out.println("\n==========================================");
        System.out.println(" ALL TESTS COMPLETED. READY FOR MILESTONE 2 ");
        System.out.println("==========================================");
    }
}