package models;

import enums.Role;
import data.HotelDatabase;
import booking.Reservation;
import exceptions.*;
import java.util.List;
import java.time.LocalDate;
import java.util.Scanner;

public abstract class Staff extends User{
    private Role role;
    private int workingHours;
    private LocalDate dateOfBirth;

    public Staff(String username, String password, Role role, int workingHours, LocalDate dateOfBirth) {
       super(username, password);
        setDateOfBirth(dateOfBirth);
        setRole(role);
        setWorkingHours(workingHours);
    }

    public List<Guest> viewAllGuests() {
        return HotelDatabase.getAllGuests();
    }

    public List<Room> viewAllRooms() {
        return HotelDatabase.getAllRooms();
    }

    public List<Reservation> viewAllReservations() {
        return HotelDatabase.getAllReservations();
    }

    //  SETTERS

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {

            throw new dobException("Date of birth cannot be null");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {

            throw new InvalidUsernameException("Username cannot be empty");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 5) {

            throw new InvalidPasswordException("Password must be at least 5 characters long");
        }
        this.password = password;
    }


    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role must be specified as ADMIN or RECEPTIONIST");
        }
        this.role = role;
    }

    public void setWorkingHours(int workingHours) {
        if (workingHours < 0) {

            throw new IllegalArgumentException("Working hours cannot be negative");
        }
        this.workingHours = workingHours;
    }

    // GETTERS
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Role getRole() { return role; }
    public int getWorkingHours() { return workingHours; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
}