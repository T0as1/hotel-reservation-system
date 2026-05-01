package controllers;

import data.HotelDatabase;
import enums.Gender;
import exceptions.DobException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidUsernameException;
import models.Guest;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     dobField;
    @FXML private TextField     addressField;
    @FXML private ComboBox<String> genderBox;
    @FXML private TextField     preferencesField;
    @FXML private Label         errorLabel;
    @FXML private Label         successLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genderBox.setItems(FXCollections.observableArrayList("MALE", "FEMALE"));
        usernameField.textProperty().addListener((o, old, now) -> hideMessages());
        passwordField.textProperty().addListener((o, old, now) -> hideMessages());
        dobField.textProperty().addListener((o, old, now)      -> hideMessages());
    }

    @FXML
    private void handleRegister() {
        hideMessages();

        String username   = usernameField.getText().trim();
        String password   = passwordField.getText();
        String dobText    = dobField.getText().trim();
        String address    = addressField.getText().trim();
        String genderText = genderBox.getValue();
        String prefs      = preferencesField.getText().trim();

        // Empty checks
        if (username.isEmpty()) { showError("Username cannot be empty."); return; }
        if (password.isEmpty()) { showError("Password cannot be empty."); return; }
        if (dobText.isEmpty())  { showError("Date of birth is required."); return; }
        if (address.isEmpty())  { showError("Address cannot be empty."); return; }
        if (genderText == null) { showError("Please select a gender."); return; }

        // Username taken check
        if (HotelDatabase.findGuestByUsername(username) != null) {
            showError("Username \"" + username + "\" is already taken.");
            return;
        }

        // Parse date
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobText);
        } catch (DateTimeParseException e) {
            showError("Invalid date. Use YYYY-MM-DD format (e.g. 2000-05-15).");
            return;
        }

        // Parse gender
        Gender gender;
        try {
            gender = Gender.valueOf(genderText.toUpperCase());
        } catch (IllegalArgumentException e) {
            showError("Invalid gender selection.");
            return;
        }

        // Create guest with validation
        Guest newGuest = new Guest();

        try {
            newGuest.setUsername(username);
        } catch (InvalidUsernameException e) {
            showError("Username error: " + e.getMessage()); return;
        }

        try {
            newGuest.setPassword(password);
        } catch (InvalidPasswordException e) {
            showError("Password error: " + e.getMessage()); return;
        }

        try {
            newGuest.setDateOfBirth(dob);
        } catch (DobException e) {
            showError("Date of birth error: " + e.getMessage()); return;
        }

        newGuest.setAddress(address);
        newGuest.setGender(gender);
        newGuest.setRoomPreferences(prefs.isEmpty() ? null : prefs);
        newGuest.setBalance(2000.0);

        HotelDatabase.addGuest(newGuest);

        showSuccess("Account created! Welcome, " + username + ". You can now sign in.");
        clearForm();
    }

    @FXML
    private void goToLogin() {
        try {
            switchScene("views/Login.fxml", "Login");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
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

    private void showError(String msg) {
        errorLabel.setText("⚠  " + msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    private void showSuccess(String msg) {
        successLabel.setText("✓  " + msg);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        dobField.clear();
        addressField.clear();
        genderBox.setValue(null);
        preferencesField.clear();
    }
}