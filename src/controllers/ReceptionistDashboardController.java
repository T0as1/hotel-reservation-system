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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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
    @FXML private TableColumn<Reservation,String> oId, oGuest, oRoom, oIn, oOut, oStatus;

    @FXML private ComboBox<String> checkInBox, checkOutBox;
    @FXML private Label checkInMsg, checkOutMsg;

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest,String> gUser, gBalance, gGender, gAddress;

    @FXML private VBox chatBox;
    @FXML private TextField replyField;
    @FXML private ScrollPane chatScroll;
    @FXML private Label chatStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private ChatServer server;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navUsername.setText(SessionManager.getCurrentUser().getUsername());
        setupTables();
        refreshOverview();
        startChatServer();
        AnimationUtils.fadeIn(overviewPage);
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
        int id = Integer.parseInt(sel.split("#")[1].split(" ")[0]);
        Reservation res = HotelDatabase.findReservationById(id);
        if (res == null) { showMsg(checkInMsg, "Reservation not found.", false); return; }
        res.setStatus(ReservationStatus.CHECKED_IN); res.getRoom().book();
        refreshOverview(); refreshCheckInBox();
        showMsg(checkInMsg, res.getGuest().getUsername() + " checked in to Room " + res.getRoom().getRoomNumber(), true);
        ToastManager.success(s, res.getGuest().getUsername() + " checked in \u2713");
    }

    private void refreshCheckOutBox() {
        List<String> items = HotelDatabase.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
                .map(r -> "#" + r.getReservationID() + " \u2014 " + r.getGuest().getUsername() + " \u2014 Room " + r.getRoom().getRoomNumber())
                .collect(Collectors.toList());
        checkOutBox.setItems(FXCollections.observableArrayList(items));
    }

    @FXML private void processCheckOut() {
        Stage s = (Stage) navUsername.getScene().getWindow();
        String sel = checkOutBox.getValue();
        if (sel == null) { showMsg(checkOutMsg, "Select a reservation.", false); return; }
        int id = Integer.parseInt(sel.split("#")[1].split(" ")[0]);
        Reservation res = HotelDatabase.findReservationById(id);
        if (res == null) { showMsg(checkOutMsg, "Reservation not found.", false); return; }
        res.setStatus(ReservationStatus.CHECKED_OUT); res.getRoom().release();
        refreshOverview(); refreshCheckOutBox();
        showMsg(checkOutMsg, res.getGuest().getUsername() + " checked out from Room " + res.getRoom().getRoomNumber(), true);
        ToastManager.info(s, res.getGuest().getUsername() + " checked out.");
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
        lbl.setStyle(ok
                ? "-fx-background-color:rgba(20,80,40,0.25);-fx-text-fill:#86EFAC;-fx-padding:10 14;-fx-background-radius:8;-fx-border-color:rgba(34,197,94,0.3);-fx-border-width:1;-fx-border-radius:8;"
                : "-fx-background-color:rgba(120,20,20,0.25);-fx-text-fill:#FCA5A5;-fx-padding:10 14;-fx-background-radius:8;-fx-border-color:rgba(239,68,68,0.3);-fx-border-width:1;-fx-border-radius:8;");
        lbl.setVisible(true); lbl.setManaged(true);
        AnimationUtils.slideUp(lbl);
        if (!ok) AnimationUtils.shake(lbl);
    }

    @FXML private void handleLogout() {
        if (server != null) server.stop();
        SessionManager.logout();
        try {
            Stage st = (Stage) navUsername.getScene().getWindow();
            NavigationManager.navigateTo(st, "views/Login.fxml", "Aurora Stays — Sign In");
        } catch (Exception e) { e.printStackTrace(); }
    }
}
