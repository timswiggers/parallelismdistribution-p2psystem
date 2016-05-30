package discovery.responses;

import peers.PeerInfo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class ResponseHandler extends Thread {
    private final PeerInfo requester;

    public ResponseHandler(PeerInfo requester) {
        this.requester = requester;
    }

    @Override
    public void run() {
        String peerAddress = requester.getIpAddress();
        int peerPort = requester.getPort();
        InetSocketAddress address = new InetSocketAddress(peerAddress, peerPort);

        try {
            respond(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respond(InetSocketAddress address) throws IOException {
        try (Socket socket = new Socket()){
            socket.connect(address);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))){
                writeResponse(writer);
                writer.println("RESPONSE_END");
                writer.flush();
            }
        }
    }

    protected abstract void writeResponse(PrintWriter response);
}
