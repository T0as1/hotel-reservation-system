package controllers;

import data.HotelDatabase;
import models.Admin;
import models.Guest;
import models.Receptionist;
import models.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameField.textProperty().addListener((obs, old, now) -> hideMessages());
        passwordField.textProperty().addListener((obs, old, now) -> hideMessages());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        User user = HotelDatabase.findUser(username, password);

        if (user == null) {
            showError("Incorrect username or password. Please try again.");
            return;
        }

        try {
            SessionManager.setCurrentUser(user);

            if (user instanceof Admin) {
                switchScene("views/AdminDashboard.fxml", "Admin Dashboard");
            } else if (user instanceof Receptionist) {
                switchScene("views/ReceptionistDashboard.fxml", "Receptionist Dashboard");
            } else if (user instanceof Guest) {
                switchScene("views/GuestDashboard.fxml", "Guest Dashboard");
            }

        } catch (Exception e) {
            // Dashboard not built yet — show green success message
            showSuccess("Welcome back, " + user.getUsername() + "! Dashboard coming soon.");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            switchScene("views/Register.fxml", "Register");
        } catch (Exception e) {
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void switchScene(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/views/hotel.css").toExternalForm());
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText("⚠  " + message);
        errorLabel.setStyle(
                "-fx-background-color: #fff0f0;" +
                        "-fx-text-fill: #c0392b;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: #f5c6c6;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-width: 1px;"
        );
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText("✓  " + message);
        errorLabel.setStyle(
                "-fx-background-color: #f0fff4;" +
                        "-fx-text-fill: #1e7e34;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: #b8e6c4;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-width: 1px;"
        );
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}