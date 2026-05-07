package models;

import booking.Reservation;
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
        if(HotelDatabase.findRoomTypeByName(name) == null)
        {
            RoomType rt = new RoomType(name, basePrice, capacity, description);
            HotelDatabase.addRoomType(rt);
        }
        else
            System.out.println("Room type already exists");
    }

    public ArrayList<RoomType> readRoomTypes() {
        return new ArrayList<>(HotelDatabase.getAllRoomTypes());
    }

    public void updateRoomType(String name, double newPrice, int newCapacity, String newDescription) {
        for (RoomType rt : HotelDatabase.getAllRoomTypes()) {

            if (rt.getName().equalsIgnoreCase(name)) {

                rt.setPricePerNight(newPrice);
		rt.setCapacity(newCapacity);
                rt.setDescription(newDescription);
                System.out.println("Room type updated successfully.");
                return;
            }
        }
        System.out.println("RoomType not found");
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
                System.out.println("Amenity updated successfully");
                return;
            }
        }
        System.out.println("Amenity not found.");
    }

    public void deleteAmenity(String name) {
        HotelDatabase.deleteAmenity(name);
    }

    // Room CRUD
    public void createRoom(int roomNumber, int floor, RoomType type,
                           ArrayList<Amenity> amenities, boolean isAvailable) {
        if(HotelDatabase.findRoomByNumber(roomNumber) == null)
        {
            Room room = new Room(roomNumber, floor, type);
            HotelDatabase.addRoom(room);
        }

    }

    public ArrayList<Room> readRooms() {
        return new ArrayList<>(HotelDatabase.getAllRooms());
    }

    public void updateRoom(int roomNumber, int floor,
                           ArrayList<Amenity> amenities, boolean isAvailable ){

        for(Room r: HotelDatabase.getAllRooms()){
            if(r.getRoomNumber() == roomNumber){
                r.setRoomNumber(roomNumber);
                r.setFloor(floor);
                r.setAmenities(amenities);
                r.setAvailable(isAvailable);
            }
        }
    }

    public void deleteRoom(int roomNumber) {
        HotelDatabase.deleteRoom(roomNumber);
    }

    public void showDashboard(Scanner sc){
        System.out.println("---Admin Dashboard---");
        boolean loggedIn = true;
        while(loggedIn){
            System.out.println("""
                    1.Manage Rooms
                    2.Manage Room Types
                    3.Manage Amenities
                    4.Logout""");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("\n---Manage Rooms");
                    manageRooms(sc);
                    break;

                case "2":
                    System.out.println("\n---Manage Room Types---");
                    manageRoomTypes(sc);
                    break;

                case "3":
                    System.out.println("\n---Manage Amenities---");
                    manageAmenities(sc);
                    break;

                case "4": System.out.println("Logging out...");
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Invalid input, try again");
            }
        }
    }


    public void manageRoomTypes(Scanner sc){
        while (true){
            System.out.println("""
                                1.Add Room Type
                                2.View Room Types
                                3.Update Room Type
                                4.Delete Room Type
                                0.Go back""");
            String c = sc.nextLine();
            switch (c)
            {
                case "0":
                    return;
                case "1":
                    String inputRtN = null;
                    double inputRtBp = 0.0;
                    int inputRtC = 0;
                    String inputRtD = null;
                    while(true) //Name loop
                    {

                        System.out.println("0.Go back\nEnter new Room Type name:");

                        try{
                            inputRtN = sc.nextLine();
                            if(inputRtN.equals("0"))
                                return;
                            break;
                        }
                        catch (Exception e){System.out.println("Unexpected error");}}
                    while(true) // Price loop
                    {
                        System.out.println("Enter base price:");
                        try {
                            inputRtBp = Double.parseDouble(sc.nextLine());
                            if(inputRtBp < 0){
                                System.out.println("Price cannot be negative");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Enter a valid price");
                        } catch (Exception e) {
                            System.out.println("Unexpected error");
                        }
                    }
                    while(true) // Capacity loop
                    {
                        System.out.println("Enter capacity:");
                        try {
                            inputRtC = Integer.parseInt(sc.nextLine());
                            if(inputRtC < 0){
                                System.out.println("Capacity cannot be negative");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Enter a valid capacity");
                            continue;
                        }
                        catch (Exception e){System.out.println("Unexpected error");}
                    }
                    while(true) // Description loop
                    {
                        System.out.println("Enter description");
                        try {
                            inputRtD = sc.nextLine();
                            break;
                        } catch (Exception e) {
                            System.out.println("Unexpected error");
                        }
                    }
                    createRoomType(inputRtN,inputRtBp,inputRtC,inputRtD);
                    System.out.println("Created Room type successfully");
                    break;

                case "2":
                    System.out.println("\n---List of Room Types---");
                    for(RoomType rt : HotelDatabase.getAllRoomTypes() )
                        System.out.println(rt);
                    System.out.println("-------------------------");
                    break;

                case "3":
                    String rtNUpdated = null;
                    double rtBpUpdated = 0.0;
                    int rtCUpdated = 0;
                    String rtDUpdated = null;
                    while (true)
                    {
                        System.out.println("0.Go Back\nEnter the exact room type name to be updated:");
                        try {
                            rtNUpdated = sc.nextLine();
                            if(rtNUpdated.equals("0"))
                                return;
                            if(HotelDatabase.findRoomTypeByName(rtNUpdated) == null){
                                System.out.println("Error: Room type " + rtNUpdated + " does not exist");
                                continue;
                            }
                            for (Room r : HotelDatabase.getAllRooms()) {
                                if (r.getRoomType().getName().equalsIgnoreCase(rtNUpdated)) {
                                    // Check if THIS specific room is busy
                                    if (HotelDatabase.isRoomBusy(r.getRoomNumber())) {
                                        System.out.println("Error: Cannot update room type " + rtNUpdated
                                                + ", room " + r.getRoomNumber() + " is currently reserved.");
                                        return;
                                    }
                                }
                            }
                            break;
                        } catch (Exception e){
                            System.out.println("Unexpected error");}
                    }
                    while (true)
                    {
                        System.out.println("Enter new price:");
                        try{
                            rtBpUpdated = Double.parseDouble(sc.nextLine());
                            if(rtBpUpdated < 0){
                                System.out.println("Price cannot be negative");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e){
                            System.out.println("Enter a valid price");

                        } catch (Exception e)
                        {
                            System.out.println("Unexpected error");
                        }
                    }
                    while (true)
                    {
                        System.out.println("Enter new capacity");
                        try{
                            rtCUpdated = Integer.parseInt(sc.nextLine());
                            if(rtCUpdated < 0){
                                System.out.println("Capacity cannot be negative");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e){
                            System.out.println("Enter a valid capacity");

                        } catch (Exception e)
                        {
                            System.out.println("Unexpected error");
                        }
                    }
                    while(true)
                    {
                        System.out.println("Enter new description");
                        try {
                            rtDUpdated = sc.nextLine();
                            break;
                        } catch (Exception e) {
                            System.out.println("Unexpected error");
                        }
                    }
                    updateRoomType(rtNUpdated,rtBpUpdated,rtCUpdated,rtDUpdated);
                    break;

                case "4":
                    String rtNDelete = null;
                    while (true) {
                       try {
                           System.out.println("0.Go back\nEnter the exact room type name to be deleted:");
                           rtNDelete = sc.nextLine();

                           if (rtNDelete.equals("0"))
                           {
                               break;
                           }
                           if(HotelDatabase.findRoomTypeByName(rtNDelete) == null)
                           {
                               System.out.println("Error: Room type not found");
                               continue;
                           }

                           boolean isUsed = false;
                           for (Room r : HotelDatabase.getAllRooms()) {

                               if (r.getRoomType().getName().equalsIgnoreCase(rtNDelete)) {
                                   System.out.println("Error: Cannot delete room type " + rtNDelete + " " +
                                           ", room " + r.getRoomNumber() + " is currently using it");
                                   isUsed = true;
                                   break;
                               }
                           }
                           if(!isUsed)
                           {
                               deleteRoomType(rtNDelete);
                               System.out.println("Deleted successfully");
                               break;
                           }

                       } catch (Exception e) {
                           System.out.println("Unexpected error");
                       }
                   }
                    break;

                default:
                    System.out.println("Invalid choice, try again");
                    break;
            }

        }
    }
    public void manageRooms(Scanner sc){
        ArrayList<Amenity> amenities= new ArrayList<>();
        boolean isAvailable = true;
        while (true){
            System.out.println("""
                                1.Add Room
                                2.View Rooms
                                3.Update Room
                                4.Delete Room
                                0.Go Back""");
            String c = sc.nextLine();
            if(c.equals("0"))
                return;
            switch (c){

                case "1":
                    int roomNumber = 0;
                    int floor = 0;
                    RoomType type = null;

                    while(true){
                        System.out.println("0.Go back\nEnter room number (max 500):");
                        try{
                            roomNumber = Integer.parseInt(sc.nextLine());
                            if(roomNumber == 0)
                                return;
                            if(HotelDatabase.findRoomByNumber(roomNumber) != null)
                            {
                                System.out.println("Room already exists ");
                                continue;
                            }
                            if(roomNumber < 1 || roomNumber > 500 ){
                                System.out.println("Enter a value between 1-500");
                                continue;
                            }
                            break;
                        }
                        catch(NumberFormatException e){
                            System.out.println("Enter a valid room number");
                        }
                        catch (Exception e){
                            System.out.println("Unexpected error");
                        }
                    }
                    while(true){
                        System.out.println("\nEnter floor number (max 5)");
                        try{
                            floor = Integer.parseInt(sc.nextLine());
                            if(floor < 1 || floor > 5){
                                System.out.println("Enter a value between 1 and 5");
                                continue;
                            }
                            break;
                        }
                        catch (NumberFormatException e){
                            System.out.println("Enter a valid floor number");
                        }
                        catch (Exception e){
                            System.out.println("Unexpected error");
                        }
                    }
                    while(true){
                        try{
                            System.out.println("\nEnter a type from the following room types:");
                            for (RoomType rt: HotelDatabase.getAllRoomTypes())
                                System.out.println(rt);
                            String typeName = sc.nextLine();
                            type = HotelDatabase.findRoomTypeByName(typeName);
                            if(type == null){
                                System.out.println("Room type not found");
                                continue;
                            }
                            break;
                        }
                        catch (Exception e) {
                            System.out.println("Unexpected error");
                        }
                    }
                    createRoom(roomNumber, floor, type,amenities, true);

                    System.out.println("Room successfully added");
                    break;

                case "2": for (Room r : HotelDatabase.getAllRooms()){
                    System.out.println(r);
                }
                    System.out.println("-----------------------");
                    break;

                case "3":
                    while (true){
                    System.out.print("0.Go back\nEnter Room Number to update:");
                    int updateRoomNumber = Integer.parseInt(sc.nextLine());
                    if(updateRoomNumber == 0){
                        return;
                    }
                    Room roomToUpdate = HotelDatabase.findRoomByNumber(updateRoomNumber);

                    if (roomToUpdate == null) {
                        System.out.println("Error: Room not found.");
                        continue;
                    }

                    System.out.print("Enter new floor Number (Max 5):");
                    int newFloor = Integer.parseInt(sc.nextLine());
                    if (newFloor < 1 || newFloor > 5) {
                        System.out.println("Error: Floor number must be 1-5");
                        continue;
                    }
                    System.out.println("Room types: ");
                    for(RoomType rt: HotelDatabase.getAllRoomTypes()){
                        System.out.println(rt);
                    }
                    System.out.println("------------------------");
                    System.out.print("Enter new Room Type Name: ");
                    String typeName = sc.nextLine();
                    RoomType newRt = HotelDatabase.findRoomTypeByName(typeName);

                    if (newRt == null) {
                        System.out.println(" Error: Room Type " + typeName + " not found.");
                        continue;
                    }
                    if (HotelDatabase.isRoomBusy(updateRoomNumber)) {
                        System.out.println("Error: Cannot update Room " + updateRoomNumber + " because it is currently reserved or occupied.");
                        continue;
                    }

                    updateRoom(updateRoomNumber, newFloor, amenities, isAvailable);
                    HotelDatabase.findRoomByNumber(updateRoomNumber).setRoomType(newRt);

                    System.out.println("Success: Room " + updateRoomNumber + " updated to Floor " + newFloor + " and Type " + newRt.getName());
                    break;
                }
                    break;

                case "4":
                    int deleteRoomNumber = 0;
                    while(true){
                        System.out.println("0.Go back\n Enter room number to delete");
                        try{
                            deleteRoomNumber = Integer.parseInt(sc.nextLine());

                            if( deleteRoomNumber == 0){
                                break;
                            }
                            Room roomToDelete = HotelDatabase.findRoomByNumber(deleteRoomNumber);
                            if(roomToDelete == null){
                                System.out.println("Error: Room not found");
                                continue;
                            }
                            else if(HotelDatabase.isRoomBusy(deleteRoomNumber) || !roomToDelete.isAvailable()){
                                System.out.println("Error: Cannot delete room " + deleteRoomNumber
                                + " because it is occupied or reserved");
                            }
                            else {
                                deleteRoom(deleteRoomNumber);
                                System.out.println("Room " + deleteRoomNumber +" successfully deleted");
                                break;
                            }
                        }
                        catch (NumberFormatException e){
                            System.out.println("Enter a valid room number");
                        }
                        catch (Exception e){
                            System.out.println("Unexpected error");
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid input, try again");

            }
        }
    }
    public void manageAmenities(Scanner sc){
        while(true)
        {
            System.out.println("""
                    1.Add Amenity
                    2.View Amenities
                    3.Update Amenity
                    4.Delete Amenity
                    0.Go back""");
            String c = sc.nextLine();
            switch (c) {
                case "0":
                    return;
                case "1":
                    String createAmenityName = null;
                    String createAmenityDesc = null;
                    double createAmenityAddCost = 0.0;
                    while(true) //name loop
                        {
                            System.out.println("0.Go back\nEnter amenity name:");
                            try {
                                createAmenityName = sc.nextLine();
                                if (createAmenityName.equals("0"))
                                    return;
                                if (HotelDatabase.getAmenityByName(createAmenityName) != null) {
                                    System.out.println("Amenity " + createAmenityName + " already exists");
                                    continue;
                                }
                                break;
                            } catch (Exception e) {
                                System.out.println("Unexpected error");
                            }
                        }
                    while(true) //desc loop
                        {
                            System.out.println("Enter amenity description:");
                            try{
                                createAmenityDesc = sc.nextLine();
                                break;
                            } catch (Exception e) {
                                System.out.println("Unexpected error");;
                            }

                        }
                    while(true) //addCost loop
                        {
                            System.out.println("Enter amenity additional cost:");
                            try{

                                createAmenityAddCost = Double.parseDouble(sc.nextLine());
                                if(createAmenityAddCost < 0 ){
                                    System.out.println("Cost cannot be negative");
                                    continue;
                                }
                                break;
                            } catch (NumberFormatException e){
                                System.out.println("Enter a valid cost");
                            }
                            catch (Exception e){
                                System.out.println("Unexpected error");
                            }
                        }
                    createAmenity(createAmenityName,createAmenityDesc,createAmenityAddCost);
                    System.out.println("Amenity "+ createAmenityName + " created successfully");
                    break;

                case "2":
                    System.out.println("\n---List of Amenities---");
                    for (Amenity a : HotelDatabase.getAllAmenities())
                        System.out.println(a);
                    System.out.println("-----------------------");
                    break;

                case "3":
                    String updateAmenityName = null;
                    String updateAmenityDesc = null;
                    double updateAmenityAddCost = 0.0;
                    while(true)
                    {
                        System.out.println("0.Go back\nEnter amenity name to be updated ");
                        try {
                        updateAmenityName = sc.nextLine();
                        if (updateAmenityName.equals("0")) {
                            return;
                        }
                        if (HotelDatabase.getAmenityByName(updateAmenityName) == null) {
                            System.out.println("Error: Amenity not found");
                            continue;
                        }
                        //checks if the amenity is used in either a room or a reservation
                        String usageLocation = HotelDatabase.getAmenityUsage(updateAmenityName);
                        if (usageLocation != null) {
                            System.out.println("Error: Cannot update " + updateAmenityName + ", it is currently tied to " + usageLocation);
                            continue;
                        }

                        break;

                    } catch (Exception e) {
                            System.out.println("Unexpected error");;
                    }
                    }
                    while (true)
                    {
                        System.out.println("Enter new Amenity description: ");
                        try{
                            updateAmenityDesc = sc.nextLine();
                            break;
                        } catch (Exception e) {
                            System.out.println("Unexpected error");
                        }
                    }
                    while (true)
                    {
                        try {
                            updateAmenityAddCost = Double.parseDouble(sc.nextLine());
                            if(updateAmenityAddCost < 0 ){
                                System.out.println("Cost cannot be negative");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e){
                            System.out.println("Enter a valid cost");
                        }
                        catch (Exception e){
                            System.out.println("Unexpected error");
                        }
                    }
                    updateAmenity(updateAmenityName,updateAmenityDesc,updateAmenityAddCost);
                    break;

                case "4":
                    String deleteAmenityName = null;
                    while(true) {
                        System.out.println("0.Go back\nEnter name of the amenity to be deleted");
                    try {
                        deleteAmenityName = sc.nextLine();

                        if (deleteAmenityName.equals("0")) {
                            return;
                        }
                        if (HotelDatabase.getAmenityByName(deleteAmenityName) == null) {
                            System.out.println("Error: Amenity not found");
                            continue;
                        }

                        String usageLocation = HotelDatabase.getAmenityUsage(deleteAmenityName);
                        if (usageLocation != null) {
                            System.out.println("Error: Cannot delete " + deleteAmenityName + " because it is in use by " + usageLocation);
                            continue;
                        }

                        deleteAmenity(deleteAmenityName);
                        System.out.println("Amenity " + deleteAmenityName + " deleted successfully");
                        break;


                    } catch (Exception e) {
                        System.out.println("Unexpected error");
                    }
                }
                    break;

                default:
                    System.out.println("Invalid input, try again");
                    break;
            }
        }
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