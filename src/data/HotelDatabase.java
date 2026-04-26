package data;

import booking.Invoice;
import booking.Reservation;
import enums.Gender;
import enums.ReservationStatus;
import enums.Role;
import models.*;

import java.time.LocalDate;
import java.time.Month;
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

    // Admin and receptionist
    public static Admin admin = new Admin("Admin1", "Y0u$ef", Role.ADMIN, LocalDate.of(1999,10,14), 40 );
    public static Receptionist receptionist = new Receptionist("Rec1","R3cepti@n", Role.RECEPTIONIST, 45,LocalDate.of(2000,9,15) );

    // Dummy data (runs automatically by using static)
    static {

        // Dummy Room Types
        roomTypes.add(new RoomType("Single", 100.0, 1,"Standard single bed room"));
        roomTypes.add(new RoomType("Double", 150.0, 2,"Two beds room" ));
        roomTypes.add(new RoomType("Suite", 250.0, 4,"Luxury suite with living area"));

        // Dummy Amenities
        amenities.add(new Amenity("WiFi", "High speed internet", 20.0));
        amenities.add(new Amenity("TV", "Smart TV", 10.0));
        amenities.add(new Amenity("Mini-bar", "Drinks and snacks", 30.0));

        // Dummy Rooms (using the first RoomType)
        rooms.add(new Room(101, 1, roomTypes.get(0)));
        rooms.add(new Room(102, 1, roomTypes.get(1)));
        rooms.add(new Room(201,2,  roomTypes.get(2)));

        // Dummy Guest
        Guest g = new Guest("Ahmed", "@4med", LocalDate.of(2000,10,7), 2000.0, "21 Street", Gender.MALE, "WiFi");
        guests.add(g);

    }

    //GETTERS
    public static List<Room> getAvailableRooms(LocalDate start, LocalDate end){
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable() && !HotelDatabase.isRoomClashing(r.getRoomNumber(),start, end))
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
    public static List<Invoice> getAllInvoices(){return new ArrayList<>(invoices);}

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
            if (g.getUsername().equals(username)){
                return g;
            }
        }
        return null;
    }
    public static Room findRoomByNumber(int roomNumber){
        for (Room r : rooms){
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }
    public static User findUser(String u, String p){
        if (admin.login(u,p))
            return admin;
        if (receptionist.login(u,p))
            return receptionist;
        for(Guest g: guests)
            if(g.login(u,p))
                return  g;
        return null;
    }
    public static Reservation  findReservationById(int id){
        for (Reservation r: reservations) {
            if (r.getReservationID() == id){
                return r;
            }
        }
        return null;
    }
    public static RoomType findRoomTypeByName(String name){
        for(RoomType rt: getAllRoomTypes()){
            if(rt.getName().equals(name)){
                return rt;
            }
        }
        return null;
    }

    // ====================== ROOM TYPE METHODS ======================
    public static void addRoomType(RoomType rt) {
        roomTypes.add(rt);
    }

    public static List<RoomType> getAllRoomTypes() {
        return new ArrayList<>(roomTypes);
    }

    public static void deleteRoomType(String name) {
        roomTypes.removeIf(rt -> rt.getName().equalsIgnoreCase(name));
    }

    // ====================== AMENITY METHODS ======================
    public static void addAmenity(Amenity a) {
        amenities.add(a);
    }

    public static List<Amenity> getAllAmenities() {
        return new ArrayList<>(amenities);
    }

    public static void deleteAmenity(String name) {
        amenities.removeIf(a -> a.getName().equalsIgnoreCase(name));
    }

    public static Amenity getAmenityByName(String name) {

        for (Amenity a : amenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }

        return null;
    }

    public static String getAmenityUsage(String amenityName) {
        // Check fixed room amenities
        for (Room r : getAllRooms()) {
            for (Amenity a : r.getAmenities()) {
                if (a.getName().equalsIgnoreCase(amenityName)) {
                    return "Room " + r.getRoomNumber();
                }
            }
        }

        //Check optional reservation amenities
        for (Reservation res : getAllReservations()) {
            for (Amenity a : res.getSelectedAmenities()) {
                if (a.getName().equalsIgnoreCase(amenityName)) {
                    return "Reservation #" + res.getReservationID();
                }
            }
        }

        return null;
    }

    // ====================== ROOM METHODS ======================
    public static void deleteRoom(int roomNumber) {
        rooms.removeIf(r -> r.getRoomNumber() == roomNumber);
    }

    //Checks if the room is reserved

    public static boolean isRoomBusy(int roomNumber) {
        for (Reservation res : getAllReservations()) {
            if (res.getRoom().getRoomNumber() == roomNumber) {
                return true;
            }
        }
        return false;
    }

    //This method checks if new reservation clashes with another one in the same room at the same time

    public static boolean isRoomClashing(int roomNum, LocalDate newIn, LocalDate newOut) {
        for (Reservation res : getAllReservations()) {
            // if room number matches and reservation status is NOT: cancelled,checkedOut,completed
            if (res.getRoom().getRoomNumber() == roomNum && !(res.getStatus().equals(ReservationStatus.CANCELLED)
            || res.getStatus().equals(ReservationStatus.CHECKED_OUT) || res.getStatus().equals(ReservationStatus.COMPLETED))) {

                //if the room is the same, check if the new check-in date is before the old checkout date
                //and if the new check-out is after the old check-in date, this means a clash is found

                if (newIn.isBefore(res.getCheckOutDate()) && newOut.isAfter(res.getCheckInDate())) {
                    return true;
                }
            }
        }
        return false;
    }

}
