import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;

public class NetworkHandler {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public NetworkHandler(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public NetworkHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void sendScreen(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        outputStream.writeObject(imageBytes);
        outputStream.flush();
    }

    public BufferedImage receiveScreen() throws IOException {
        try {
            byte[] imageBytes = (byte[]) inputStream.readObject();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(byteArrayInputStream);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendMouseEvent(int x, int y, int button, boolean isPress) throws IOException {
        outputStream.writeObject(new MousePressReleaseEvent(x, y, button, isPress));
        outputStream.flush();
    }

    public void sendMouseScrollEvent(int scrollAmount) throws IOException {
        outputStream.writeObject(new MouseScrollEvent(scrollAmount));
        outputStream.flush();
    }

    public void sendKeyEvent(int keyCode, boolean isPress) throws IOException {
        outputStream.writeObject(new KeyEvent(keyCode, isPress));
        outputStream.flush();
    }

    public Event receiveEvent() throws IOException {
        try {
            return (Event) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static abstract class Event implements Serializable {}

    public static class MousePressReleaseEvent extends Event {
        public final int x, y, button;
        public final boolean isPress;

        public MousePressReleaseEvent(int x, int y, int button, boolean isPress) {
            this.x = x;
            this.y = y;
            this.button = button;
            this.isPress = isPress;
        }
    }

    public static class MouseScrollEvent extends Event {
        public final int scrollAmount;

        public MouseScrollEvent(int scrollAmount) {
            this.scrollAmount = scrollAmount;
        }
    }

    public static class KeyEvent extends Event {
        public final int keyCode;
        public final boolean isPress;

        public KeyEvent(int keyCode, boolean isPress) {
            this.keyCode = keyCode;
            this.isPress = isPress;
        }
    }
}
