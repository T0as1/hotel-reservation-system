package controllers;

import booking.Reservation;
import data.HotelDatabase;
import enums.ReservationStatus;
import models.Guest;
import networking.ChatServer;
import utils.AnimationUtils;
import utils.ToastManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.animation.PauseTransition;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReceptionistDashboardController implements Initializable {

    @FXML private Label navUsername;
    @FXML private Button btnOverview, btnCheckIn, btnCheckOut, btnGuests, btnChat;
    @FXML private VBox overviewPage, checkInPage, checkOutPage, guestsPage, chatPage;

    @FXML private Label pendingCount, checkedInCount, availableCount;
    @FXML private TableView<Reservation> overviewTable;
    @FXML private TableColumn<Reservation,String> oId, oGuest, oRoom, oNights, oIn, oOut, oStatus;

    @FXML private ComboBox<String> checkInBox, checkOutBox;
    @FXML private Label checkInMsg, checkOutMsg;

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest,String> gUser, gBalance, gGender, gAddress;

    @FXML private VBox chatBox;
    @FXML private TextField replyField;
    @FXML private ScrollPane chatScroll;
    @FXML private Label chatStatus;
    @FXML private StackPane contentArea;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private ChatServer server;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navUsername.setText(SessionManager.getCurrentUser().getUsername());
        setupTables();
        refreshOverview();
        startChatServer();
        startSyncTimer();
        AnimationUtils.fadeIn(overviewPage);
    }

    private void startSyncTimer() {
        javafx.animation.Timeline timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(2000), e -> {
                    if (HotelDatabase.checkForUpdates()) {
                        setupTables();
                        refreshOverview();
                        refreshCheckInBox();
                        refreshCheckOutBox();
                    }
                }));
        timer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timer.play();
    }

    private void refreshOverview() {
        List<Reservation> all = HotelDatabase.getAllReservations();
        long pending   = all.stream().filter(r -> r.getStatus() == ReservationStatus.CONFIRMED || r.getStatus() == ReservationStatus.PENDING).count();
        long checkedIn = all.stream().filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN).count();
        long available = HotelDatabase.getAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(1)).size();
        AnimationUtils.animateCounter(pendingCount,   pending,    "", "");
        AnimationUtils.animateCounter(checkedInCount, checkedIn,  "", "");
        AnimationUtils.animateCounter(availableCount, available,  "", "");
        overviewTable.setItems(FXCollections.observableArrayList(all));
    }

    private void setupTables() {
        oId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReservationID())));
        oGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuest().getUsername()));
        oRoom.setCellValueFactory(d -> new SimpleStringProperty("Room " + d.getValue().getRoom().getRoomNumber()));
        oNights.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().calculateDuration())));
        oIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().format(FMT)));
        oOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().format(FMT)));
        oStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        overviewTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllReservations()));

        gUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        gBalance.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getBalance())));
        gGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender() != null ? d.getValue().getGender().name() : "\u2014"));
        gAddress.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress() != null ? d.getValue().getAddress() : "\u2014"));
    }

    private void refreshCheckInBox() {
        List<String> items = HotelDatabase.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED || r.getStatus() == ReservationStatus.PENDING)
                .map(r -> "#" + r.getReservationID() + " \u2014 " + r.getGuest().getUsername() + " \u2014 Room " + r.getRoom().getRoomNumber())
                .collect(Collectors.toList());
        checkInBox.setItems(FXCollections.observableArrayList(items));
    }

    @FXML private void processCheckIn() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        String sel = checkInBox.getValue();
        if (sel == null) { showMsg(checkInMsg, "Select a reservation.", false); return; }
        int id;
        try { id = Integer.parseInt(sel.split("#")[1].split(" ")[0]); }
        catch (Exception e) { showMsg(checkInMsg, "Invalid selection.", false); return; }
        Reservation res = HotelDatabase.findReservationById(id);
        if (res == null) { showMsg(checkInMsg, "Reservation not found.", false); return; }
        res.setStatus(ReservationStatus.CHECKED_IN); res.getRoom().book();
        HotelDatabase.saveToFile();
        refreshOverview(); refreshCheckInBox();
        showMsg(checkInMsg, res.getGuest().getUsername() + " checked in to Room " + res.getRoom().getRoomNumber(), true);
        ToastManager.success(s, res.getGuest().getUsername() + " checked in \u2713");
    }

    private void refreshCheckOutBox() {
        List<String> items = HotelDatabase.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.AWAITING_CHECKOUT)
                .map(r -> "#" + r.getReservationID() + " \u2014 " + r.getGuest().getUsername()
                        + " \u2014 Room " + r.getRoom().getRoomNumber() + " (paid)")
                .collect(Collectors.toList());
        checkOutBox.setItems(FXCollections.observableArrayList(items));
    }

    @FXML private void processCheckOut() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        String sel = checkOutBox.getValue();
        if (sel == null) { showMsg(checkOutMsg, "Select a reservation.", false); return; }
        int id;
        try { id = Integer.parseInt(sel.split("#")[1].split(" ")[0]); }
        catch (Exception e) { showMsg(checkOutMsg, "Invalid selection.", false); return; }
        Reservation res = HotelDatabase.findReservationById(id);
        if (res == null) { showMsg(checkOutMsg, "Reservation not found.", false); return; }
        if (res.getStatus() != ReservationStatus.AWAITING_CHECKOUT) {
            showMsg(checkOutMsg, "Guest has not paid yet. Complete payment first.", false);
            ToastManager.warning(s, "Guest must pay before checkout.");
            return;
        }
        res.setStatus(ReservationStatus.CHECKED_OUT);
        showMsg(checkOutMsg, res.getGuest().getUsername() + " checkout confirmed from Room " + res.getRoom().getRoomNumber(), true);
        ToastManager.success(s, res.getGuest().getUsername() + " checkout confirmed.");
        HotelDatabase.saveToFile();
        refreshOverview(); refreshCheckOutBox();
    }

    private void startChatServer() {
        server = ChatServer.getInstance();
        server.start();
        server.addMessageListener(msg -> javafx.application.Platform.runLater(() -> {
            addBubble(msg, false);
            chatScroll.setVvalue(1.0);
            if (!chatPage.isVisible()) {
                Stage s = (Stage) navUsername.getScene().getWindow();
                ToastManager.info(s, "\uD83D\uDCAC New message from guest");
            }
        }));
        chatStatus.setText("\u25cf Server Online");
        chatStatus.setStyle("-fx-text-fill:#22C55E;-fx-font-size:12px;-fx-font-weight:bold;");
    }

    @FXML private void sendReply() {
        String text = replyField.getText().trim();
        if (text.isEmpty()) return;
        if (server != null) server.broadcast("[Reception]: " + text, null);
        addBubble("You (Reception): " + text, true);
        replyField.clear(); chatScroll.setVvalue(1.0);
    }

    private void addBubble(String text, boolean mine) {
        HBox row = new HBox(); row.setPadding(new Insets(2, 0, 2, 0));
        Label lbl = new Label(text); lbl.setWrapText(true); lbl.setMaxWidth(300);
        lbl.getStyleClass().add(mine ? "msg-mine" : "msg-other");
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getChildren().add(lbl);
        chatBox.getChildren().add(row);
        AnimationUtils.slideUp(row);
    }

    @FXML private void showOverview()  { show(overviewPage, btnOverview); refreshOverview(); }
    @FXML private void showCheckIn()   { show(checkInPage, btnCheckIn);   refreshCheckInBox(); }
    @FXML private void showCheckOut()  { show(checkOutPage, btnCheckOut); refreshCheckOutBox(); }
    @FXML private void showGuests()    { show(guestsPage, btnGuests);
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.getAllGuests())); }
    @FXML private void showChat()      { show(chatPage, btnChat); }

    private void show(VBox page, Button active) {
        for (VBox p : Arrays.asList(overviewPage, checkInPage, checkOutPage, guestsPage, chatPage)) {
            p.setVisible(false); p.setManaged(false);
        }
        page.setVisible(true); page.setManaged(true);
        AnimationUtils.slideInRight(page);
        for (Button b : Arrays.asList(btnOverview, btnCheckIn, btnCheckOut, btnGuests, btnChat))
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

    @FXML private void handleLogout() {
        StackPane overlay = AnimationUtils.createLoadingOverlay("Signing out\u2026");
        contentArea.getChildren().add(overlay);
        PauseTransition pt = new PauseTransition(Duration.millis(600));
        pt.setOnFinished(ev -> {
            if (server != null) server.stop();
            SessionManager.logout();
            try {
                Stage st = (Stage) navUsername.getScene().getWindow();
                NavigationManager.navigateTo(st, "views/Login.fxml", "Vespera — Sign In");
            } catch (Exception e) { e.printStackTrace(); }
        });
        pt.play();
    }
}
