import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 114114;
    private static Map<String, PrintWriter> clients = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        System.out.println("Chat server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error in server: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private String username;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // Ask for username
                writer.println("Enter your username:");
                username = reader.readLine();

                if (username == null || username.trim().isEmpty()) {
                    writer.println("Invalid username. Connection closed.");
                    socket.close();
                    return;
                }

                synchronized (clients) {
                    if (clients.containsKey(username)) {
                        writer.println("Username already taken. Connection closed.");
                        socket.close();
                        return;
                    } else {
                        clients.put(username, writer);
                    }
                }

                broadcast("🔵 " + username + " has joined the chat!", null);
                updateUserList();
                System.out.println(username + " connected.");

                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) break;

                    if (message.startsWith("/msg ")) {
                        handlePrivateMessage(message);
                    } else {
                        broadcast(username + ": " + message, username);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection error with " + username);
            } finally {
                if (username != null) {
                    clients.remove(username);
                    broadcast("🔴 " + username + " has left the chat.", null);
                    updateUserList();
                    System.out.println(username + " disconnected.");
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private void handlePrivateMessage(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                writer.println("⚠ Usage: /msg <username> <message>");
                return;
            }
            String targetUser = parts[1];
            String privateMsg = parts[2];

            PrintWriter targetWriter;
            synchronized (clients) {
                targetWriter = clients.get(targetUser);
            }

            if (targetWriter != null) {
                targetWriter.println("📩 [Private] " + username + ": " + privateMsg);
                writer.println("✅ [Private to " + targetUser + "] " + privateMsg);
            } else {
                writer.println("⚠ User " + targetUser + " not found.");
            }
        }
    }

    private static void broadcast(String message, String excludeUser) {
        synchronized (clients) {
            for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
                if (!entry.getKey().equals(excludeUser)) {
                    entry.getValue().println(message);
                }
            }
        }
    }

    private static void updateUserList() {
        synchronized (clients) {
            String userList = String.join(",", clients.keySet());
            for (PrintWriter writer : clients.values()) {
                writer.println("/users " + userList);
            }
        }
    }
}
