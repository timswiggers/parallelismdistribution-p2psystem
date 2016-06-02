package peers.communication;

import peers.PeerIndex;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationClient extends Thread {

    private int port;
    private final PeerIndex peers;
    private boolean running;

    public CommunicationClient(int port, PeerIndex peers) {
        this.port = port;
        this.peers = peers;
    }

    public int getPort() { return port; }

    @Override
    public void run() {
        running = true;
        acceptRequests();
    }

    public void stopRunning(){
        running = false;
    }

    private void acceptRequests(){
        try (ServerSocket server = new ServerSocket(port)) {

            port = server.getLocalPort();

            while (running) {
                try {
                    Socket requestSocket = server.accept();
                    RequestHandler requestHandler = new RequestHandler(requestSocket, peers);

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
