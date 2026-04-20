package models;

import java.time.LocalDate;
import java.util.ArrayList;
import enums.Role;

public class Admin extends Staff {

    private static ArrayList<Room> allRooms = new ArrayList<>();
    private static ArrayList<RoomType> allRoomTypes = new ArrayList<>();
    private static ArrayList<Amenity> allAmenities = new ArrayList<>();

    public Admin(String username, String password, Role role, int workingHours, LocalDate dateOfBirth) {
        super(username, password, Role.ADMIN, workingHours, dateOfBirth);
    }

    // --- CRUD FOR AMENITY ---

    public void createAmenity(String name, String description, double additionalCost) {
        allAmenities.add(new Amenity(name, description, additionalCost));
    }

    public ArrayList<Amenity> readAmenities() {
        return allAmenities;
    }

    public void updateAmenity(int index, String newName, String newDesc, double newCost) {
        if (index >= 0 && index < allAmenities.size()) {
            allAmenities.set(index, new Amenity(newName, newDesc, newCost));
        }
    }

    public void deleteAmenity(Amenity amenity) {
        allAmenities.remove(amenity);
    }

    // --- CRUD FOR ROOM TYPE ---

    public void createRoomType(String name, double price, int capacity, String description) {
        // Updated to include description
        allRoomTypes.add(new RoomType(name, price, capacity, description));
    }

    public ArrayList<RoomType> readRoomTypes() {
        return allRoomTypes;
    }

    public void updateRoomType(String targetName, double newPrice, String newDesc) {
        for (int i = 0; i < allRoomTypes.size(); i++) {
            RoomType current = allRoomTypes.get(i);
            if (current.getName().equalsIgnoreCase(targetName)) {
                RoomType updated = new RoomType(targetName, newPrice, current.getCapacity(), newDesc);
                allRoomTypes.set(i, updated);
                return;
            }
        }
    }

    public void deleteRoomType(String name) {
        allRoomTypes.removeIf(rt -> rt.getName().equalsIgnoreCase(name));
    }

    // --- CRUD FOR ROOM ---

    public void createRoom(int roomNumber, int floor, RoomType type) {

        allRooms.add(new Room(roomNumber, floor, type));
    }

    public ArrayList<Room> readRooms() {
        return allRooms;
    }

    public void updateRoomNumber(int oldNumber, int newNumber) {
        for (int i = 0; i < allRooms.size(); i++) {
            Room current = allRooms.get(i);
            if (current.getRoomNumber() == oldNumber) {
                Room updatedRoom = new Room(newNumber, current.getFloor(), current.getRoomType());
                allRooms.set(i, updatedRoom);
                return;
            }
        }
    }

    public void deleteRoom(int targetRoomNumber) {
        allRooms.removeIf(r -> r.getRoomNumber() == targetRoomNumber);
    }
}
