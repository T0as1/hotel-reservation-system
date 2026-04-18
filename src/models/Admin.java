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

    //CRUD FOR AMENITY
    public void createAmenity(String name, String description) {

        allAmenities.add(new Amenity(name, description));
    }

    public ArrayList<Amenity> readAmenities()
    {
        return allAmenities;
    }

    public void updateAmenity(int index, String newName, String newDesc) {
        if (index >= 0 && index < allAmenities.size()) {
            allAmenities.set(index, new Amenity(newName, newDesc));
        }
    }

    public void deleteAmenity(Amenity amenity) {

        allAmenities.remove(amenity);
    }

    //CRUD FOR ROOM TYPE
    public void createRoomType(String name, double price, int capacity) {
        allRoomTypes.add(new RoomType(name, price, capacity));
    }

    public ArrayList<RoomType> readRoomTypes() {

        return allRoomTypes;
    }
    public void updateRoomType(String targetName, double newPrice) {
        for (int i = 0; i < allRoomTypes.size(); i++) {
            RoomType current = allRoomTypes.get(i);

            if (current.getName().equalsIgnoreCase(targetName)) {

                RoomType updated = new RoomType(targetName,newPrice,current.getCapacity());
                allRoomTypes.set(i, updated);

                System.out.println("Price updated for " + targetName);
                return;
            }
        }
        System.out.println("Room type not found!");
    }


    public void deleteRoomType(String name) {
        for (int i = allRoomTypes.size() - 1; i >= 0; i--) {
            RoomType rt = allRoomTypes.get(i);

            if (rt.getName().equalsIgnoreCase(name)) {
                allRoomTypes.remove(i);
                System.out.println(name + " was deleted.");
            }
        }
    }

    //CRUD FOR ROOM
    public void createRoom(int roomNumber, RoomType type) {

        allRooms.add(new Room(roomNumber, type));
    }

    public ArrayList<Room> readRooms() {

        return allRooms;
    }

    public void updateRoomNumber(int oldNumber, int newNumber) {

        for (int i = 0; i < allRooms.size(); i++) {
            Room current = allRooms.get(i);

            if (current.getRoomNumber() == oldNumber) {

                Room updatedRoom = new Room(newNumber, current.getRoomType());
                allRooms.set(i, updatedRoom);
                System.out.println("Room updated successfully from " + oldNumber + " to " + newNumber);
                return;
            }
        }
        System.out.println("Error: Room " + oldNumber + " was not found.");
    }

    public void deleteRoom(int targetRoomNumber) {
        for (int i = allRooms.size() - 1; i >= 0; i--) {
            Room r = allRooms.get(i);
            if (r.getRoomNumber() == targetRoomNumber) {
                allRooms.remove(i);
                System.out.println("Room " + targetRoomNumber + " was removed from the system.");
            }
        }
    }
}
