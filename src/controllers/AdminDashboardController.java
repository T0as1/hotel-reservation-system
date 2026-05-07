package controllers;

import booking.Reservation;
import data.HotelDatabase;
import models.*;
import utils.AnimationUtils;
import utils.ToastManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminDashboardController implements Initializable {

    @FXML private Label navUsername;
    @FXML private Button btnStats, btnRooms, btnTypes, btnAmenity, btnGuests, btnAllRes;
    @FXML private VBox statsPage, roomsPage, typesPage, amenitiesPage, guestsPage, allResPage;

    // Stats page
    @FXML private Label totalGuests, totalRooms, totalRes, totalRevenue;
    @FXML private TableView<Reservation> statsResTable;
    @FXML private TableColumn<Reservation,String> sColId, sColGuest, sColRoom, sColIn, sColOut, sColCost, sColStatus;

    // Rooms page
    @FXML private TextField roomNumField, roomFloorField;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private Label roomMsg;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room,String> rNum, rFloor, rType, rPrice, rAvail;

    // Room types page
    @FXML private TextField typeNameField, typePriceField, typeCapField, typeDescField;
    @FXML private Label typeMsg;
    @FXML private TableView<RoomType> typesTable;
    @FXML private TableColumn<RoomType,String> tName, tPrice, tCap, tDesc;

    // Amenities page
    @FXML private TextField amenNameField, amenDescField, amenCostField;
    @FXML private Label amenMsg;
    @FXML private TableView<Amenity> amenitiesTable;
    @FXML private TableColumn<Amenity,String> aName, aDesc, aCost;

    // Guests page
    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest,String> gUser, gGender, gDob, gBalance, gAddress;

    // All reservations page
    @FXML private TableView<Reservation> allResTable;
    @FXML private TableColumn<Reservation,String> arId, arGuest, arRoom, arIn, arOut, arCost, arStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navUsername.setText(SessionManager.getCurrentUser().getUsername());
        setupAllTables();
        refreshStats();
        AnimationUtils.fadeIn(statsPage);
    }

    private void refreshStats() {
        AnimationUtils.animateCounter(totalGuests,  HotelDatabase.getAllGuests().size(), "", "");
        AnimationUtils.animateCounter(totalRooms,   HotelDatabase.getAllRooms().size(), "", "");
        AnimationUtils.animateCounter(totalRes,     HotelDatabase.getAllReservations().size(), "", "");
        double rev = HotelDatabase.getAllReservations().stream().mapToDouble(Reservation::calculateTotalCost).sum();
        AnimationUtils.animateMoney(totalRevenue, rev);
        statsResTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllReservations()));
    }

    @FXML private void addRoom() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        try {
            int num = Integer.parseInt(roomNumField.getText().trim());
            int floor = Integer.parseInt(roomFloorField.getText().trim());
            String typeName = roomTypeCombo.getValue();
            if (typeName == null) { showMsg(roomMsg, "Select a room type.", false); return; }
            RoomType rt = HotelDatabase.findRoomTypeByName(typeName);
            if (rt == null) { showMsg(roomMsg, "Room type not found.", false); return; }
            if (HotelDatabase.findRoomByNumber(num) != null) { showMsg(roomMsg, "Room " + num + " already exists.", false); return; }
            HotelDatabase.addRoom(new Room(num, floor, rt));
            roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRooms()));
            showMsg(roomMsg, "Room " + num + " added successfully.", true);
            ToastManager.success(s, "Room " + num + " added!");
            roomNumField.clear(); roomFloorField.clear(); roomTypeCombo.setValue(null);
        } catch (NumberFormatException e) { showMsg(roomMsg, "Enter valid numbers for room number and floor.", false); }
    }

    @FXML private void deleteRoom() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        Room sel = roomsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg(roomMsg, "Select a room to delete.", false); return; }
        HotelDatabase.deleteRoom(sel.getRoomNumber());
        roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRooms()));
        showMsg(roomMsg, "Room deleted.", true);
        ToastManager.info(s, "Room " + sel.getRoomNumber() + " removed.");
    }

    @FXML private void saveRoomType() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        try {
            String name = typeNameField.getText().trim();
            double price = Double.parseDouble(typePriceField.getText().trim());
            int cap = Integer.parseInt(typeCapField.getText().trim());
            String desc = typeDescField.getText().trim();
            if (name.isEmpty()) { showMsg(typeMsg, "Name required.", false); return; }
            RoomType existing = HotelDatabase.findRoomTypeByName(name);
            if (existing != null) {
                existing.setPricePerNight(price); existing.setCapacity(cap); existing.setDescription(desc);
                showMsg(typeMsg, name + " updated.", true); ToastManager.success(s, "Room type updated!");
            } else {
                HotelDatabase.addRoomType(new RoomType(name, price, cap, desc));
                showMsg(typeMsg, name + " added.", true); ToastManager.success(s, name + " room type added!");
            }
            typesTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRoomTypes()));
            refreshRoomTypeCombo();
        } catch (NumberFormatException e) { showMsg(typeMsg, "Enter valid price and capacity.", false); }
    }

    @FXML private void deleteRoomType() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        RoomType sel = typesTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg(typeMsg, "Select a room type.", false); return; }
        HotelDatabase.deleteRoomType(sel.getName());
        typesTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRoomTypes()));
        refreshRoomTypeCombo();
        showMsg(typeMsg, sel.getName() + " deleted.", true);
        ToastManager.info(s, sel.getName() + " deleted.");
    }

    @FXML private void saveAmenity() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        try {
            String name = amenNameField.getText().trim();
            String desc = amenDescField.getText().trim();
            double cost = Double.parseDouble(amenCostField.getText().trim());
            if (name.isEmpty()) { showMsg(amenMsg, "Name required.", false); return; }
            Amenity existing = HotelDatabase.getAmenityByName(name);
            if (existing != null) {
                existing.setDescription(desc); existing.setAdditionalCost(cost);
                showMsg(amenMsg, name + " updated.", true); ToastManager.success(s, "Amenity updated!");
            } else {
                HotelDatabase.addAmenity(new Amenity(name, desc, cost));
                showMsg(amenMsg, name + " added.", true); ToastManager.success(s, name + " amenity added!");
            }
            amenitiesTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllAmenities()));
        } catch (NumberFormatException e) { showMsg(amenMsg, "Enter valid cost.", false); }
    }

    @FXML private void deleteAmenity() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        Amenity sel = amenitiesTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg(amenMsg, "Select an amenity.", false); return; }
        HotelDatabase.deleteAmenity(sel.getName());
        amenitiesTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllAmenities()));
        showMsg(amenMsg, sel.getName() + " deleted.", true);
        ToastManager.info(s, sel.getName() + " deleted.");
    }

    private void setupAllTables() {
        sColId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReservationID())));
        sColGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuest().getUsername()));
        sColRoom.setCellValueFactory(d -> new SimpleStringProperty("Room " + d.getValue().getRoom().getRoomNumber()));
        sColIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().format(FMT)));
        sColOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().format(FMT)));
        sColCost.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calculateTotalCost())));
        sColStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));

        rNum.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        rFloor.setCellValueFactory(d -> new SimpleStringProperty("Floor " + d.getValue().getFloor()));
        rType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoomType().getName()));
        rPrice.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getRoomType().getPricePerNight())));
        rAvail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isAvailable() ? "\u2713 Yes" : "\u2717 No"));
        roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRooms()));
        refreshRoomTypeCombo();

        tName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        tPrice.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getPricePerNight())));
        tCap.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCapacity())));
        tDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        typesTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRoomTypes()));

        aName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        aDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        aCost.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getAdditionalCost())));
        amenitiesTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllAmenities()));

        gUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        gGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender() != null ? d.getValue().getGender().name() : "\u2014"));
        gDob.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDateOfBirth() != null ? d.getValue().getDateOfBirth().format(FMT) : "\u2014"));
        gBalance.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getBalance())));
        gAddress.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress() != null ? d.getValue().getAddress() : "\u2014"));
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllGuests()));

        arId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReservationID())));
        arGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuest().getUsername()));
        arRoom.setCellValueFactory(d -> new SimpleStringProperty("Room " + d.getValue().getRoom().getRoomNumber()));
        arIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().format(FMT)));
        arOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().format(FMT)));
        arCost.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calculateTotalCost())));
        arStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        allResTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllReservations()));
    }

    private void refreshRoomTypeCombo() {
        roomTypeCombo.setItems(FXCollections.observableArrayList(
                HotelDatabase.getAllRoomTypes().stream().map(RoomType::getName).collect(Collectors.toList())));
    }

    @FXML private void showStats()     { show(statsPage, btnStats);     refreshStats(); }
    @FXML private void showRooms()     { show(roomsPage, btnRooms); }
    @FXML private void showTypes()     { show(typesPage, btnTypes); }
    @FXML private void showAmenities() { show(amenitiesPage, btnAmenity); }
    @FXML private void showGuests()    { show(guestsPage, btnGuests);
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllGuests())); }
    @FXML private void showAllRes()    { show(allResPage, btnAllRes);
        allResTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllReservations())); }

    private void show(VBox page, Button active) {
        for (VBox p : Arrays.asList(statsPage, roomsPage, typesPage, amenitiesPage, guestsPage, allResPage)) {
            p.setVisible(false); p.setManaged(false);
        }
        page.setVisible(true); page.setManaged(true);
        AnimationUtils.slideInRight(page);
        for (Button b : Arrays.asList(btnStats, btnRooms, btnTypes, btnAmenity, btnGuests, btnAllRes))
            b.getStyleClass().setAll("sidebar-btn");
        active.getStyleClass().setAll("sidebar-btn-active");
    }

    private void showMsg(Label lbl, String msg, boolean ok) {
        lbl.setText((ok ? "\u2713  " : "\u26a0  ") + msg);
        lbl.setStyle(ok
                ? "-fx-background-color:rgba(20,80,40,0.25);-fx-text-fill:#86EFAC;-fx-padding:8 12;-fx-background-radius:6;-fx-border-color:rgba(34,197,94,0.3);-fx-border-width:1;-fx-border-radius:6;"
                : "-fx-background-color:rgba(120,20,20,0.25);-fx-text-fill:#FCA5A5;-fx-padding:8 12;-fx-background-radius:6;-fx-border-color:rgba(239,68,68,0.3);-fx-border-width:1;-fx-border-radius:6;");
        lbl.setVisible(true); lbl.setManaged(true);
        AnimationUtils.slideUp(lbl);
        if (!ok) AnimationUtils.shake(lbl);
    }

    @FXML private void handleLogout() {
        SessionManager.logout();
        try {
            Stage st = (Stage) navUsername.getScene().getWindow();
            NavigationManager.navigateTo(st, "views/Login.fxml", "Aurora Stays — Sign In");
        } catch (Exception e) { e.printStackTrace(); }
    }
}
