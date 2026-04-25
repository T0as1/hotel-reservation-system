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
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            System.out.println("Check-in not allowed. Reservation must be CONFIRMED first.");
            return;
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);

        System.out.println("--- Check-In Confirmation ---");
        System.out.println("Date: " + reservation.getCheckInDate());
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
        System.out.println("Date: " + reservation.getCheckOutDate());
        System.out.println("Guest: " + reservation.getGuest().getUsername() + " has checked out.");
        System.out.println("Reservation ID: " + reservation.getReservationID());
    }

    public void showDashboard(Scanner sc){
        boolean loggedIn = true;
        System.out.println("---Receptionist Dashboard---");

        while (loggedIn) {
            System.out.println("1.View All Guests\n2.View All Rooms\n3.View All Reservations\n" +
                    "4. Confirm Reservation\n5.Check-in\n6.Check-out\n7.Add Amenity to room\n8.Logout\nChoice: ");
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
                    System.out.println("0.Go back\nEnter Reservation ID to confirm: ");
                    try {
                        int id = Integer.parseInt(sc.nextLine());

                        if (id == 0) {
                            break;
                        }

                        Reservation resToConfirm = findReservationByID(id);

                        if (resToConfirm != null && resToConfirm.getStatus() == ReservationStatus.PENDING) {
                            resToConfirm.setStatus(ReservationStatus.CONFIRMED);
                            System.out.println("Reservation " + id + " confirmed successfully.");
                        } else {
                            System.out.println("Error: Reservation ID not found or reservation is not pending.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid input, please input a valid ID.");
                    }
                    break;

                case "5":
                    System.out.println("0.Go back\nEnter Reservation ID to check-in: ");
                    try{
                        int id = Integer.parseInt(sc.nextLine());
                        if(id == 0)
                            break;
                        Reservation resToCheckIn = findReservationByID(id);
                        if (resToCheckIn != null && resToCheckIn.getStatus() == ReservationStatus.CONFIRMED){
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

                case "6":
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

                case "7":
                    while (true) {
                        try {
                            System.out.println("\n---Add Amenity to Room---");
                            System.out.println("---Official Amenities---");
                            for(Amenity a : HotelDatabase.getAllAmenities()){
                                System.out.println(a);
                            }
                            System.out.println("------------------------");
                            System.out.print("0.Go Back\nEnter Amenity Name: ");

                            String nameInput = sc.nextLine();
                            if (nameInput.equals("0")) break;


                            Amenity amenityToAdd = HotelDatabase.getAmenityByName(nameInput);
                            if (amenityToAdd == null) {
                                System.out.println("Error: '" + nameInput + "' is not in the database, try again");
                                continue;
                            }

                            System.out.print("0.Go back\nEnter Room Number: ");
                            String roomInput = sc.nextLine();
                            if (roomInput.equals("0")) break;

                            int rNum = Integer.parseInt(roomInput);
                            Room room = HotelDatabase.findRoomByNumber(rNum);

                            if (room == null) {
                                System.out.println("Error: Room " + rNum + " not found. Please try again");
                                continue;
                            }

                            room.addAmenityToRoom(amenityToAdd.getName());
                            break;

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input: Room Number must be a numeric value");

                        } catch (Exception e) {
                            System.out.println("Unexpected error");
                            break;
                        }
                    }
                    break;
                case "8":
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
