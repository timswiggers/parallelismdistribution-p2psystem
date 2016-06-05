package peers.networkclient;

import common.Request;
import common.Response;
import peers.PeerIndex;
import peers.PeerInfo;
import vault.Vault;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;

class RequestHandler extends Thread {
    private final Socket socket;
    private final PeerIndex peers;
    private final Vault vault;

    public RequestHandler(Socket socket, PeerIndex peers, Vault vault) {
        super("Peer.Communication.RequestHandler");
        this.socket = socket;
        this.peers = peers;
        this.vault = vault;
    }

    @Override
    public void run() {
        try(DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            Request request = new Request(inputStream);
            Response response = new Response(outputStream);

            routeRequest(request, response);

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    /*
    * Request Router
    * */

    private void routeRequest(Request request, Response response) throws IOException, JAXBException {
        // Parse request
        PeerRequestType commandType = PeerRequestType.values()[request.getIntParameter()];

        // Route request
        switch(commandType) {
            case Ping: { handlePing(response); break; }
            case AcceptPeers: { handleAcceptPeers(request); break; }
            case UploadFile: { handleUploadFile(request, response); break; }
            case DownloadFile: { handleDownloadFile(request, response); break; }
            default: {
                response.error("Cannot handle command: " + commandType.toString());
                break;
            }
        }
    }

    /*
    * Discovery Request: Accept Peers
    * */

    private void handleAcceptPeers(Request request) throws IOException, JAXBException {
        // Parse request
        int nrOfPeers = request.getIntParameter();

        // Handle request
        for(int i = 0; i < nrOfPeers; i++){
            PeerInfo peer = request.getPeerParameter();
            peers.add(peer);
        }
    }

    /*
    * Peer Request: Ping
    * */

    private void handlePing(Response response) throws IOException {
        // Send response
        response.success();
    }

    /*
    * Peer Request: Upload File
    * */

    private void handleUploadFile(Request request, Response response) throws IOException {
        // Parse request
        String peerName = request.getPeerParameter().getName();
        String fileName = request.getStringParameter();
        byte[] bytes = request.getBytesParameter();

        // Handle request
        vault.store(peerName, fileName, bytes);

        // Send response
        response.success();
    }

    /*
    * Peer Request: Download File
    * */

    private void handleDownloadFile(Request request, Response response) throws IOException {
        // Parse request
        String peerName = request.getPeerParameter().getName();
        String fileName = request.getStringParameter();

        // Handle request
        byte[] bytes = vault.load(peerName, fileName);

        // Send response
        response.file(bytes);
    }
}
