package peers.communication;

import java.net.Socket;

/**
 * Created by timsw on 30/05/2016.
 */
class RequestHandler extends Thread {
    private final Socket requestSocket;

    public RequestHandler(Socket requestSocket) {
        this.requestSocket = requestSocket;
    }

    @Override
    public void run() {

    }



    /*private static PeerInfo deserializePeer(String peerString) {
        String[] parts = peerString.split("\\|");
        String id = parts[0];
        String ipAddress = parts[1];
        int vaultPort = Integer.parseInt(parts[2]);

        return new PeerInfo(id, ipAddress, vaultPort);
    }*/
}
