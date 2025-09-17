import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatClientGUI {
    private JFrame frame;
    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    private Map<String, Color> userColors = new HashMap<>();
    private Random random = new Random();

    public ChatClientGUI(String serverAddress, int serverPort, String username) {
        this.username = username;

        frame = new JFrame("Chat Client - " + username);
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Chat area
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(new Color(30, 30, 30));
        chatPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatPane);

        // User list sidebar
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(new Color(45, 45, 45));
        userList.setForeground(Color.CYAN);
        userList.setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.WHITE);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(chatScroll, BorderLayout.CENTER);
        frame.add(userScroll, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Networking
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(username);

            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server.");
            System.exit(0);
        }

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            writer.println(message);
            inputField.setText("");
        }
    }

    private void appendMessage(String prefix, String message, Color color, boolean bold) {
        StyledDocument doc = chatPane.getStyledDocument();
        String timeStamp = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ";
        SimpleAttributeSet timeAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(timeAttr, Color.GRAY);

        try {
            doc.insertString(doc.getLength(), timeStamp, timeAttr);

            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, color);
            StyleConstants.setBold(attr, bold);

            if (prefix != null && !prefix.isEmpty()) {
                doc.insertString(doc.getLength(), prefix + " ", attr);
            }
            doc.insertString(doc.getLength(), message + "\n", attr);
        } catch (BadLocationException ignored) {}

        chatPane.setCaretPosition(doc.getLength());
    }

    private class IncomingReader implements Runnable {
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("/users ")) {
                        updateUserList(line.substring(7));
                    } else if (line.startsWith("🔵") || line.startsWith("🔴")) {
                        appendMessage("", line, Color.ORANGE, true);
                    } else if (line.startsWith("📩 [Private]")) {
                        appendMessage("", line, new Color(180, 100, 255), true);
                    } else if (line.startsWith("✅ [Private")) {
                        appendMessage("", line, new Color(255, 140, 200), true);
                    } else if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        String user = parts[0].trim();
                        String msg = parts[1].trim();

                        userColors.putIfAbsent(user, new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));

                        appendMessage(user + ":", msg, userColors.get(user), true);
                    } else {
                        appendMessage("", line, Color.LIGHT_GRAY, false);
                    }
                }
            } catch (IOException e) {
                appendMessage("", "Connection lost.", Color.BLUE, true);
            }
        }
    }

    private void updateUserList(String users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users.split(",")) {
                if (!user.trim().isEmpty()) {
                    userListModel.addElement(user.trim());
                }
            }
        });
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username != null && !username.trim().isEmpty()) {
            new ChatClientGUI("10.10.0.149", 114114, username.trim());
        }
    }
}
