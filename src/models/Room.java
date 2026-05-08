package models;
import data.HotelDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
     private int roomNumber;
     private int floor;
     private RoomType roomType;
     private ArrayList <Amenity> amenities;
     private boolean isAvailable;

     public Room(int roomNumber,int floor , RoomType roomType) {
         if (roomNumber <= 0)
             throw new IllegalArgumentException("Invalid room number");

         if (roomType == null)
             throw new IllegalArgumentException("Room type cannot be null");
         this.floor = floor;

         this.roomNumber = roomNumber;
         this.roomType = roomType;
         this.amenities = new ArrayList<>();
         this.isAvailable = true;
     }
    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public ArrayList<Amenity> getAmenities() {
        return amenities;
    }


    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setAmenities(ArrayList<Amenity> amenities) {
        this.amenities = amenities;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isAvailable() {

         return isAvailable;
    }

    public void book()
    {
        this.isAvailable = false;

    }

    public void release()
    {
        this.isAvailable = true;

    }

    public int getFloor() {
        return floor;
    }

    public void addAmenityToRoom(String amenity) {
        //check to ensure the same thing is not added twice to the same room
        for (Amenity a : this.amenities) {
            if (a.getName().equalsIgnoreCase(amenity)) {
                System.out.println("Room already has this amenity");
                return;
            }
        }


        Amenity roomAmenity = HotelDatabase.getAmenityByName(amenity);

        if (roomAmenity != null) {
            this.amenities.add(roomAmenity);
            System.out.println(amenity + " added successfully");
        }
    }


    @Override
    public String toString(){
         return "Room Number: " + getRoomNumber() + " | Floor: " + getFloor() + " | Room Type: "
                 + getRoomType() + " | Amenities: " + getAmenities() + " | Status: " + (isAvailable() ? "Available"
    : "Occupied");
    }
}
