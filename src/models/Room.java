package models;
import java.util.ArrayList;

public class Room {
     private final int roomNumber;
     private final int floor;
     private final RoomType roomType;
     private final ArrayList <Amenity> amenities;
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
}
