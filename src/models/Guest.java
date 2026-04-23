package models;

import data.HotelDatabase;
import enums.ReservationStatus;
import exceptions.*;
import enums.Gender;
import interfaces.Payable;
import booking.Invoice;
import booking.Reservation;

import java.util.ArrayList;
import java.util.List;


import java.time.LocalDate;

public class Guest implements Payable {
    // data fields
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private double balance;
    private String address;
    private Gender gender;
    private String roomPreferences;

    //Constructors
    public Guest(){

    }

    public Guest(String username, String password, LocalDate dateOfBirth,
                 double balance, String address, Gender gender,String roomPreferences ){
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.balance = balance;
        this.address = address;
        this.gender = gender;
        this.roomPreferences = roomPreferences;
    }

    // setters & getters, password shouldn't have a getter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
        if (username == null || username.isBlank()) {
            throw new EmptyUserNameException("Username cannot be empty");
        }
    }

    //password setter, when called in main, password requirements
    //(1 letter 1 digit 1 special and at least 5 chars)
    //must appear to the user BEFORE writing the password

    public void setPassword(String password) throws InvalidPasswordException {
        this.password = password;
        validatePassword(password);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        if (dateOfBirth.getYear() > 2008)
            throw new DobException("Date of birth is invalid, only 18+ are allowed to register");
        if (dateOfBirth.getYear() < 1900 )
            throw new DobException("Date of birth can not be earlier than 1900");
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
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
        this.roomPreferences = roomPreferences;
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

    private void validatePassword (String p){
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasLetter = false;

        if (p == null || p.isBlank()) {
            throw new InvalidPasswordException("Password cannot be empty");
        }
        if (p.length() < 5){
            throw new InvalidPasswordException("Password must contain at least 5 characters");
        }

        // turn password to a character array and loops
        // through it checking the type of every character

        for(char c: p.toCharArray()){
            if(Character.isLetter(c)){
                hasLetter = true;
            }
            else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            else{
                hasSpecial = true;
            }
        }
        if(!hasDigit){
            throw  new InvalidPasswordException("Password must have at least one digit: 0 - 9");
        }
        if(!hasLetter){
            throw new InvalidPasswordException("Password must include at least one letter: a - z or A - Z");
        }

        // if neither a letter nor a digit, then it is a special character

        if(!hasSpecial){
            throw new InvalidPasswordException("Password must have at least one special character: " +
                    "@ # $ % ^ & * ( ) _ - + = ! ? . , ; : [ ] { } ( ) < > / |");
        }
    }

    public boolean register(String username, String password, LocalDate dob, String address, Gender gender) {
        if (HotelDatabase.findGuestByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        this.setUsername(username);
        this.setPassword(password);
        this.setDateOfBirth(dob);
        this.setAddress(address);
        this.setGender(gender);
        this.setBalance(1000); // default starting balance, change if you want
        HotelDatabase.addGuest(this);
        return true;
    }

    public boolean login(String username, String password){
        Guest g = HotelDatabase.findGuestByUsername(username);
        if (g == null) {
            System.out.println("User not found.");
            return false;
        }

        if (!g.password.equals(password)) {
            System.out.println("Incorrect password.");
            return false;
        }

        return true;
    }

    public List<Room> viewAvailableRooms(){
       return HotelDatabase.getAvailableRooms();
    }

    public boolean makeReservation(Reservation res){
        if (!res.getRoom().isAvailable()) {
            throw new IllegalArgumentException("Room is not available");
        }
        for (Reservation existing : HotelDatabase.getAllReservations()) {
            if (existing.getRoom().getRoomNumber() == res.getRoom().getRoomNumber()
                    && existing.getStatus() != ReservationStatus.CANCELLED
                    && existing.overlapsWith(res.getCheckInDate(), res.getCheckOutDate())) {

                throw new IllegalArgumentException("Room already booked for these dates");
            }
        }

        res.getRoom().book(); //mark room as booked
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

    public boolean cancelReservation(String reservationId) {
        int reservationIdInt = Integer.parseInt(reservationId);

        for (Reservation r : HotelDatabase.getAllReservations()) {
            if (r.getReservationID() == reservationIdInt) {

                if (!r.getGuest().getUsername().equals(this.username)) {
                    throw new IllegalStateException("You can only cancel your own reservation");
                }

                if (r.getStatus() == ReservationStatus.CHECKED_IN ||
                        r.getStatus() == ReservationStatus.CHECKED_OUT ||
                        r.getStatus() == ReservationStatus.COMPLETED) {
                    throw new IllegalStateException("This reservation can no longer be cancelled");
                }

                r.setStatus(ReservationStatus.CANCELLED);
                r.getRoom().release();
                return true;
            }
        }
        return false;
    }

        public Invoice checkout(Reservation res) {
            Invoice invoice = new Invoice(res);
            HotelDatabase.addInvoice(invoice);
            return invoice;
        }

}