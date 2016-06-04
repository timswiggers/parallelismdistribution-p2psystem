package peers.communication;

import peers.PeerIndex;
import peers.PeerInfo;
import peers.PeerMapper;
import vault.local.LocalVault;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;

class RequestHandler extends Thread {
    private final Socket requestSocket;
    private final PeerIndex peers;
    private final LocalVault localVault;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public RequestHandler(Socket requestSocket, PeerIndex peers, LocalVault localVault) {
        super("Peer.Communication.RequestHandler");
        this.requestSocket = requestSocket;
        this.peers = peers;
        this.localVault = localVault;
    }

    @Override
    public void run() {
        try {
            this.inputStream = new DataInputStream (requestSocket.getInputStream());
            this.outputStream = new DataOutputStream(requestSocket.getOutputStream());

            handleRequest();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest() throws IOException, JAXBException {
        // Parameter: Command
        PeerRequestType commandType = PeerRequestType.values()[inputStream.readInt()];

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
        int nrOfPeers = inputStream.readInt();

        for(int i = 0; i < nrOfPeers; i++){
            byte[] peerBytes = new byte[inputStream.readInt()];
            inputStream.readFully(peerBytes);
            peers.add(PeerMapper.fromBytes(peerBytes));
        }

        responseSuccess();
    }

    private void handlePing() throws IOException {
        responseSuccess();
    }

    private void handleUploadFile() throws IOException {

        // Parameter: Peer
        byte[] peerBytes = new byte[inputStream.readInt()];
        inputStream.readFully(peerBytes);
        PeerInfo peer = PeerMapper.fromBytes(peerBytes);
        String peerName = peer.getName();

        // Parameter: File Name
        byte[] fileNameBytes = new byte[inputStream.readInt()];
        inputStream.readFully(fileNameBytes);
        String fileName = new String(fileNameBytes);

        // Parameter: File Bytes
        byte[] bytes = new byte[inputStream.readInt()];
        inputStream.readFully(bytes);

        localVault.store(peerName, fileName, bytes);

        responseSuccess();
    }

    private void responseSuccess() throws IOException {
        outputStream.writeInt(PeerResponseType.Success.ordinal());
        outputStream.flush();
    }

    private void responseError(String message) throws IOException {
        outputStream.writeInt(PeerResponseType.Error.ordinal());
        outputStream.writeInt(message.length());
        outputStream.writeBytes(message);
        outputStream.flush();
    }
}
