import discoveryserver.*;

public class DiscoveryService {

    static final int port = 12345;

    public static void main(String[] args) {
        DiscoveryServer server = new DiscoveryServer(port);
        server.runUntilInfinity();
    }
}
