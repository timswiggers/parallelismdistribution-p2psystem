package discovery;

import peers.PeerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryServer {
    private final int port;

    private final ConcurrentLinkedQueue<RequestHandler> blockedHandlersWaitingForPeers;
    private final CopyOnWriteArrayList<PeerInfo> peers;

    public DiscoveryServer(int port) {
        this.port = port;

        blockedHandlersWaitingForPeers = new ConcurrentLinkedQueue<>();
        peers = new CopyOnWriteArrayList<>();
    }

    public void runUntilInfinity() {

        try (ServerSocket server = new ServerSocket(port)) {

            System.out.printf("Discovery server running on localhost port %d\n\n", port);

            while (true) {
                try {
                    Socket requestSocket = server.accept();
                    RequestHandler requestHandler = new RequestHandler(requestSocket, peers, blockedHandlersWaitingForPeers);

                    // Runs the handling of the request in a new thread, allowing other requests to be handled.
                    requestHandler.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
