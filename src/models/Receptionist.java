package models;
import enums.Role;
import enums.ReservationStatus;
import booking.Reservation;
import java.time.LocalDate;

public class Receptionist extends Staff {

    public Receptionist(String username, String password, Role role, int workingHours, LocalDate dateOfBirth) {
        super(username, password, role.RECEPTIONIST, workingHours, dateOfBirth);
    }

    public void checkIn(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CHECKED_IN); //
        System.out.println("--- Check-In Confirmation ---");
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " checked in.");
    }

    public void checkOut(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CHECKED_OUT); //
        System.out.println("--- Check-Out Confirmation ---");
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " checked out.");
    }
}
