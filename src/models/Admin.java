package models;

import data.HotelDatabase;
import enums.Role;
import interfaces.Manageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin extends Staff implements Manageable {

    public Admin(String username, String password,Role role,  LocalDate dateOfBirth, int workingHours) {
        super(username, password, Role.ADMIN, workingHours, dateOfBirth);
    }

    // RoomType CRUD
    public void createRoomType(String name, double basePrice, int capacity, String description) {
        RoomType rt = new RoomType(name, basePrice, capacity, description);
        HotelDatabase.addRoomType(rt);
    }

    public ArrayList<RoomType> readRoomTypes() {
        return new ArrayList<>(HotelDatabase.getAllRoomTypes());
    }

    public void updateRoomType(String name, double newPrice, String newDescription) {
        System.out.println("RoomType updated: " + name);
    }

    public void deleteRoomType(String name) {
        HotelDatabase.deleteRoomType(name);
    }

    // Amenity CRUD
    public void createAmenity(String name, String description, double additionalCost) {
        Amenity a = new Amenity(name, description, additionalCost);
        HotelDatabase.addAmenity(a);
    }

    public ArrayList<Amenity> readAmenities() {
        return new ArrayList<>(HotelDatabase.getAllAmenities());
    }

    public void updateAmenity(String name, String newDesc, double newCost) {
        System.out.println("Amenity updated: " + name);
    }

    public void deleteAmenity(String name) {
        HotelDatabase.deleteAmenity(name);
    }

    // Room CRUD
    public void createRoom(int roomNumber, int floor, RoomType type) {
        Room room = new Room(roomNumber, floor, type);
        HotelDatabase.addRoom(room);
    }

    public ArrayList<Room> readRooms() {
        return new ArrayList<>(HotelDatabase.getAllRooms());
    }

    public void deleteRoom(int roomNumber) {
        HotelDatabase.deleteRoom(roomNumber);
    }

    public void showDashboard(Scanner sc){
        System.out.println("---Admin Dashboard---");
    }

    @Override
    public boolean login(String username, String password) {
        if (this.getUsername().equals(username) && this.password.equals(password))
            return true;
        return false;
    }

    @Override
    public void create(Object obj) {
        if (obj instanceof RoomType rt) {
            HotelDatabase.addRoomType(rt);
        }
    }

    @Override
    public Object read(String id) {
        return null;
    }

    @Override
    public void update(Object obj) {
    }

    @Override
    public void delete(String id) {
    }
}