package discovery;

import peers.PeerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DiscoveryClient {
    private final InetAddress serverAddress;
    private final int serverPort;

    public DiscoveryClient(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public PeerInfo[] requestPeers(){
        DiscoveryCommandType command = DiscoveryCommandType.RequestPeers;

        return null;
    }

    public void joinPeers() throws IOException {
        DiscoveryCommandType command = DiscoveryCommandType.Join;

        send(command);
    }

    private void send(DiscoveryCommandType command) throws IOException {
        InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                writer.printf("%s\n", command);
                writer.flush();

                String responseString = reader.readLine();
                DiscoveryResponseType response = DiscoveryResponseType.valueOf(responseString);

                if(response != DiscoveryResponseType.Success){
                    throw new RuntimeException("Could not join peers on the discovery service");
                }
            }

        }
    }
}
