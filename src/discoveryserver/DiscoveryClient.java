package discoveryserver;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

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
        InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

                // Send request
                outputStream.writeInt(clientPort);
                outputStream.writeInt(command.ordinal());
                outputStream.flush();

                // Get response
                DiscoveryResponseType response = DiscoveryResponseType.values()[inputStream.readInt()];

                if(response == DiscoveryResponseType.Error){
                    byte[] messageBytes = new byte[inputStream.readInt()];
                    inputStream.readFully(messageBytes);
                    String message = new String(messageBytes);

                    throw new RuntimeException(message);
                }

                return response;
            }
        }
    }
}
