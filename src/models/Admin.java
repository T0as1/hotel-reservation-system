package models;

import data.HotelDatabase;
import enums.Role;
import interfaces.Manageable;

import java.time.LocalDate;
import java.util.ArrayList;

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
        for (RoomType rt : HotelDatabase.getAllRoomTypes()) {
            if (rt.getName().equalsIgnoreCase(name)) {
                // You need setters first (see below)
                rt.setPricePerNight(newPrice);
                rt.setDescription(newDescription);
                System.out.println("RoomType updated successfully.");
                return;
            }
        }
        System.out.println("RoomType not found.");
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
        for (Amenity a : HotelDatabase.getAllAmenities()) {
            if (a.getName().equalsIgnoreCase(name)) {
                a.setDescription(newDesc);
                a.setAdditionalCost(newCost);
                System.out.println("Amenity updated successfully.");
                return;
            }
        }
        System.out.println("Amenity not found.");
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