package discovery;

import peers.PeerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketHandler extends Thread {
    private final BufferedReader requestStream;
    private final PrintWriter responseWriter;
    private final CopyOnWriteArrayList<PeerInfo> peers;
    private final Socket socket;

    public SocketHandler(Socket socket, CopyOnWriteArrayList<PeerInfo> peers) throws IOException {
        this.socket = socket;
        this.requestStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.responseWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.peers = peers;
    }

    @Override
    public void run() {
        try {
            handleRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest() throws IOException {
        String commandText = requestStream.readLine();
        DiscoveryCommandType commandType = DiscoveryCommandType.valueOf(commandText);

        switch(commandType) {
            case Join: handleJoin(); break;
            case RequestPeers: handleRequestPeers(); break;
            default: responseError("Cannot handle command: " + commandType.toString()); break;
        }
    }

    private void handleJoin(){
        InetAddress peerAddress = socket.getInetAddress();
        int port = socket.getPort();

        String ipAddress = peerAddress.getHostAddress();
        String peerId = String.format("%s:%s", ipAddress, port);

        if(peers.stream().anyMatch(p -> peerId.equals(p.getId()))){
            return; // don't add this peer twice
        }

        peers.add(new PeerInfo(peerId, ipAddress, port));

        System.out.printf("Peer joined the network: %s\n", peerId);

        responseSuccess();
    }

    private void handleRequestPeers() {
        responseSuccess();
    }

    private void responseSuccess(){
        responseWriter.printf("%s\n", DiscoveryResponseStatus.Success);
        responseWriter.flush();
    }

    private void responseError(String message){
        responseWriter.printf("%s\n", DiscoveryResponseStatus.Success);
        responseWriter.printf("%s\n", message);
        responseWriter.flush();
    }
}
