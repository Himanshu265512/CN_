public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            RDPServer server = new RDPServer();
            server.start();
        } else {
            RDPClient client = new RDPClient();
            client.start();
        }
    }
}