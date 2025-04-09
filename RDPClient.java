import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RDPClient {
    private NetworkHandler networkHandler;
    private RDPFrame frame;

    public void start() {
        String serverAddress = JOptionPane.showInputDialog("Enter server address:");
        int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port number:", "12345"));
        
        try {
            networkHandler = new NetworkHandler(serverAddress, port);
            frame = new RDPFrame(this);
            startReceivingUpdates();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
        }
    }

    private void startReceivingUpdates() {
        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage image = networkHandler.receiveScreen();
                    if (image != null) {
                        frame.updateScreen(image);
                    }
                    Thread.sleep(Config.REFRESH_RATE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMouseEvent(int x, int y, int button) {
        try {
            networkHandler.sendMouseEvent(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendKeyEvent(int keyCode) {
        try {
            networkHandler.sendKeyEvent(keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}