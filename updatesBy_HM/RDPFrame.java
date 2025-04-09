import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RDPFrame extends JFrame {
    private final RDPClient client;
    private JLabel screenLabel;

    public RDPFrame(RDPClient client) {
        this.client = client;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Java RDP Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        screenLabel = new JLabel();
        screenLabel.setHorizontalAlignment(JLabel.CENTER);
        screenLabel.setVerticalAlignment(JLabel.CENTER);
        add(new JScrollPane(screenLabel), BorderLayout.CENTER);

        screenLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point labelPoint = screenLabel.getLocationOnScreen();
                Point clickPoint = e.getLocationOnScreen();
                int x = clickPoint.x - labelPoint.x;
                int y = clickPoint.y - labelPoint.y;
                client.sendMouseEvent(x, y, e.getButton(), true); // Added 'true' for press
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point labelPoint = screenLabel.getLocationOnScreen();
                Point clickPoint = e.getLocationOnScreen();
                int x = clickPoint.x - labelPoint.x;
                int y = clickPoint.y - labelPoint.y;
                client.sendMouseEvent(x, y, e.getButton(), false); // Added 'false' for release
            }
        });

        screenLabel.addMouseWheelListener(e -> {
            client.sendMouseScrollEvent(e.getWheelRotation());
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                client.sendKeyEvent(e.getKeyCode(), true); // Added 'true' for press
            }

            @Override
            public void keyReleased(KeyEvent e) {
                client.sendKeyEvent(e.getKeyCode(), false); // Added 'false' for release
            }
        });

        setSize(800, 600);
        setVisible(true);
    }

    public void updateScreen(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        screenLabel.setIcon(icon);
        screenLabel.repaint();
    }
}
