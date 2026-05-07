package models;
import data.HotelDatabase;
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
        System.out.println("Date: " + reservation.getCheckInDate());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked in.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }


    public void checkOut(Reservation reservation) {

        reservation.setStatus(ReservationStatus.CHECKED_OUT);


        System.out.println("--- Check-Out Confirmation ---");
        System.out.println("Date: " + reservation.getCheckOutDate());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked out.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }

    public void showDashboard(Scanner sc){
        boolean loggedIn = true;
        System.out.println("---Receptionist Dashboard---");

        while (loggedIn) {
            System.out.println("1.View All Guests\n2.View All Rooms\n3.View All Reservations\n" +
                    "4.Check-in\n5.Check-out\n6.Logout\nChoice: ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("\n---Current Guest List---");
                    for(Guest g: viewAllGuests()){
                        System.out.println(g);
                    }
                    System.out.println("\n------------------------");
                    break;

                case "2":
                    System.out.println("\n---Current Room List---");
                    for(Room r : viewAllRooms()){
                        System.out.println(r);
                    }
                    System.out.println("\n-----------------------");
                    break;

                case "3":
                    System.out.println("\n---Current Reservation List---");
                    for(Reservation res: viewAllReservations()){
                        System.out.println(res);
                    }
                    System.out.println("\n------------------------------");
                    break;

                case "4":
                    System.out.println("0.Go back\nEnter Reservation ID to check-in: ");
                    try{
                        int id = Integer.parseInt(sc.nextLine());
                        if(id == 0)
                            break;
                        Reservation resToCheckIn = findReservationByID(id);
                        if(resToCheckIn != null && resToCheckIn.getStatus() == ReservationStatus.PENDING){
                            checkIn(resToCheckIn);
                            resToCheckIn.getRoom().setAvailable(false);
                        }
                        else
                            System.out.println("Error: Reservation ID not found or reservation is not eligible");
                    } catch (NumberFormatException e)
                    { System.out.println("Error: Invalid input, please input a valid ID");}
                    catch (Exception e){
                        System.out.println("Unexpected error");
                    }
                    break;

                case "5":
                    System.out.println("0.Go back\nEnter reservation ID to check-out: ");
                    try{
                        int id = Integer.parseInt(sc.nextLine());
                        if(id == 0 )
                            break;
                        Reservation ResToCheckout = findReservationByID(id);
                        if(ResToCheckout != null){
                            checkOut(ResToCheckout);
                            ResToCheckout.getRoom().setAvailable(true);
                            break;
                        }
                        else {
                            System.out.println("Error: Reservation ID not found");
                            break;
                        }
                    } catch (NumberFormatException e)
                    { System.out.println("Error: Invalid input, please input a valid ID");}
                    catch (Exception e)
                    {
                        System.out.println("Unexpected Error");
                    }
		    break;		     


                case "6":
                    System.out.println("Logging out...");
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Invalid choice, try again");
            }
        }
    }

    private Reservation findReservationByID(int id){
        for(Reservation r : HotelDatabase.getAllReservations())
        {
            if (r.getReservationID() == id)
                return r;
        }
        return null;
    }

    @Override
    public boolean login(String username, String password) {
        if(this.getUsername().equals(username) && this.password.equals(password))
            return true;
        return false;
    }

}
