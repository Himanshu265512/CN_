import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;

public class RDPServer {
    private ServerSocket serverSocket;
    private NetworkHandler networkHandler;
    private ScreenCapture screenCapture;

    public void start() {
        try {
            serverSocket = new ServerSocket(0);
            int assignedPort = serverSocket.getLocalPort();
            System.out.println("Server started on port " + assignedPort);

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
                    Thread.sleep(100);
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

            if (event instanceof NetworkHandler.MousePressReleaseEvent) {
                NetworkHandler.MousePressReleaseEvent mouseEvent = (NetworkHandler.MousePressReleaseEvent) event;
                robot.mouseMove(mouseEvent.x, mouseEvent.y);
                int buttonMask = getMouseButtonMask(mouseEvent.button);
                if (mouseEvent.isPress) {
                    robot.mousePress(buttonMask);
                } else {
                    robot.mouseRelease(buttonMask);
                }
            } else if (event instanceof NetworkHandler.MouseScrollEvent) {
                robot.mouseWheel(((NetworkHandler.MouseScrollEvent) event).scrollAmount);
            } else if (event instanceof NetworkHandler.KeyEvent) {
                NetworkHandler.KeyEvent keyEvent = (NetworkHandler.KeyEvent) event;
                if (keyEvent.isPress) {
                    robot.keyPress(keyEvent.keyCode);
                } else {
                    robot.keyRelease(keyEvent.keyCode);
                }
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    public int getPort() {
        return serverSocket.getLocalPort();
    }
    
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private int getMouseButtonMask(int button) {
        switch (button) {
            case 1:
                return InputEvent.BUTTON1_DOWN_MASK;
            case 2:
                return InputEvent.BUTTON2_DOWN_MASK;
            case 3:
                return InputEvent.BUTTON3_DOWN_MASK;
            default:
                return 0;
        }
    }
}
