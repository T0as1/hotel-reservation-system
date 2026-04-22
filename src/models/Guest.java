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
import java.util.Scanner;

public class Guest extends User implements Payable {
    // data fields

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


    public boolean register(String username,String password, LocalDate dob, String address, Gender gender){
        this.setUsername(username);
        this.setPassword(password);
        this.setDateOfBirth(dob);
        this.setAddress(address);
        this.setGender(gender);
        return true;
    }

    public boolean login(String username, String password){
        Guest g = HotelDatabase.findGuestByUsername(username);
        if (g!= null && g.password.equals(password))
            return true;
        return false;
    }

    public List<Room> viewAvailableRooms(){
       return HotelDatabase.getAvailableRooms();
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

    public void showDashboard(Scanner sc){
        System.out.println("---Guest Dashboard---");
    }

}