import discoveryserver.*;

import java.io.IOException;

public class DiscoveryService {

    static final int port = 12345;

    public static void main(String[] args) throws IOException {
        DiscoveryServer server = new DiscoveryServer(port);
        server.start();

        System.out.printf("Discovery server started at port %d\n", port);

        System.in.read();

        server.stopServer();
        System.out.printf("Discovery server stopped");
    }
}
