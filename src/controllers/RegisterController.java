package controllers;

import data.HotelDatabase;
import enums.Gender;
import exceptions.DobException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidUsernameException;
import models.Guest;
import utils.AnimationUtils;
import utils.ToastManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.scene.layout.Region;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class RegisterController implements Initializable {

    @FXML private TextField        usernameField;
    @FXML private PasswordField    passwordField;
    @FXML private TextField        dobField;
    @FXML private TextField        addressField;
    @FXML private ComboBox<String> genderBox;
    @FXML private TextField        preferencesField;
    @FXML private Label            messageLabel;
    @FXML private Region           messageSpace;
    @FXML private VBox             formBox;
    @FXML private Button           registerBtn;
    @FXML private StackPane        rootStack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AnimationUtils.fadeIn(formBox);
        genderBox.setItems(FXCollections.observableArrayList("MALE", "FEMALE"));
        usernameField.textProperty().addListener((o, v, n) -> hideMsg());
        passwordField.textProperty().addListener((o, v, n) -> hideMsg());
    }

    @FXML
    private void handleRegister() {
        AnimationUtils.buttonPress(registerBtn);
        hideMsg();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String dobText  = dobField.getText().trim();
        String address  = addressField.getText().trim();
        String gText    = genderBox.getValue();
        String prefs    = preferencesField.getText().trim();

        if (username.isEmpty()) { shake(usernameField); showMsg("Username cannot be empty.", false); return; }
        if (password.isEmpty()) { shake(passwordField); showMsg("Password cannot be empty.", false); return; }
        if (dobText.isEmpty())  { shake(dobField);      showMsg("Date of birth is required.", false); return; }
        if (address.isEmpty())  { shake(addressField);  showMsg("Address cannot be empty.", false); return; }
        if (gText == null)      { shake(genderBox);     showMsg("Please select a gender.", false); return; }

        if (HotelDatabase.findGuestByUsername(username) != null) {
            shake(usernameField);
            showMsg("Username \u201c" + username + "\u201d is already taken.", false);
            return;
        }

        LocalDate dob;
        try { dob = LocalDate.parse(dobText); }
        catch (DateTimeParseException e) {
            shake(dobField);
            showMsg("Use YYYY-MM-DD format (e.g. 2000-05-15).", false);
            return;
        }

        Gender gender;
        try { gender = Gender.valueOf(gText.toUpperCase()); }
        catch (IllegalArgumentException e) { showMsg("Invalid gender.", false); return; }

        Guest g = new Guest();
        try { g.setUsername(username); } catch (InvalidUsernameException e) { shake(usernameField); showMsg(e.getMessage(), false); return; }
        try { g.setPassword(password); } catch (InvalidPasswordException e) { shake(passwordField); showMsg(e.getMessage(), false); return; }
        try { g.setDateOfBirth(dob);   } catch (DobException e)             { shake(dobField);      showMsg(e.getMessage(), false); return; }

        g.setAddress(address);
        g.setGender(gender);
        g.setRoomPreferences(prefs.isEmpty() ? null : prefs);
        g.setBalance(2000.0);

        HotelDatabase.addGuest(g);
        SessionManager.setCurrentUser(g);

        showLoading("Creating your account\u2026");
        PauseTransition pt = new PauseTransition(Duration.millis(800));
        pt.setOnFinished(ev -> {
            try {
                Stage s = (Stage) usernameField.getScene().getWindow();
                if (!s.isMaximized() && !s.isFullScreen()) {
                    if (s.getWidth()  < 1150) s.setWidth(1150);
                    if (s.getHeight() < 760)  s.setHeight(760);
                }
                NavigationManager.navigateTo(s,
                        "views/GuestDashboard.fxml",
                        "Vespera \u2014 Guest Portal");
                ToastManager.success(s, "Welcome, " + username + "! Your account is ready.");
            } catch (Exception e) {
                dismissLoading();
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                showMsg("Navigation failed: " + cause.getMessage(), false);
            }
        });
        pt.play();
    }

    @FXML private void goToLogin() {
        showLoading("Going to sign in\u2026");
        PauseTransition pt = new PauseTransition(Duration.millis(700));
        pt.setOnFinished(ev -> {
            try {
                Stage s = (Stage) usernameField.getScene().getWindow();
                NavigationManager.navigateTo(s,
                        "views/Login.fxml",
                        "Vespera \u2014 Sign In");
            } catch (Exception e) {
                dismissLoading();
                showMsg("Navigation failed: " + e.getMessage(), false);
            }
        });
        pt.play();
    }

    private void showLoading(String msg) {
        if (rootStack == null) return;
        dismissLoading();
        StackPane overlay = AnimationUtils.createLoadingOverlay(msg);
        overlay.setId("loadingOverlay");
        rootStack.getChildren().add(overlay);
    }

    private void dismissLoading() {
        if (rootStack != null)
            rootStack.getChildren().removeIf(n -> "loadingOverlay".equals(n.getId()));
    }

    private void shake(javafx.scene.Node node) {
        AnimationUtils.shake(node);
        AnimationUtils.flashError(node);
    }

    private void showMsg(String msg, boolean ok) {
        messageLabel.setText((ok ? "\u2713   " : "\u26a0   ") + msg);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        messageLabel.getStyleClass().add(ok ? "success-label" : "error-label");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
        if (messageSpace != null) { messageSpace.setVisible(true); messageSpace.setManaged(true); }
        AnimationUtils.slideUp(messageLabel);
    }

    private void hideMsg() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        if (messageSpace != null) { messageSpace.setVisible(false); messageSpace.setManaged(false); }
    }
}
