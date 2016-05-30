package peers.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationClient {

    private CommunicationClientState state;
    private final int port;

    enum CommunicationClientState {
        Stopped,
        Started
    }

    public CommunicationClient(int port) {
        this.port = port;
        this.state = CommunicationClientState.Stopped;
    }

    public void start(){
        state = CommunicationClientState.Started;
        acceptRequests();
    }

    public void stop(){
        state = CommunicationClientState.Stopped;
    }

    private void acceptRequests(){
        try (ServerSocket server = new ServerSocket(port)) {

            System.out.printf("Discovery server running on localhost port %d\n\n", port);

            while (state == CommunicationClientState.Started) {
                try {
                    Socket requestSocket = server.accept();
                    SocketHandler requestHandler = new SocketHandler(requestSocket);

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
