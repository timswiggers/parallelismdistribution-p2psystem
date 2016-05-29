import discovery.*;
import peers.PeerInfo;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryService {

    static final int port = 12345;

    public static void main(String[] args) {
        try {

            runDiscoveryServer(port);

        } catch(IOException e) {

            e.printStackTrace();
        }
    }

    private static void runDiscoveryServer(int port) throws IOException {

        ConcurrentLinkedQueue<SocketHandler> blockedHandlersWaitingForPeers = new ConcurrentLinkedQueue<>();
        CopyOnWriteArrayList<PeerInfo> peers = new CopyOnWriteArrayList<>();

        try (ServerSocket server = new ServerSocket(port)) {

            System.out.printf("Discovery server running on localhost port %d\n\n", port);

            while (true) {
                try {
                    Socket requestSocket = server.accept();
                    SocketHandler requestHandler = new SocketHandler(requestSocket, peers, blockedHandlersWaitingForPeers);

                    // Runs the handling of the request in a new thread, allowing other requests to be handled.
                    requestHandler.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
