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
        Socket socket = new Socket();
        socket.connect(address);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        writer.printf("%s\n", command);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String responseMessage = reader.readLine();

        System.out.println(responseMessage);

        socket.close();
    }
}
