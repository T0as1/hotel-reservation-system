package controllers;

import networking.ChatClient;
import networking.ChatServer;
import utils.AnimationUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable {

    @FXML private VBox messagesBox;
    @FXML private TextField messageInput;
    @FXML private ScrollPane scrollPane;
    @FXML private Label statusLabel;
    @FXML private Label connDot;

    private ChatClient client;
    private String username;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        username = SessionManager.getCurrentUser() != null
                ? SessionManager.getCurrentUser().getUsername() : "Guest";
        client = new ChatClient();
        boolean connected = client.connect("localhost", ChatServer.PORT, username, new ChatClient.MessageReceiver() {
            @Override public void onReceive(String message) {
                if (message.startsWith("WELCOME:")) { setStatus(true, "Connected to reception"); }
                else { addBubble(message, false); scrollToBottom(); }
            }
            @Override public void onDisconnect() { setStatus(false, "Disconnected"); }
        });
        if (connected) {
            setStatus(true, "Connected to reception");
            addSystem("Welcome! Reception is here to help. Type your message below.");
        } else {
            setStatus(false, "Reception offline \u2014 try again later");
            addSystem("Could not connect. The receptionist may not be logged in yet.");
        }
    }

    @FXML private void sendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;
        if (!client.isConnected()) { addSystem("Not connected. Please reopen chat."); return; }
        client.send(text);
        addBubble("You: " + text, true);
        messageInput.clear();
        scrollToBottom();
    }

    private void addBubble(String text, boolean mine) {
        HBox row = new HBox(); row.setPadding(new Insets(2, 0, 2, 0));
        Label msg = new Label(text); msg.setWrapText(true); msg.setMaxWidth(280);
        msg.getStyleClass().add(mine ? "msg-mine" : "msg-other");
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getChildren().add(msg);
        messagesBox.getChildren().add(row);
        AnimationUtils.slideUp(row);
    }

    private void addSystem(String text) {
        Label msg = new Label(text);
        msg.setStyle("-fx-text-fill:#2A4060;-fx-font-size:11px;-fx-font-style:italic;-fx-padding:2 0;");
        msg.setWrapText(true);
        HBox row = new HBox(msg); row.setAlignment(Pos.CENTER); row.setPadding(new Insets(4, 0, 4, 0));
        messagesBox.getChildren().add(row);
        AnimationUtils.fadeInFast(row);
    }

    private void setStatus(boolean online, String text) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText(text);
            connDot.setStyle("-fx-text-fill:" + (online ? "#22C55E" : "#F87171") + ";-fx-font-size:10px;");
        });
    }

    private void scrollToBottom() {
        javafx.application.Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
