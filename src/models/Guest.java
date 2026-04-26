package models;

import data.HotelDatabase;
import enums.PaymentMethod;
import enums.ReservationStatus;
import exceptions.*;
import enums.Gender;
import interfaces.Payable;
import booking.Invoice;
import booking.Reservation;

import java.util.ArrayList;
import java.util.List;


import java.time.LocalDate;
import java.util.Scanner;

public class Guest extends User implements Payable {
    // data fields

    private LocalDate dateOfBirth;
    private double balance = 2000.0;
    private String address;
    private Gender gender;
    private String roomPreferences;

    //Constructors
    public Guest(){

    }

    public Guest(String username, String password, LocalDate dateOfBirth,
                 double balance, String address, Gender gender,String roomPreferences ){
        super(username, password);
        this.dateOfBirth = dateOfBirth;
        this.balance = balance;
        this.address = address;
        this.gender = gender;
        this.roomPreferences = roomPreferences;
    }

    // setters & getters, password shouldn't have a getter

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        if (dateOfBirth.getYear() > 2008)
            throw new dobException("Date of birth is invalid, only 18+ are allowed to register");
        if (dateOfBirth.getYear() < 1900 )
            throw new dobException("Date of birth can not be earlier than 1900");
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        if (balance < 0){
            throw new InsufficientBalanceException("Balance cannot be negative");
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getRoomPreferences() {
        return roomPreferences;
    }

    public void setRoomPreferences(String roomPreferences) {
        if(roomPreferences == null)
            this.roomPreferences = "None";
        else {
            this.roomPreferences = roomPreferences;
        }
    }


    // get invoice amount and deduct from balance, handle balance exceptions
    @Override
    public boolean pay(double amount) {
        if(amount <= 0){
            throw new InvalidAmountException("Amount must be positive");
        }
        if(this.balance < amount){
            throw new InsufficientBalanceException("Balance is insufficient");
        }
        this.balance = this.balance - amount;
        return true;
    }


    public boolean register(String username,String password, LocalDate dob, String address, Gender gender){
        this.setUsername(username);
        this.setPassword(password);
        this.setDateOfBirth(dob);
        this.setAddress(address);
        this.setGender(gender);
        return true;
    }

    public boolean login(String username, String password) {

        if (this.username.equals(username) && this.password.equals(password)) {
            return true;
        }
        return false;
    }

    public List<Room> viewAvailableRooms(LocalDate start, LocalDate end){
       return HotelDatabase.getAvailableRooms(start, end);
    }

    public boolean makeReservation(Reservation res){
        HotelDatabase.addReservation(res);
        return true;
    }

    public List<Reservation> viewMyReservations() {
        List<Reservation> myReservations = new ArrayList<>();
        for(Reservation r: HotelDatabase.getAllReservations()){
            if(r.getGuest().getUsername().equals(this.username))
                myReservations.add(r);
        }
        return myReservations;
    }

    public boolean cancelReservation(String reservationId){
        int reservationIdInt = Integer.parseInt(reservationId);
        for(Reservation r : HotelDatabase.getAllReservations()){
            if(r.getReservationID() == reservationIdInt){
                r.setStatus(ReservationStatus.CANCELLED);
                return true;
            }
        }
        return false;
    }

    public Invoice checkout(Reservation res) {
        Invoice invoice = new Invoice(res.getReservationID(), res);
        this.pay(invoice.getAmount());
        HotelDatabase.addInvoice(invoice);
        return invoice;
    }

    public String toString (){
        return super.toString() + "| Date of birth: " + getDateOfBirth() + " | Balance: " + getBalance()
                + " | Address: " + getAddress() +  " | Gender: " + getGender() + " | Room preferences: "
                + getRoomPreferences();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof Guest))
            return false;
        Guest guest = (Guest) obj;
        return this.getUsername().equals(guest.getUsername());
    }

    public void showDashboard(Scanner sc){
        boolean loggedIn = true;
        System.out.println("\n---Guest Dashboard---");
        while (loggedIn) {
            System.out.println("""
                    1.View Available Rooms
                    2.Make a Reservation
                    3.View My Reservations
                    4.Cancel Reservation
                    5.Check out
                    6.Logout""");
            String choice = sc.nextLine();
            switch (choice){
                case "1":
                    LocalDate start = null;
                    LocalDate end = null;
                    System.out.println("\n0.Go back\n1.Rooms available for today\n" +
                            "2.Rooms available for a specific date range");
                    String d = sc.nextLine();
                    if(d.equals("0"))
                        break;
                    if(d.equals("1")) {
                        System.out.println("\n---Rooms Available for today---");
                        for (Room r : viewAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(1))) {
                            System.out.println(r);
                        }
                        System.out.println("\n---------------------------");
                        break;
                    }
                    if(d.equals("2")) {
                       while(true) {
                          try {
                               System.out.print("0.Go back\nPlease enter start date (YYYY-MM-DD): ");
                               String s = sc.nextLine();
                               if (s.equals("0")) {
                                   return;
                               }
                               LocalDate temp = LocalDate.parse(s);
                               // if cond. to check that check in date is not in the past
                               if (temp.isBefore(LocalDate.now())) {
                                   System.out.print("Error: Date in the past");
                                   continue;
                               } else
                                   start = temp;

                               System.out.print("Enter end date (YYYY-MM-DD): ");
                               end = LocalDate.parse(sc.nextLine());
                               if (!(start.isBefore(end))) {
                                   System.out.println("Error: Start date must be before end date");
                                   continue;
                               }
                               break;
                          } catch (Exception e) {
                              System.out.println("Error: please input a valid date");
                          }
                       }
                            System.out.println("\n---Rooms Available from " + start + " to " + end + " ---");
                            for (Room r : viewAvailableRooms(start, end)) {
                                System.out.println(r);
                            }
                            System.out.println("\n---------------------------");
                            break;
                        }
                    break;

                case "2": try{
                    System.out.println("0.Go back\n" +
                            "(To view available rooms, choose option 1 from the dashboard)\n" +
                            "Enter room number you want to book");
                    int resRoomNo = -1; // invalid value for while loop
                    while (resRoomNo == -1)
                    {
                        try {
                            resRoomNo = Integer.parseInt(sc.nextLine());
                        } catch (NumberFormatException e)
                        {
                            System.out.println("Please enter a valid room number");
                        } catch (Exception e){
                            System.out.println("Unexpected error");
                        }
                    }
                    if(resRoomNo == 0)
                        break;
                    Room selectRoom =  HotelDatabase.findRoomByNumber(resRoomNo);
                    LocalDate inDate = null;
                    LocalDate outDate = null;
                    LocalDate today = LocalDate.now();
                    if(selectRoom != null && selectRoom.isAvailable()) {
                        // This loop is to ensure dates are input correctly, then it is broken and flow continues;
                        while (true) {
                            try {
                                System.out.print("0.Go back\nEnter Check-In Date (YYYY-MM-DD): ");
                                String input = sc.nextLine();
                                if (input.equals("0")) {
                                    return;
                                }
                                LocalDate temp = LocalDate.parse(input);
                                // if cond. to check that check in date is not in the past
                                if (temp.isBefore(today)) {
                                    System.out.println("Error: Date in the past");
                                    continue;
                                } else
                                    inDate = temp;

                                System.out.print("Enter Check-Out Date (YYYY-MM-DD): ");
                                outDate = LocalDate.parse(sc.nextLine());
                                if (!(inDate.isBefore(outDate))) {
                                    System.out.println("Error: Check-in date must be before Check-out date");
                                    continue;
                                }
                                if (HotelDatabase.isRoomClashing(resRoomNo, inDate, outDate)) {
                                    System.out.println("Error: Room " + resRoomNo + " is already booked for these dates.");
                                    System.out.println("Please try a different room or different dates.");
                                    continue;
                                }
                                break;
                            } catch (Exception e) {
                                System.out.println("Error: Please input date in the correct format");
                            }
                        }

                        // Auto-generate ID
                        int newId = HotelDatabase.getAllReservations().size() + 1;

                        Reservation newRes = new Reservation(newId, this, selectRoom, inDate, outDate);
                        while (true) {
                            System.out.println("\n--- Available Amenities ---");
                            for (Amenity a : HotelDatabase.getAllAmenities()) {
                                System.out.println("- " + a.getName() + " (" + a.getAdditionalCost() + ")");
                            }
                            System.out.println("Enter exact amenity name to add, or type 'done' to finish:");

                            String c = sc.nextLine();
                            if (c.equalsIgnoreCase("done")) break;

                            Amenity selected = HotelDatabase.getAmenityByName(c);

                            if (selected == null) {
                                System.out.println("Error: Amenity not found.");
                                continue;
                            }

                            boolean alreadyInRoom = false;
                            if (selectRoom.getAmenities() != null) {
                                for (Amenity roomAmenity : selectRoom.getAmenities()) {
                                    if (roomAmenity.getName().equalsIgnoreCase(c)) {
                                        alreadyInRoom = true;
                                        break;
                                    }
                                }
                            }

                            if (alreadyInRoom) {
                                System.out.println("Error: Amenity is already in room " + selectRoom.getRoomNumber()
                                        + " and included in its base price.");
                            } else if (newRes.getSelectedAmenities().contains(selected)) {
                                System.out.println("Error: Amenity already selected.");
                            } else {
                                newRes.getSelectedAmenities().add(selected);
                                System.out.println(selected.getName() + " added to your reservation.");
                            }
                        }

                        HotelDatabase.addReservation(newRes);


                        System.out.println("Reservation successful! Your ID is: " + newId);
                        double baseCost = (newRes.getRoom().getRoomType().getPricePerNight())
                                *(newRes.calculateDuration()); // calculates price of the room without selected amenities
                        System.out.println("Base Cost: " + baseCost);
                        if(selectRoom.getAmenities() != null)
                        {
                            double totalPrice = newRes.calculateTotalCost();
                            for (Amenity a : newRes.getSelectedAmenities()) {
                                System.out.println("+ " + a.getName() + ": " + a.getAdditionalCost());
                            }

                            System.out.println("---------------------------");
                            System.out.println("Total Cost: " + totalPrice+"\n");
                            break;

                        } else {
                        System.out.println("Room is not available or does not exist.");
                        break;
                    }
                }
                } catch (Exception e) {
                    System.out.println("Error: Invalid input, please input a valid room number");
                }
                break;

                case "3":
                    System.out.println("\n---View My Reservations---");
                    boolean hasRes = false;
                    for (Reservation res : HotelDatabase.getAllReservations()) {

                        if (res.getGuest().getUsername().equals(this.getUsername())) {
                            System.out.println(res);
                            hasRes = true;
                        }
                    }
                    if(!hasRes) {
                        System.out.println("You currently have no reservations\n");
                        break;
                    }
                    break;

                case "4":
                    System.out.println("\n---Cancel Reservation---");
                    System.out.println("0.Go back\nEnter Reservation ID");
                    int cancelId = -1;
                    while(cancelId == -1) {
                        try {
                            cancelId = Integer.parseInt(sc.nextLine());

                        } catch (NumberFormatException e){
                                System.out.println("Please enter a valid reservation ID");
                            } catch (Exception e){
                                System.out.println("Unexpected error");
                            }
                        }
                    if(cancelId == 0)
                        break;
                    Reservation cancelRes = HotelDatabase.findReservationById(cancelId);
                    if(cancelRes == null)
                    {
                        System.out.println("Reservation does not exist");
                        break;
                    }
                    if(!cancelRes.getGuest().equals(this))
                        System.out.println("Access denied: You can only cancel your reservations ");
                    //only pending and confirmed reservations can be cancelled
                    if(!((cancelRes.getStatus() == ReservationStatus.PENDING) ||
                            (cancelRes.getStatus() == ReservationStatus.CONFIRMED))){
                        System.out.println("Reservation is not eligible for cancellation");
                    }
                    //if the guest is the one cancelling and the past condition
                    if(cancelRes.getGuest().equals(this) && ((cancelRes.getStatus() == ReservationStatus.PENDING) ||
                            (cancelRes.getStatus() == ReservationStatus.CONFIRMED)) ){
                        cancelRes.setStatus(ReservationStatus.CANCELLED);
                        cancelRes.getRoom().setAvailable(true);
                        System.out.println("Reservation " + cancelId + " has been cancelled\n");
                    }
                    else {
                        System.out.println("Reservation cannot be cancelled");
                    }
                    break;

                case "5":
                    System.out.println("\n---Check-out---");
                    System.out.print("0.Go back\nEnter Reservation ID to check out: ");
                    int outId = -1;
                    while(outId == -1){
                    try {
                        outId = Integer.parseInt(sc.nextLine());
                        if(outId == 0)
                            break;
                    } catch (NumberFormatException e){
                        System.out.println("Please enter a valid reservation ID");
                    } catch (Exception e){
                        System.out.println("Unexpected error");
                    }
                }

                    Reservation resToOut = HotelDatabase.findReservationById(outId);

                    if (resToOut != null && resToOut.getGuest().equals(this)) {
                        double cost = resToOut.calculateTotalCost();
                        System.out.println("Your total is: " + cost);

                        PaymentMethod chosenMethod = null;

                        while (chosenMethod == null) {
                            System.out.println("\n--- Choose Payment Method ---");
                            System.out.println("1. Cash");
                            System.out.println("2. Credit Card");
                            System.out.println("3. Online");
                            System.out.print("Selection: ");

                            String c = sc.nextLine();

                            switch (c) {
                                case "1":
                                    chosenMethod = PaymentMethod.CASH;
                                    break;
                                case "2":
                                    chosenMethod = PaymentMethod.CREDIT_CARD;
                                    break;
                                case "3":
                                    chosenMethod = PaymentMethod.ONLINE;
                                    break;
                                default:
                                    System.out.println("Invalid selection, please enter 1, 2, or 3");
                                    break;
                            }
                        }
                        try {

                            if (this.pay(cost)) {
                                resToOut.setStatus(ReservationStatus.CHECKED_OUT);
                                int invoiceNo = HotelDatabase.getAllInvoices().size() + 1 ;
                                Invoice inv = new Invoice(invoiceNo, resToOut);
                                inv.setPaymentMethod(chosenMethod);
                                inv.printInvoice();
                            }
                        } catch (Exception e) {
                            if((e instanceof InsufficientBalanceException) ||
                                    (e instanceof InvalidAmountException)) {
                                System.out.println("Payment failed: " + e.getMessage());
                            }
                            else {System.out.println("Unexpected Error");}
                        }
                    } else {
                        System.out.println("Invalid Reservation ID\n");
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

}