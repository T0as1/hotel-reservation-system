package controllers;

import booking.Invoice;
import booking.Reservation;
import data.HotelDatabase;
import enums.PaymentMethod;
import enums.ReservationStatus;
import models.Amenity;
import models.Guest;
import models.Room;
import models.RoomType;
import services.RoomAvailabilityService;
import utils.AnimationUtils;
import utils.ToastManager;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class GuestDashboardController implements Initializable {

    // Top nav + sidebar
    @FXML private Label navUsername;
    @FXML private Button navAvatar;
    @FXML private Button btnOverview, btnBrowse, btnReservations, btnCheckout, btnProfile;
    @FXML private VBox overviewPage, browsePage, reservationsPage, checkoutPage, profilePage;

    // Overview page
    @FXML private Label welcomeLabel, balanceLabel, activeResLabel, roomsLabel, totalSpentLabel;
    @FXML private TableView<Reservation> recentTable;
    @FXML private TableColumn<Reservation,String> colId, colRoom, colType, colCheckIn, colCheckOut, colCost, colStatus;
    @FXML private HBox statCardsRow;

    // Browse Rooms page
    @FXML private FlowPane roomsFlow;
    @FXML private DatePicker checkInPicker, checkOutPicker;
    @FXML private ComboBox<String> typeFilter;
    @FXML private Label refreshLabel;
    @FXML private StackPane browseStack;

    // Reservations page
    @FXML private TableView<Reservation> allResTable;
    @FXML private TableColumn<Reservation,String> rColId, rColRoom, rColType, rColCheckIn, rColCheckOut, rColNights, rColCost, rColStatus;

    // Checkout page
    @FXML private ComboBox<String> checkoutResBox;
    @FXML private VBox cashOption, cardOption, onlineOption;
    @FXML private Label checkoutMsg, invRoom, invCheckIn, invCheckOut, invNights, invRate, invTotal, invAfterBalance;

    // Profile page
    @FXML private Label profUsername, profBalance, profDob, profGender, profAddress, profPrefs;
    @FXML private StackPane contentArea;

    private Guest guest;
    private PaymentMethod selectedPayment;
    private RoomAvailabilityService availabilityService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private static int nextReservationId = 1000;
    private static int nextInvoiceId = 5000;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        guest = SessionManager.getCurrentGuest();
        if (guest == null) {
            Platform.runLater(this::redirectToLogin);
            return;
        }

        navUsername.setText(guest.getUsername());
        if (navAvatar != null && !guest.getUsername().isEmpty())
            navAvatar.setText(guest.getUsername().substring(0, 1).toUpperCase());

        welcomeLabel.setText("Welcome back, " + guest.getUsername() + " ✨");

        setupTables();
        setupBrowse();
        refreshOverview();
        refreshProfile();
        startAvailabilityService();
        startSyncTimer();

        AnimationUtils.fadeIn(overviewPage);
    }

    private void startSyncTimer() {
        javafx.animation.Timeline timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(2000), e -> {
                    if (HotelDatabase.checkForUpdates()) {
                        Guest updated = HotelDatabase.findGuestByUsername(guest.getUsername());
                        if (updated != null) {
                            guest = updated;
                            SessionManager.setCurrentUser(guest);
                        }
                        syncIdCounters();
                        refreshOverview();
                        refreshProfile();
                    }
                }));
        timer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timer.play();
    }

    private void syncIdCounters() {
        int maxRes = HotelDatabase.getAllReservations().stream()
                .mapToInt(Reservation::getReservationID).max().orElse(0);
        int maxInv = HotelDatabase.getAllInvoices().stream()
                .mapToInt(Invoice::getInvoiceID).max().orElse(0);
        if (maxRes >= nextReservationId) nextReservationId = maxRes + 1;
        if (maxInv >= nextInvoiceId) nextInvoiceId = maxInv + 1;
    }

    // TABLES
    private void setupTables() {
        // Recent / overview table
        colId.setCellValueFactory(d -> new SimpleStringProperty("#" + d.getValue().getReservationID()));
        colRoom.setCellValueFactory(d -> new SimpleStringProperty("Room " + d.getValue().getRoom().getRoomNumber()));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom().getRoomType().getName()));
        colCheckIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().format(FMT)));
        colCheckOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().format(FMT)));
        colCost.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calculateTotalCost())));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));

        // All reservations
        rColId.setCellValueFactory(d -> new SimpleStringProperty("#" + d.getValue().getReservationID()));
        rColRoom.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoom().getRoomNumber())));
        rColType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom().getRoomType().getName()));
        rColCheckIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().format(FMT)));
        rColCheckOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().format(FMT)));
        rColNights.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().calculateDuration())));
        rColCost.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calculateTotalCost())));
        rColStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
    }

    private List<Reservation> myReservations() {
        return HotelDatabase.getAllReservations().stream()
                .filter(r -> r.getGuest() != null
                        && r.getGuest().getUsername().equalsIgnoreCase(guest.getUsername()))
                .toList();
    }

    private void refreshOverview() {
        List<Reservation> mine = myReservations();
        long active = mine.stream().filter(r ->
                r.getStatus() != ReservationStatus.CANCELLED
                && r.getStatus() != ReservationStatus.CHECKED_OUT
                && r.getStatus() != ReservationStatus.COMPLETED).count();
        double spent = mine.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .mapToDouble(Reservation::calculateTotalCost).sum();
        int avail = HotelDatabase.getAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(1)).size();

        AnimationUtils.animateMoney(balanceLabel, guest.getBalance());
        AnimationUtils.animateCounter(activeResLabel, active, "", "");
        AnimationUtils.animateCounter(roomsLabel, avail, "", "");
        AnimationUtils.animateMoney(totalSpentLabel, spent);

        recentTable.setItems(FXCollections.observableArrayList(mine.stream().limit(5).toList()));
        allResTable.setItems(FXCollections.observableArrayList(mine));
        refreshCheckoutBox();
    }

    private void refreshProfile() {
        profUsername.setText(guest.getUsername());
        profBalance.setText(String.format("$%,.2f", guest.getBalance()));
        profDob.setText(guest.getDateOfBirth() != null ? guest.getDateOfBirth().format(FMT) : "—");
        profGender.setText(guest.getGender() != null ? guest.getGender().name() : "—");
        profAddress.setText(guest.getAddress() != null ? guest.getAddress() : "—");
        profPrefs.setText(guest.getRoomPreferences() != null ? guest.getRoomPreferences() : "None");
    }

    // BROWSE
    private void setupBrowse() {
        checkInPicker.setValue(LocalDate.now());
        checkOutPicker.setValue(LocalDate.now().plusDays(1));

        typeFilter.getItems().add("All Types");
        for (RoomType rt : HotelDatabase.getAllRoomTypes()) typeFilter.getItems().add(rt.getName());
        typeFilter.setValue("All Types");
    }

    private void startAvailabilityService() {
        availabilityService = new RoomAvailabilityService();
        availabilityService.setDates(checkInPicker.getValue(), checkOutPicker.getValue());
        availabilityService.setOnSucceeded(e -> {
            @SuppressWarnings("unchecked")
            List<Room> rooms = (List<Room>) availabilityService.getValue();
            renderRooms(rooms);
        });
        availabilityService.setOnFailed(e -> {
            if (refreshLabel != null) refreshLabel.setText("⚠ Could not fetch availability");
        });
        availabilityService.start();
    }

    @FXML private void searchRooms() {
        LocalDate in  = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();
        Stage s = (Stage) navUsername.getScene().getWindow();
        if (in == null || out == null) { ToastManager.warning(s, "Please select both dates."); return; }
        if (!out.isAfter(in)) { ToastManager.warning(s, "Check-out must be after check-in."); return; }
        if (in.isBefore(LocalDate.now())) { ToastManager.warning(s, "Check-in cannot be in the past."); return; }

        if (refreshLabel != null) refreshLabel.setText("○ Searching…");
        if (availabilityService != null) availabilityService.cancel();
        availabilityService.setDates(in, out);
        availabilityService.restart();
    }

    private void renderRooms(List<Room> rooms) {
        roomsFlow.getChildren().clear();
        String filter = typeFilter.getValue();
        int shown = 0;
        for (Room r : rooms) {
            if (filter != null && !filter.equals("All Types")
                    && !r.getRoomType().getName().equals(filter)) continue;
            roomsFlow.getChildren().add(buildRoomCard(r));
            shown++;
        }
        if (refreshLabel != null)
            refreshLabel.setText("● Live availability \u2014 " + shown + " rooms found");
        if (roomsFlow.getChildren().isEmpty()) {
            Label empty = new Label("No rooms match your search. Try different dates or types.");
            empty.setStyle("-fx-text-fill:#9AA3BE;-fx-font-size:13px;-fx-padding:40;");
            roomsFlow.getChildren().add(empty);
            if (refreshLabel != null)
                refreshLabel.setText("⚠ No rooms match your criteria");
        }
        AnimationUtils.staggerFadeIn(
                roomsFlow.getChildren().stream().map(n -> (javafx.scene.Node) n).toList(), 50);
    }

    private VBox buildRoomCard(Room r) {
        VBox card = new VBox(10);
        card.getStyleClass().add("room-card");
        card.setAlignment(Pos.CENTER_LEFT);

        String icon = switch (r.getRoomType().getName().toLowerCase()) {
            case "suite", "penthouse suite" -> "🏰";
            case "double" -> "🛏️";
            case "single" -> "🛌";
            default -> "🏨";
        };
        Label iconL = new Label(icon); iconL.getStyleClass().add("room-card-icon");
        Label num = new Label("Room " + r.getRoomNumber()); num.getStyleClass().add("room-card-num");
        Label type = new Label(r.getRoomType().getName().toUpperCase()); type.getStyleClass().add("room-card-type");
        Label meta = new Label("Floor " + r.getFloor() + "  •  Capacity " + r.getRoomType().getCapacity());
        meta.getStyleClass().add("room-card-meta");
        Label price = new Label(String.format("$%.0f", r.getRoomType().getPricePerNight()) + " / night");
        price.getStyleClass().add("room-card-price");

        Button book = new Button("✦ BOOK NOW");
        book.getStyleClass().add("primary-btn");
        book.setMaxWidth(Double.MAX_VALUE);
        book.setPrefHeight(38);
        book.setOnAction(e -> bookRoom(r));

        card.getChildren().addAll(iconL, num, type, meta, price, book);
        AnimationUtils.setupHoverScale(card, 1.025);
        return card;
    }

    private void bookRoom(Room r) {
        Stage s = (Stage) navUsername.getScene().getWindow();
        LocalDate in  = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();
        if (in == null || out == null || !out.isAfter(in)) {
            ToastManager.warning(s, "Pick valid check-in and check-out dates first."); return;
        }
        if (HotelDatabase.isRoomClashing(r.getRoomNumber(), in, out)) {
            ToastManager.error(s, "Room " + r.getRoomNumber() + " is no longer available."); return;
        }
        long nights = ChronoUnit.DAYS.between(in, out);
        Reservation res = new Reservation(nextReservationId++, guest, r, in, out);
        double cost = res.calculateTotalCost();
        double alreadyHeld = HotelDatabase.getAllReservations().stream()
                .filter(x -> x.getGuest().getUsername().equalsIgnoreCase(guest.getUsername())
                    && (x.getStatus() == ReservationStatus.PENDING || x.getStatus() == ReservationStatus.CONFIRMED))
                .mapToDouble(Reservation::calculateTotalCost).sum();
        if (guest.getBalance() < cost + alreadyHeld) {
            ToastManager.error(s, String.format("Insufficient balance. Need $%.2f (incl. $%.2f pending), have $%.2f.",
                    cost + alreadyHeld, alreadyHeld, guest.getBalance()));
            return;
        }

        HotelDatabase.addReservation(res);
        ToastManager.success(s, "Room " + r.getRoomNumber() + " booked for " + nights + " night(s)! Waiting for receptionist check-in.");
        refreshOverview();
        if (availabilityService != null) availabilityService.restart();
    }

    // RESERVATIONS
    @FXML private void cancelReservation() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        Reservation sel = allResTable.getSelectionModel().getSelectedItem();
        if (sel == null) { ToastManager.warning(s, "Select a reservation to cancel."); return; }
        if (sel.getStatus() == ReservationStatus.CANCELLED
                || sel.getStatus() == ReservationStatus.CHECKED_OUT
                || sel.getStatus() == ReservationStatus.COMPLETED
                || sel.getStatus() == ReservationStatus.CHECKED_IN) {
            ToastManager.warning(s, "This reservation cannot be cancelled."); return;
        }
        sel.getRoom().release();
        sel.cancel();
        HotelDatabase.saveToFile();
        ToastManager.success(s, "Reservation #" + sel.getReservationID() + " cancelled.");
        refreshOverview();
        allResTable.refresh();
        recentTable.refresh();
        if (availabilityService != null) availabilityService.restart();
    }

    // CHECKOUT
    private boolean checkoutListenerAttached = false;
    private void refreshCheckoutBox() {
        if (checkoutResBox == null) return;
        List<Reservation> payable = myReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN).toList();
        String previous = checkoutResBox.getValue();
        checkoutResBox.getItems().clear();
        for (Reservation r : payable)
            checkoutResBox.getItems().add("#" + r.getReservationID() + " — Room "
                    + r.getRoom().getRoomNumber() + " (" + r.getCheckInDate().format(FMT) + ")");
        // Attach listener once to avoid stale invoice updates.
        if (!checkoutListenerAttached) {
            checkoutResBox.valueProperty().addListener((o, ov, nv) -> updateInvoicePreview());
            checkoutListenerAttached = true;
        }
        // Preserve selection if it's still valid; otherwise clear preview
        if (previous != null && checkoutResBox.getItems().contains(previous)) {
            checkoutResBox.setValue(previous);
        } else {
            checkoutResBox.setValue(null);
            updateInvoicePreview();
        }
    }

    private Reservation findSelectedReservation() {
        String sel = checkoutResBox.getValue();
        if (sel == null) return null;
        try {
            int id = Integer.parseInt(sel.substring(1, sel.indexOf(" ")));
            return HotelDatabase.findReservationById(id);
        } catch (Exception e) { return null; }
    }

    private void updateInvoicePreview() {
        Reservation r = findSelectedReservation();
        if (r == null) {
            invRoom.setText("—"); invCheckIn.setText("—"); invCheckOut.setText("—");
            invNights.setText("—"); invRate.setText("—"); invTotal.setText("$0.00");
            invAfterBalance.setText("—"); return;
        }
        invRoom.setText("Room " + r.getRoom().getRoomNumber() + " · " + r.getRoom().getRoomType().getName());
        invCheckIn.setText(r.getCheckInDate().format(FMT));
        invCheckOut.setText(r.getCheckOutDate().format(FMT));
        invNights.setText(String.valueOf(r.calculateDuration()));
        invRate.setText(String.format("$%.2f", r.getRoom().getRoomType().getPricePerNight()));
        invTotal.setText(String.format("$%.2f", r.calculateTotalCost()));
        double after = guest.getBalance() - r.calculateTotalCost();
        invAfterBalance.setText(String.format("$%.2f", after));
    }

    @FXML private void selectCash()   { setPayment(PaymentMethod.CASH); }
    @FXML private void selectCard()   { setPayment(PaymentMethod.CREDIT_CARD); }
    @FXML private void selectOnline() { setPayment(PaymentMethod.ONLINE); }

    private void setPayment(PaymentMethod m) {
        selectedPayment = m;
        cashOption.getStyleClass().setAll("payment-option");
        cardOption.getStyleClass().setAll("payment-option");
        onlineOption.getStyleClass().setAll("payment-option");
        VBox chosen = m == PaymentMethod.CASH ? cashOption
                    : m == PaymentMethod.CREDIT_CARD ? cardOption : onlineOption;
        chosen.getStyleClass().setAll("payment-option-selected");
        AnimationUtils.bounce(chosen);
    }

    @FXML private void processCheckout() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        Reservation r = findSelectedReservation();
        if (r == null) { showCheckoutMsg("Please select a reservation.", false); return; }
        // Safety: refuse to charge cancelled / already-paid reservations
        if (r.getStatus() == ReservationStatus.CANCELLED) {
            showCheckoutMsg("This reservation was cancelled and cannot be paid.", false);
            refreshCheckoutBox(); return;
        }
        if (r.getStatus() == ReservationStatus.CHECKED_OUT
                || r.getStatus() == ReservationStatus.COMPLETED) {
            showCheckoutMsg("This reservation has already been paid.", false);
            refreshCheckoutBox(); return;
        }
        try {
            if (selectedPayment == null) {
                showCheckoutMsg("Please select a payment method first.", false); return;
            }
            double amount = r.calculateTotalCost();
            if (guest.getBalance() < amount) {
                showCheckoutMsg("Insufficient balance.", false); return;
            }
            Invoice inv = new Invoice(nextInvoiceId++, r);
            inv.processPayment(selectedPayment);
            HotelDatabase.addInvoice(inv);
            guest.pay(amount);
            r.getRoom().release();
            r.setStatus(ReservationStatus.COMPLETED);
            HotelDatabase.saveToFile();

            ToastManager.success(s, "Payment of $" + String.format("%.2f", amount) + " processed via " + selectedPayment.name());
            showCheckoutMsg("✓ Payment successful. Visit reception to confirm checkout.", true);
            refreshOverview(); refreshProfile(); refreshCheckoutBox();
        } catch (Exception ex) {
            showCheckoutMsg(ex.getMessage(), false);
            ToastManager.error(s, ex.getMessage());
        }
    }

    private void showCheckoutMsg(String msg, boolean ok) {
        checkoutMsg.setText(msg);
        checkoutMsg.getStyleClass().removeAll("error-label", "success-label");
        checkoutMsg.getStyleClass().add(ok ? "success-label" : "error-label");
        checkoutMsg.setVisible(true); checkoutMsg.setManaged(true);
    }

    // NAVIGATION
    @FXML private void showOverview()    { switchPage(overviewPage,    btnOverview); refreshOverview(); }
    @FXML private void showBrowse()      { switchPage(browsePage,      btnBrowse); }
    @FXML private void showReservations(){ switchPage(reservationsPage,btnReservations); refreshOverview(); }
    @FXML private void showCheckout()    { switchPage(checkoutPage,    btnCheckout); refreshCheckoutBox(); updateInvoicePreview(); }
    @FXML private void showProfile()     { switchPage(profilePage,     btnProfile); refreshProfile(); }

    private void switchPage(VBox page, Button btn) {
        for (VBox p : new VBox[]{overviewPage, browsePage, reservationsPage, checkoutPage, profilePage}) {
            p.setVisible(false); p.setManaged(false);
        }
        for (Button b : new Button[]{btnOverview, btnBrowse, btnReservations, btnCheckout, btnProfile}) {
            b.getStyleClass().setAll("sidebar-btn");
        }
        page.setVisible(true); page.setManaged(true);
        btn.getStyleClass().setAll("sidebar-btn-active");
        AnimationUtils.fadeIn(page);
    }

    // CHAT
    @FXML private void openChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChatWindow.fxml"));
            Parent root = loader.load();
            Stage chatStage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.web("#07090F"));
            scene.getStylesheets().add(getClass().getResource("/views/hotel.css").toExternalForm());
            chatStage.setScene(scene);
            chatStage.setTitle("Vespera — Live Chat");
            chatStage.show();
        } catch (Exception e) {
            ToastManager.error((Stage) navUsername.getScene().getWindow(), "Could not open chat: " + e.getMessage());
        }
    }

    // LOGOUT
    private void redirectToLogin() {
        try {
            Stage st = (Stage) navUsername.getScene().getWindow();
            NavigationManager.navigateTo(st, "views/Login.fxml", "Vespera — Sign In");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout() {
        StackPane overlay = AnimationUtils.createLoadingOverlay("Signing out\u2026");
        contentArea.getChildren().add(overlay);
        PauseTransition pt = new PauseTransition(Duration.millis(600));
        pt.setOnFinished(ev -> {
            if (availabilityService != null) availabilityService.cancel();
            SessionManager.logout();
            redirectToLogin();
        });
        pt.play();
    }
}
