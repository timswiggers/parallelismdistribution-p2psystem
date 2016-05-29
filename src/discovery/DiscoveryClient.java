package discovery;

import peers.PeerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class DiscoveryClient {
    private final String peerId;
    private final InetAddress serverAddress;
    private final int serverPort;

    public DiscoveryClient(String peerId, InetAddress serverAddress, int serverPort) {
        this.peerId = peerId;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void joinPeers() throws IOException {
        DiscoveryCommandType command = DiscoveryCommandType.Join;

        send(command); // Not interested in response data
    }

    public void leavePeers() throws IOException {
        DiscoveryCommandType command = DiscoveryCommandType.Leave;

        send(command); // Not interested in response data
    }

    public PeerInfo[] requestPeers() throws IOException {
        DiscoveryCommandType command = DiscoveryCommandType.RequestPeers;
        Collection<String> response = send(command);

        return response.stream().map(DiscoveryClient::deserializePeer).toArray(PeerInfo[]::new);
    }

    private static PeerInfo deserializePeer(String peerString) {
        String[] parts = peerString.split("\\|");
        String id = parts[0];
        String ipAddress = parts[1];
        int vaultPort = Integer.parseInt(parts[2]);

        return new PeerInfo(id, ipAddress, vaultPort);
    }

    private Collection<String> send(DiscoveryCommandType command) throws IOException {
        return send(command, new ArrayList<>());
    }

    private Collection<String> send(DiscoveryCommandType command, Collection<String> requestData) throws IOException {
        InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // The first two lines are always the peer's id and the command of the request, respectively
                writer.printf("%s\n", peerId);
                writer.printf("%s\n", command);

                for(String requestString : requestData) {
                    writer.printf("%s\n", requestString);
                }

                writer.flush();

                String responseString = reader.readLine();
                DiscoveryResponseType response = DiscoveryResponseType.valueOf(responseString);

                if(response != DiscoveryResponseType.Success){
                    String errorMessage = reader.readLine();
                    throw new RuntimeException(errorMessage);
                }

                Collection<String> responseData = new ArrayList<>();

                while((responseString = reader.readLine()) != null) {
                    if("RESPONSE_END".equals(responseString)){
                        break;
                    }

                    responseData.add(responseString);
                }

                return responseData;
            }

        }
    }
}
