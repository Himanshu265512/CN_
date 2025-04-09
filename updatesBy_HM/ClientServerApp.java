import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class ClientServerApp {
    private static JFrame frame;
    private static RDPServer server;
    private static JLabel statusLabel;
    private static JLabel ipLabel;
    private static JLabel portLabel;
    private static JButton disconnectButton;
    private static JButton reconnectButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientServerApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Client-Server Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 45));

        JLabel titleLabel = new JLabel("Remote Desktop Connection");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JButton clientButton = new JButton("Enter as Client");
        JButton serverButton = new JButton("Enter as Server");

        styleButton(clientButton, new Color(30, 144, 255));
        styleButton(serverButton, new Color(34, 139, 34));

        clientButton.addActionListener(e -> startClient());
        serverButton.addActionListener(e -> startServer());

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

    private static void startServer() {
        frame.dispose(); // Close selection screen

        JFrame serverFrame = new JFrame("RDP Server");
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.setSize(400, 250);
        serverFrame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));

        statusLabel = new JLabel("Starting Server...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);

        ipLabel = new JLabel("IP Address: Fetching...");
        ipLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        ipLabel.setForeground(Color.WHITE);

        portLabel = new JLabel("Port: Fetching...");
        portLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        portLabel.setForeground(Color.WHITE);

        disconnectButton = new JButton("Disconnect");
        reconnectButton = new JButton("Reconnect");

        styleButton(disconnectButton, Color.RED);
        styleButton(reconnectButton, new Color(0, 153, 0));

        disconnectButton.setVisible(false);
        reconnectButton.setVisible(false);

        disconnectButton.addActionListener(e -> disconnectServer());
        reconnectButton.addActionListener(e -> startServer());

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

        gbc.gridy = 4;
        panel.add(reconnectButton, gbc);

        serverFrame.add(panel, BorderLayout.CENTER);
        serverFrame.setLocationRelativeTo(null);
        serverFrame.setVisible(true);

        new Thread(() -> {
            try {
                server = new RDPServer();
                server.start();

                InetAddress ip = InetAddress.getLocalHost();
                String ipAddress = ip.getHostAddress();
                int port = server.getPort(); // Fetch the correct port

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Server Running...");
                    ipLabel.setText("IP Address: " + ipAddress);
                    portLabel.setText("Port: " + port);
                    disconnectButton.setVisible(true);
                });
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> statusLabel.setText("Error Starting Server"));
            }
        }).start();
    }

    private static void disconnectServer() {
        if (server != null) {
            server.stopServer(); // Ensure RDPServer has a stopServer() method
            statusLabel.setText("Server Disconnected");
            disconnectButton.setVisible(false);
            reconnectButton.setVisible(true);
        }
    }

    private static void startClient() {
        frame.dispose(); // Close selection screen

        new Thread(() -> {
            RDPClient client = new RDPClient();
            client.start();
        }).start();
    }
}
