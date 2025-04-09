import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class ClientServerApp {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static JFrame waitingFrame;
    private static JLabel statusLabel;
    private static JButton disconnectButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientServerApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Client-Server Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 45));

        JLabel titleLabel = new JLabel("Remote Desktop Connection");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JButton clientButton = new JButton("Enter as Client");
        JButton serverButton = new JButton("Enter as Server");

        styleButton(clientButton, new Color(30, 144, 255));
        styleButton(serverButton, new Color(34, 139, 34));

        clientButton.addActionListener(e -> runCommand(new String[]{"java", "Main"}));
        serverButton.addActionListener(e -> startServer(frame));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(clientButton, gbc);

        gbc.gridy = 2;
        panel.add(serverButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(180, 50));
    }

    private static void startServer(JFrame oldFrame) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(0); // Pick an available port
                int assignedPort = serverSocket.getLocalPort();
                String ipAddress = getLocalIPAddress();
                showWaitingWindow(oldFrame, ipAddress, assignedPort);

                clientSocket = serverSocket.accept(); // Wait for client
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Client Connected!");
                    disconnectButton.setVisible(true);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void showWaitingWindow(JFrame oldFrame, String ip, int port) {
        SwingUtilities.invokeLater(() -> {
            oldFrame.dispose();

            waitingFrame = new JFrame("Server Running");
            waitingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            waitingFrame.setSize(400, 250);
            waitingFrame.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(50, 50, 50));

            statusLabel = new JLabel("Waiting for connection...");
            statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
            statusLabel.setForeground(Color.WHITE);

            JLabel ipLabel = new JLabel("IP Address: " + ip);
            ipLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            ipLabel.setForeground(Color.WHITE);

            JLabel portLabel = new JLabel("Port: " + port);
            portLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            portLabel.setForeground(Color.WHITE);

            disconnectButton = new JButton("Disconnect");
            disconnectButton.setFont(new Font("Arial", Font.BOLD, 16));
            disconnectButton.setBackground(Color.RED);
            disconnectButton.setForeground(Color.WHITE);
            disconnectButton.setFocusPainted(false);
            disconnectButton.setVisible(false);  // Initially hidden
            disconnectButton.addActionListener(e -> disconnectClient());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(statusLabel, gbc);

            gbc.gridy = 1;
            panel.add(ipLabel, gbc);

            gbc.gridy = 2;
            panel.add(portLabel, gbc);

            gbc.gridy = 3;
            panel.add(disconnectButton, gbc);

            waitingFrame.add(panel, BorderLayout.CENTER);
            waitingFrame.setLocationRelativeTo(null);
            waitingFrame.setVisible(true);
        });
    }

    private static String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
