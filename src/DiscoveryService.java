import discovery.*;
import peers.PeerInfo;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryService {

    static final int port = 12345;

    public static void main(String[] args) {
        DiscoveryServer server = new DiscoveryServer(port);
        server.runUntilInfinity();
    }
}
