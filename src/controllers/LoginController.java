package controllers;

import data.HotelDatabase;
import models.Admin;
import models.Guest;
import models.Receptionist;
import models.User;
import utils.AnimationUtils;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         messageLabel;
    @FXML private Region        messageSpace;
    @FXML private VBox          formBox;
    @FXML private Button        loginBtn;
    @FXML private StackPane     rootStack;
    @FXML private Label         logoLabel;

    //flag to keep error visible when clearing password field
    private boolean suppressHide = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fade in form and hide error when user types
        AnimationUtils.fadeIn(formBox);
        if (logoLabel != null) AnimationUtils.createGlowPulse(logoLabel);
        usernameField.textProperty().addListener((o, v, n) -> { if (!suppressHide) hideMsg(); });
        passwordField.textProperty().addListener((o, v, n) -> { if (!suppressHide) hideMsg(); });
        usernameField.setOnAction(e -> passwordField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        AnimationUtils.buttonPress(loginBtn);
        hideMsg();

        String u = usernameField.getText().trim();
        String p = passwordField.getText();

        if (u.isEmpty()) { fail(usernameField, "Username cannot be empty."); return; }
        if (p.isEmpty()) { fail(passwordField, "Password cannot be empty."); return; }

        User user = HotelDatabase.findUser(u, p);
        if (user == null) {
            suppressHide = true;
            passwordField.clear();
            suppressHide = false;

            AnimationUtils.shake(formBox);
            AnimationUtils.flashError(usernameField);
            AnimationUtils.flashError(passwordField);
            showMsg("Incorrect username or password. Please try again.", false);
            passwordField.requestFocus();
            return;
        }

        SessionManager.setCurrentUser(user);
        showLoading("Welcome to Vespera\u2026");

        PauseTransition pt = new PauseTransition(Duration.millis(900));
        pt.setOnFinished(ev -> {
            try {
                if (user instanceof Admin)
                    nav("views/AdminDashboard.fxml",        "Vespera \u2014 Management",  1180, 760);
                else if (user instanceof Receptionist)
                    nav("views/ReceptionistDashboard.fxml", "Vespera \u2014 Reception",   1180, 760);
                else if (user instanceof Guest)
                    nav("views/GuestDashboard.fxml",        "Vespera \u2014 Guest Portal", 1180, 760);
            } catch (Exception e) {
                dismissLoading();
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                showMsg("Navigation failed: " + cause.getMessage(), false);
            }
        });
        pt.play();
    }

    @FXML private void goToRegister() {
        showLoading("Opening registration\u2026");
        PauseTransition pt = new PauseTransition(Duration.millis(700));
        pt.setOnFinished(ev -> {
            try { nav("views/Register.fxml", "Vespera \u2014 Create Account", 1080, 720); }
            catch (Exception e) {
                dismissLoading();
                showMsg("Navigation failed: " + e.getMessage(), false);
            }
        });
        pt.play();
    }

    private void fail(javafx.scene.Node node, String msg) {
        AnimationUtils.shake(node);
        AnimationUtils.flashError(node);
        showMsg(msg, false);
    }

    private void nav(String fxml, String title, double w, double h) throws Exception {
        Stage s = (Stage) usernameField.getScene().getWindow();
        if (!s.isMaximized() && !s.isFullScreen()) {
            if (s.getWidth()  < w) s.setWidth(w);
            if (s.getHeight() < h) s.setHeight(h);
        }
        NavigationManager.navigateTo(s, fxml, title);
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

    private void showMsg(String msg, boolean ok) {
        messageLabel.setText((ok ? "\u2713   " : "\u26a0   ") + msg);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        messageLabel.getStyleClass().add(ok ? "success-label" : "error-label");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
        if (messageSpace != null) { messageSpace.setVisible(true); messageSpace.setManaged(true); }
        AnimationUtils.slideUp(messageLabel);
    }

    public void showSuccess(String msg) { showMsg(msg, true); }

    private void hideMsg() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        if (messageSpace != null) { messageSpace.setVisible(false); messageSpace.setManaged(false); }
    }
}
