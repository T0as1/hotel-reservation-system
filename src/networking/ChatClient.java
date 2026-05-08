package networking;

import java.io.*;
import java.net.*;


public class ChatClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean connected = false;
    private MessageReceiver receiver;
    private Thread listenThread;

    public interface MessageReceiver {
        void onReceive(String message);
        void onDisconnect();
    }

    public boolean connect(String host, int port, String username, MessageReceiver receiver) {
        this.receiver = receiver;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 3000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;

            out.println("NAME:" + username);

            listenThread = new Thread(() -> {
                try {
                    String msg;
                    while (connected && (msg = in.readLine()) != null) {
                        final String finalMsg = msg;
                        javafx.application.Platform.runLater(() -> receiver.onReceive(finalMsg));
                    }
                } catch (IOException e) {
                    if (connected) {
                        javafx.application.Platform.runLater(() -> receiver.onDisconnect());
                    }
                }
            });
            listenThread.setDaemon(true);
            listenThread.start();
            return true;

        } catch (IOException e) {
            System.out.println("[Client] Cannot connect to server: " + e.getMessage());
            return false;
        }
    }

    public void send(String message) {
        if (connected && out != null) out.println(message);
    }

    public void disconnect() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    public boolean isConnected() { return connected; }
}