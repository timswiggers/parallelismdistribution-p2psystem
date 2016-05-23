import discovery.*;
import peers.PeerInfo;

import java.io.IOException;
import java.net.*;
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

        CopyOnWriteArrayList<PeerInfo> peers = new CopyOnWriteArrayList<>();
        ServerSocket server = new ServerSocket(port);

        System.out.printf("Discovery server running on localhost port %d\n\n", port);

        try {
            while (true) {
                try {
                    Socket requestSocket = server.accept();
                    SocketHandler requestHandler = new SocketHandler(requestSocket, peers);

                    requestHandler.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            server.close();
        }
    }
}
