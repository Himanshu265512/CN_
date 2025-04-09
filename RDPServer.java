import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;

public class RDPServer {
    private ServerSocket serverSocket;
    private NetworkHandler networkHandler;
    private ScreenCapture screenCapture;

    public void start() {
        try {
            int port = Config.DEFAULT_PORT;
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            screenCapture = new ScreenCapture();
            networkHandler = new NetworkHandler(serverSocket.accept());
            System.out.println("Client connected");

            startSendingUpdates();
            startReceivingEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSendingUpdates() {
        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage image = screenCapture.captureScreen();
                    networkHandler.sendScreen(image);
                    Thread.sleep(Config.REFRESH_RATE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startReceivingEvents() {
        new Thread(() -> {
            try {
                while (true) {
                    NetworkHandler.Event event = networkHandler.receiveEvent();
                    if (event != null) {
                        handleEvent(event);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleEvent(NetworkHandler.Event event) {
        try {
            Robot robot = new Robot();
            if (event instanceof NetworkHandler.MouseEvent) {
                NetworkHandler.MouseEvent mouseEvent = (NetworkHandler.MouseEvent) event;
                robot.mouseMove(mouseEvent.x, mouseEvent.y);
                if (mouseEvent.button == 1) {
                    robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                } else if (mouseEvent.button == 3) {
                    robot.mousePress(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
                }
            } else if (event instanceof NetworkHandler.KeyEvent) {
                NetworkHandler.KeyEvent keyEvent = (NetworkHandler.KeyEvent) event;
                robot.keyPress(keyEvent.keyCode);
                robot.keyRelease(keyEvent.keyCode);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}