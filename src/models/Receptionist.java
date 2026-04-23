package models;
import enums.Role;
import enums.ReservationStatus;
import booking.Reservation;
import java.time.LocalDate;
import java.util.Scanner;

public class Receptionist extends Staff {


    public Receptionist(String username, String password, Role role, int workingHours, LocalDate dateOfBirth) {
        super(username, password, role, workingHours, dateOfBirth);
    }


    public void checkIn(Reservation reservation) {

        reservation.setStatus(ReservationStatus.CHECKED_IN);


        System.out.println("--- Check-In Confirmation ---");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked in.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }


    public void checkOut(Reservation reservation) {

        reservation.setStatus(ReservationStatus.CHECKED_OUT);


        System.out.println("--- Check-Out Confirmation ---");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked out.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }

    public void showDashboard(Scanner sc){
        System.out.println("---Receptionist Dashboard---");
        System.out.println("1.View All Guests\n2.View All Rooms\n3.View All Reservations\n" +
                "4.Check-in\n5.Check-out\n6.Logout\nChoice: ");
        String choice = sc.nextLine();
        while (true) {
            switch (choice) {
                case "1":
                    System.out.println("\n---Current Guest List---");
                    for(Guest g: viewAllGuests()){
                        System.out.println(g);
                    }
                    System.out.println("\n------------------------");
                case "2":
                    System.out.println("\n---Current Room List---");
                    for(Room r : viewAllRooms()){
                        System.out.println(r);
                    }
                    System.out.println("\n------------------------------");
                case "3":
                    System.out.println("\n---Current Reservation List---");
                    for(Reservation res: viewAllReservations()){
                        System.out.println(res);
                    }
                    System.out.println("\n------------------------------");
            }
        }
    }

    @Override
    public boolean login(String username, String password) {
        if(this.getUsername().equals(username) && this.password.equals(password))
            return true;
        return false;
    }

}
