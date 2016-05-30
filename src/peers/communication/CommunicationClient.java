package peers.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationClient extends Thread {

    private int port;

    public CommunicationClient(int port) {
        this.port = port;
    }

    public int getPort() { return port; }

    @Override
    public void run() {
        acceptRequests();
    }

    private void acceptRequests(){
        try (ServerSocket server = new ServerSocket(port)) {

            port = server.getLocalPort();

            while (true) {
                try {
                    Socket requestSocket = server.accept();
                    RequestHandler requestHandler = new RequestHandler(requestSocket);

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
