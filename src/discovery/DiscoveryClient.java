package discovery;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class DiscoveryClient {
    private final int clientPort;
    private final InetAddress serverAddress;
    private final int serverPort;

    public DiscoveryClient(int clientPort, InetAddress serverAddress, int serverPort) {
        this.clientPort = clientPort;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public boolean joinPeers() throws IOException {
        return send(DiscoveryRequestType.Join) == DiscoveryResponseType.Success;
    }

    public boolean leavePeers() throws IOException {
        return send(DiscoveryRequestType.Leave) == DiscoveryResponseType.Success;
    }

    public boolean requestPeers() throws IOException {
        return send(DiscoveryRequestType.RequestPeers) == DiscoveryResponseType.Success;
    }

    private DiscoveryResponseType send(DiscoveryRequestType command) throws IOException {
        return send(command, new ArrayList<>());
    }

    private DiscoveryResponseType send(DiscoveryRequestType command, Collection<String> requestData) throws IOException {
        InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // The first two lines are always the clients communication port and the command of the request, respectively
                writer.printf("%s\n", clientPort);
                writer.printf("%s\n", command);

                for(String requestString : requestData) {
                    writer.printf("%s\n", requestString);
                }

                writer.flush();

                String responseString = reader.readLine();
                DiscoveryResponseType response = DiscoveryResponseType.valueOf(responseString);

                if(response == DiscoveryResponseType.Error){
                    String errorMessage = reader.readLine();
                    throw new RuntimeException(errorMessage);
                }

                return response;
            }
        }
    }
}
