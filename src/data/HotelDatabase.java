package data;

import booking.Invoice;
import booking.Reservation;
import enums.RoomType;
import models.Amenity;
import models.Guest;
import models.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HotelDatabase {
    //Attributes
    private static final ArrayList<Guest> guests = new ArrayList<>();
    private static final ArrayList<Room> rooms = new ArrayList<>();
    private static final ArrayList<Reservation> reservations = new ArrayList<>();
    private static final ArrayList<Invoice> invoices = new ArrayList<>();
    private static final ArrayList<RoomType> roomTypes = new ArrayList<>();
    private static final ArrayList<Amenity> amenities = new ArrayList<>();

    // Dummy data (runs automatically)
    static {
        System.out.println("✅ HotelDatabase: Loading dummy data...");

        // Dummy Room Types
        roomTypes.add(new RoomType("Single", 100.0, 1, "Single bed room"));
        roomTypes.add(new RoomType("Double", 150.0, 2, "Two beds room"));
        roomTypes.add(new RoomType("Suite", 250.0, 4, "Luxury suite"));

        // Dummy Amenities
        amenities.add(new Amenity("WiFi", "High speed internet", 0.0));
        amenities.add(new Amenity("TV", "Smart TV", 10.0));
        amenities.add(new Amenity("Mini-bar", "Drinks and snacks", 25.0));

        // Dummy Rooms (using the first RoomType)
        rooms.add(new Room("101", 1, true, roomTypes.get(0)));
        rooms.add(new Room("102", 1, true, roomTypes.get(1)));
        rooms.add(new Room("201", 2, true, roomTypes.get(2)));

        System.out.println("✅ HotelDatabase: Dummy data loaded successfully!");
    }

    //GETTERS
    public static List<Room> getAvailableRooms(){
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            available.add(r);
        }
        return available;
    }
    public static List<Guest> getAllGuests(){
        return new ArrayList<>(guests);
    }
    public static List<Room> getAllRooms(){
        return new ArrayList<>(rooms);
    }
    public static List<Reservation> getAllReservations(){
        return new ArrayList<>(reservations);
    }

    //ADDERS
    public static void addGuest(Guest guest){
        guests.add(guest);
    }
    public static void addRoom(Room room){
        rooms.add(room);
    }
    public static void addReservation(Reservation reservation){
        reservations.add(reservation);
    }
    public static void addInvoice(Invoice invoice){
        invoices.add(invoice);
    }

    //FINDERS
    public static Guest findGuestByUsername(String username){
        for (Guest g : guests){
            if (g.getUsername().equal(username)){
                return g;
            }
        }
        return null;
    }
    public static Room findRoomByNumber(String roomNumber){
        for (Room r : rooms){
            if (r.getRoomNumber().equals(roomNumber)) {
                return r;
            }
        }
        return null;
    }

}
