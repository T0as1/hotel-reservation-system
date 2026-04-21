import data.HotelDatabase;
import models.Guest;
import models.Room;
import booking.Reservation;
import booking.Invoice;
import enums.Gender;
import enums.PaymentMethod;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- STARTING HOTEL RESERVATION SYSTEM (BACKEND TEST) ---");

        // 1. Create a new Guest
        Guest currentGuest = new Guest("john_doe", "password123", LocalDate.of(1990, 5, 15),
                1000.0, "123 Main St", Gender.MALE, "Suite, High Floor");

        // 2. Add Guest to Database (Simulating Registration)
        HotelDatabase.addGuest(currentGuest);
        System.out.println("Guest registered successfully: " + currentGuest.getUsername());

        // 3. Search for available rooms
        List<Room> availableRooms = HotelDatabase.getAvailableRooms();
        System.out.println("\nAvailable Rooms Found: " + availableRooms.size());

        if (!availableRooms.isEmpty()) {
            // 4. Select the first available room
            Room selectedRoom = availableRooms.get(0);
            System.out.println("Selecting Room No: " + selectedRoom.getRoomNumber() +
                    " (" + selectedRoom.getRoomType().getName() + ")");

            // 5. Create a Reservation (Check-in today, Check-out in 3 days)
            LocalDate checkIn = LocalDate.now();
            LocalDate checkOut = checkIn.plusDays(3);

            Reservation myReservation = new Reservation(
                    1001, currentGuest, selectedRoom, checkIn, checkOut
            );

            // Add reservation to database
            HotelDatabase.addReservation(myReservation);
            System.out.println("\nReservation created successfully! Status: " + myReservation.getStatus());

            // 6. Generate and Process Invoice
            Invoice myInvoice = new Invoice(5001, myReservation);
            HotelDatabase.addInvoice(myInvoice);

            System.out.println("\n--- Processing Payment ---");
            myInvoice.printInvoice(); // Will show unpaid

            // Simulating payment (you would normally check guest balance here using your Payable interface)
            myInvoice.processPayment(PaymentMethod.CREDIT_CARD);

            System.out.println("\n--- Final Invoice Status ---");
            myInvoice.printInvoice(); // Will show paid
        } else {
            System.out.println("No rooms available for booking.");
        }

        System.out.println("\n--- TEST COMPLETE ---");
    }
}