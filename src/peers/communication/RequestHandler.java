package peers.communication;

import java.net.Socket;

class RequestHandler extends Thread {
    private final Socket requestSocket;

    public RequestHandler(Socket requestSocket) {
        this.requestSocket = requestSocket;
    }

    @Override
    public void run() {
        System.out.println("GOT REQUEST");
    }



    /*private static PeerInfo deserializePeer(String peerString) {
        String[] parts = peerString.split("\\|");
        String id = parts[0];
        String ipAddress = parts[1];
        int vaultPort = Integer.parseInt(parts[2]);

        return new PeerInfo(id, ipAddress, vaultPort);
    }*/
}
