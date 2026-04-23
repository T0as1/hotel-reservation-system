package models;
import enums.Role;
import enums.ReservationStatus;
import booking.Reservation;
import java.time.LocalDate;

public class Receptionist extends Staff {


    public Receptionist(String username, String password, Role role, int workingHours, LocalDate dateOfBirth) {
        super(username, password, role, workingHours, dateOfBirth);
    }


    public void checkIn(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            System.out.println("Check-in not allowed. Reservation must be CONFIRMED first.");
            return;
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);

        System.out.println("--- Check-In Confirmation ---");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked in.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }


    public void checkOut(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            System.out.println("Check-out not allowed. Guest must be CHECKED_IN first.");
            return;
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.getRoom().release();

        System.out.println("--- Check-Out Confirmation ---");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked out.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }
}
