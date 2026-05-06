package networking;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {

    public static final int PORT = 9090;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = false;
    private Thread serverThread;

    private static ChatServer instance;
    public static ChatServer getInstance() {
        if (instance == null) instance = new ChatServer();
        return instance;
    }

    public void start() {
        if (running) return;
        running = true;
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[Server] Chat server started on port " + PORT);
                while (running) {
                    try {
                        Socket client = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(client, this);
                        clients.add(handler);
                        new Thread(handler).start();
                        System.out.println("[Server] New client connected: " + client.getInetAddress());
                    } catch (SocketException e) {
                        if (!running) break; // normal shutdown
                    }
                }
            } catch (IOException e) {
                System.out.println("[Server] Error: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
    }

    public boolean isRunning() { return running; }

    public void broadcast(String message, ClientHandler sender) {
        String timestamped = "[" + LocalTime.now().format(TIME_FMT) + "] " + message;
        for (ClientHandler c : clients) {
            if (c != sender) c.send(timestamped);
        }
        System.out.println("[Server] " + timestamped);
    }

    public void addMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    private MessageListener listener;

    public void notifyListener(String from, String message) {
        if (listener != null) {
            String full = "[" + LocalTime.now().format(TIME_FMT) + "] " + from + ": " + message;
            listener.onMessage(full);
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public interface MessageListener {
        void onMessage(String message);
    }

    public static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ChatServer server;
        private PrintWriter out;
        private String clientName = "Guest";

        public ClientHandler(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.out = writer;
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("NAME:")) {
                        clientName = line.substring(5);
                        send("WELCOME:" + clientName);
                    } else {
                        server.notifyListener(clientName, line);
                        server.broadcast(clientName + ": " + line, this);
                    }
                }
            } catch (IOException e) {
                System.out.println("[Server] Client disconnected: " + clientName);
            } finally {
                server.removeClient(this);
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        public void send(String message) {
            if (out != null) out.println(message);
        }
    }
}