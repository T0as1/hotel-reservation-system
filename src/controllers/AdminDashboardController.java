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
import javafx.animation.PauseTransition;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    @FXML private TableColumn<Reservation,String> sColId, sColGuest, sColRoom, sColNights, sColIn, sColOut, sColCost, sColStatus;

    // Rooms page
    @FXML private TextField roomNumField, roomFloorField;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private Label roomMsg;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room,String> rNum, rFloor, rType, rAmen, rPrice, rAvail;
    @FXML private Label amenRoomLabel;
    @FXML private ListView<String> roomAmenList;
    @FXML private ComboBox<String> addAmenCombo;

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
    @FXML private TableColumn<Reservation,String> arId, arGuest, arRoom, arNights, arIn, arOut, arCost, arStatus;
    @FXML private StackPane contentArea;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navUsername.setText(SessionManager.getCurrentUser().getUsername());
        setupAllTables();
        refreshStats();
        startSyncTimer();
        AnimationUtils.fadeIn(statsPage);
    }

    private void startSyncTimer() {
        javafx.animation.Timeline timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(2000), e -> {
                    if (HotelDatabase.checkForUpdates()) {
                        setupAllTables();
                        refreshStats();
                    }
                }));
        timer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timer.play();
    }

    private void refreshStats() {
        AnimationUtils.animateCounter(totalGuests,  HotelDatabase.getAllGuests().size(), "", "");
        AnimationUtils.animateCounter(totalRooms,   HotelDatabase.getAllRooms().size(), "", "");
        AnimationUtils.animateCounter(totalRes,     HotelDatabase.getAllReservations().size(), "", "");
        double rev = HotelDatabase.getAllReservations().stream()
                .filter(r -> r.getStatus() != enums.ReservationStatus.CANCELLED)
                .mapToDouble(Reservation::calculateTotalCost).sum();
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
            HotelDatabase.saveToFile();
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
            HotelDatabase.saveToFile();
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
        sColNights.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().calculateDuration())));
        sColIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().format(FMT)));
        sColOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().format(FMT)));
        sColCost.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calculateTotalCost())));
        sColStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));

        rNum.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        rFloor.setCellValueFactory(d -> new SimpleStringProperty("Floor " + d.getValue().getFloor()));
        rType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoomType().getName()));
        rAmen.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAmenities().size() + " items"));
        rPrice.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getRoomType().getPricePerNight())));
        rAvail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isAvailable() ? "\u2713 Yes" : "\u2717 No"));
        roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRooms()));
        roomsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            refreshRoomAmenities(sel);
        });
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
        arNights.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().calculateDuration())));
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
    @FXML private void showRooms()     { show(roomsPage, btnRooms); roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllRooms())); refreshRoomAmenityCombo(); }
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
        lbl.getStyleClass().removeAll("error-label", "success-label");
        lbl.getStyleClass().add(ok ? "success-label" : "error-label");
        lbl.setVisible(true); lbl.setManaged(true);
        AnimationUtils.slideUp(lbl);
        if (!ok) AnimationUtils.shake(lbl);
    }

    private void refreshRoomAmenities(Room room) {
        if (room == null) { amenRoomLabel.setText("Select a room"); roomAmenList.getItems().clear(); return; }
        amenRoomLabel.setText("Room " + room.getRoomNumber() + " \u2014 " + room.getRoomType().getName());
        roomAmenList.getItems().clear();
        for (Amenity a : room.getAmenities()) {
            roomAmenList.getItems().add(a.getName() + "  ($" + String.format("%.2f", a.getAdditionalCost()) + ")");
        }
        refreshRoomAmenityCombo();
    }

    private void refreshRoomAmenityCombo() {
        addAmenCombo.setItems(FXCollections.observableArrayList(
                HotelDatabase.getAllAmenities().stream().map(Amenity::getName).collect(Collectors.toList())));
    }

    @FXML private void handleAddRoomAmenity() {
        Room sel = roomsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg(roomMsg, "Select a room first.", false); return; }
        String amenName = addAmenCombo.getValue();
        if (amenName == null) { showMsg(roomMsg, "Select an amenity.", false); return; }
        for (Amenity existing : sel.getAmenities()) {
            if (existing.getName().equalsIgnoreCase(amenName)) {
                showMsg(roomMsg, "Room already has this amenity.", false); return;
            }
        }
        Amenity a = HotelDatabase.getAmenityByName(amenName);
        if (a == null) return;
        sel.addAmenity(a);
        HotelDatabase.saveToFile();
        refreshRoomAmenities(sel);
        roomsTable.refresh();
        showMsg(roomMsg, "Amenity added to room.", true);
        ToastManager.success((Stage) navUsername.getScene().getWindow(), amenName + " added to Room " + sel.getRoomNumber());
    }

    @FXML private void handleRemoveRoomAmenity() {
        Room sel = roomsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg(roomMsg, "Select a room first.", false); return; }
        String selected = roomAmenList.getSelectionModel().getSelectedItem();
        if (selected == null) { showMsg(roomMsg, "Select an amenity to remove.", false); return; }
        String amenName = selected.split("  ")[0];
        sel.getAmenities().removeIf(a -> a.getName().equalsIgnoreCase(amenName));
        HotelDatabase.saveToFile();
        refreshRoomAmenities(sel);
        roomsTable.refresh();
        showMsg(roomMsg, "Amenity removed.", true);
        ToastManager.info((Stage) navUsername.getScene().getWindow(), amenName + " removed from Room " + sel.getRoomNumber());
    }

    @FXML private void handleLogout() {
        StackPane overlay = AnimationUtils.createLoadingOverlay("Signing out\u2026");
        contentArea.getChildren().add(overlay);
        PauseTransition pt = new PauseTransition(Duration.millis(600));
        pt.setOnFinished(ev -> {
            SessionManager.logout();
            try {
                Stage st = (Stage) navUsername.getScene().getWindow();
                NavigationManager.navigateTo(st, "views/Login.fxml", "Vespera — Sign In");
            } catch (Exception e) { e.printStackTrace(); }
        });
        pt.play();
    }
}
