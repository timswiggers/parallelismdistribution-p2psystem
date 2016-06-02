package peers.communication;

import peers.PeerIndex;
import peers.PeerInfo;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;

class RequestHandler extends Thread {
    private final Socket requestSocket;
    private final PeerIndex peers;

    private BufferedReader requestStream;
    private PrintWriter responseWriter;

    public RequestHandler(Socket requestSocket, PeerIndex peers) {
        this.requestSocket = requestSocket;
        this.peers = peers;
    }

    @Override
    public void run() {
        try {
            this.requestStream = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
            this.responseWriter = new PrintWriter(new OutputStreamWriter(requestSocket.getOutputStream()));

            handleRequest();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest() throws IOException, JAXBException {
        PeerRequestType commandType = PeerRequestType.valueOf(requestStream.readLine());

        switch(commandType) {
            case AcceptPeers: { handleAcceptPeers(); break; }
            case Ping: { handlePing(); break; }
            case UploadFile: { handleUploadFile(); break; }
            default: {
                responseError("Cannot handle command: " + commandType.toString());
                break;
            }
        }
    }

    private void handleAcceptPeers() throws IOException, JAXBException {
        String peerString = requestStream.readLine();
        while(peerString != null && !peerString.isEmpty()){
            if("RESPONSE_END".equals(peerString)){
                break;
            }

            PeerInfo peer = deserializePeer(peerString);
            peers.add(peer);

            peerString = requestStream.readLine();
        }

        responseSuccess();
    }

    private void handlePing() {
        responseSuccess();
    }

    private void handleUploadFile() throws IOException {
        String fileName = requestStream.readLine();
        int bytesLength = Integer.parseInt(requestStream.readLine());

        responseSuccess();
    }

    private void responseSuccess(){
        responseWriter.printf("%s\n", PeerResponseType.Success);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    private void responseError(String message){
        responseWriter.printf("%s\n", PeerResponseType.Error);
        responseWriter.printf("%s\n", message);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    private static PeerInfo deserializePeer(String peerString) {
        String[] parts = peerString.split("\\|");
        String ipAddress = parts[0];
        int vaultPort = Integer.parseInt(parts[1]);

        return new PeerInfo(ipAddress, vaultPort);
    }
}
