package data;

import booking.Invoice;
import booking.Reservation;
import enums.Gender;
import enums.ReservationStatus;
import enums.Role;
import models.*;

import java.io.*;
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
    private static Admin admin = new Admin("Admin1", "Y0u$ef", Role.ADMIN, LocalDate.of(1999,10,14), 40 );
    private static Receptionist receptionist = new Receptionist("Rec1","R3cepti@n", Role.RECEPTIONIST, 45,LocalDate.of(2000,9,15) );

    private static final String DATA_FILE = "vespera_data.dat";
    private static long lastLoadTime = 0;

    // Dummy data (runs automatically by using static)
    static {
        if (!loadFromFile()) {
            roomTypes.add(new RoomType("Single", 60.0, 1,"Standard single bed room"));
            roomTypes.add(new RoomType("Double", 90.0, 2,"Two beds room" ));
            roomTypes.add(new RoomType("Suite", 150.0, 4,"Luxury suite with living area"));
            roomTypes.add(new RoomType("Penthouse", 10000.0, 6,"Ultra-luxury penthouse with private pool"));

            amenities.add(new Amenity("WiFi", "High speed internet", 20.0));
            amenities.add(new Amenity("TV", "Smart TV", 10.0));
            amenities.add(new Amenity("Mini-bar", "Drinks and snacks", 30.0));

            rooms.add(new Room(101, 1, roomTypes.get(0)));
            rooms.add(new Room(102, 1, roomTypes.get(1)));
            rooms.add(new Room(201, 2, roomTypes.get(2)));
            rooms.add(new Room(301, 3, roomTypes.get(3)));

            // Assign default amenities to rooms
            rooms.get(0).addAmenity(amenities.get(0)); // Room 101: WiFi
            rooms.get(1).addAmenity(amenities.get(0)); // Room 102: WiFi
            rooms.get(1).addAmenity(amenities.get(1)); // Room 102: TV
            rooms.get(2).addAmenity(amenities.get(0)); // Room 201: WiFi
            rooms.get(2).addAmenity(amenities.get(1)); // Room 201: TV
            rooms.get(2).addAmenity(amenities.get(2)); // Room 201: Mini-bar
            rooms.get(3).addAmenity(amenities.get(0)); // Room 301: WiFi
            rooms.get(3).addAmenity(amenities.get(1)); // Room 301: TV
            rooms.get(3).addAmenity(amenities.get(2)); // Room 301: Mini-bar

            Guest g = new Guest("Ahmed", "@4med", LocalDate.of(2000,10,7), 2000.0, "21 Street", Gender.MALE, "WiFi");
            guests.add(g);
        }
        saveToFile();
    }

    // Shared file sync across processes
    public static synchronized void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(guests);
            oos.writeObject(rooms);
            oos.writeObject(reservations);
            oos.writeObject(invoices);
            oos.writeObject(roomTypes);
            oos.writeObject(amenities);
            oos.writeObject(admin);
            oos.writeObject(receptionist);
            oos.flush();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static synchronized boolean loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return false;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            guests.clear(); rooms.clear(); reservations.clear();
            invoices.clear(); roomTypes.clear(); amenities.clear();
            guests.addAll((ArrayList<Guest>) ois.readObject());
            rooms.addAll((ArrayList<Room>) ois.readObject());
            reservations.addAll((ArrayList<Reservation>) ois.readObject());
            invoices.addAll((ArrayList<Invoice>) ois.readObject());
            roomTypes.addAll((ArrayList<RoomType>) ois.readObject());
            amenities.addAll((ArrayList<Amenity>) ois.readObject());
            admin = (Admin) ois.readObject();
            receptionist = (Receptionist) ois.readObject();
            lastLoadTime = System.currentTimeMillis();
            return true;
        } catch (Exception e) { return false; }
    }

    // Poll file for changes made by other processes
    public static synchronized boolean checkForUpdates() {
        File f = new File(DATA_FILE);
        if (f.exists() && f.lastModified() > lastLoadTime + 500) {
            return loadFromFile();
        }
        return false;
    }

    //GETTERS
    public static List<Room> getAvailableRooms(LocalDate start, LocalDate end){
        List<Room> available = new ArrayList<>();
        for (Room r : getAllRooms()) {
            if (!HotelDatabase.isRoomClashing(r.getRoomNumber(),start, end))
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
        saveToFile();
    }
    public static void addRoom(Room room){
        rooms.add(room);
        saveToFile();
    }
    public static void addReservation(Reservation reservation){
        reservations.add(reservation);
        saveToFile();
    }
    public static void addInvoice(Invoice invoice){
        invoices.add(invoice);
        saveToFile();
    }

    //FINDERS
    public static Guest findGuestByUsername(String username){
        for (Guest g : guests){
            if (g.getUsername().equalsIgnoreCase(username)){
                return g;
            }
        }
        return null;
    }

    public static Invoice findInvoiceById(int id) {
        for (Invoice invoice : invoices) {
            if (invoice.getInvoiceID() == id)
            return invoice;
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
    public static boolean isRoomAvailableForDates(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation r : reservations) {
            if (r.getRoom().equals(room)
                    && r.getStatus() != ReservationStatus.CANCELLED
                    && r.overlapsWith(checkIn, checkOut)) {
                return false;
            }
        }
        return true;
    }

    // ====================== ROOM TYPE METHODS ======================
    public static void addRoomType(RoomType rt) {
        roomTypes.add(rt);
        saveToFile();
    }

    public static List<RoomType> getAllRoomTypes() {
        return new ArrayList<>(roomTypes);
    }

    public static void deleteRoomType(String name) {
        roomTypes.removeIf(rt -> rt.getName().equalsIgnoreCase(name));
        saveToFile();
    }

    // ====================== AMENITY METHODS ======================
    public static void addAmenity(Amenity a) {
        amenities.add(a);
        saveToFile();
    }

    public static List<Amenity> getAllAmenities() {
        return new ArrayList<>(amenities);
    }

    public static void deleteAmenity(String name) {
        amenities.removeIf(a -> a.getName().equalsIgnoreCase(name));
        saveToFile();
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
        saveToFile();
    }

    //Checks if the room is reserved

    public static boolean isRoomBusy(int roomNumber) {
        for (Reservation res : getAllReservations()) {
            if (res.getRoom().getRoomNumber() == roomNumber
		&& res.getStatus() != ReservationStatus.CANCELLED
    		&& res.getStatus() != ReservationStatus.CHECKED_OUT
    		&& res.getStatus() != ReservationStatus.COMPLETED
    		&& res.getStatus() != ReservationStatus.AWAITING_CHECKOUT) {
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
            || res.getStatus().equals(ReservationStatus.CHECKED_OUT) || res.getStatus().equals(ReservationStatus.COMPLETED)
            || res.getStatus().equals(ReservationStatus.AWAITING_CHECKOUT))) {

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
