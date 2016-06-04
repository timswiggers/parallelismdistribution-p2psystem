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

    public RequestHandler(Socket requestSocket, PeerIndex peers, LocalVault localVault) {
        super("Peer.Communication.RequestHandler");
        this.requestSocket = requestSocket;
        this.peers = peers;
        this.localVault = localVault;
    }

    @Override
    public void run() {
        try(DataInputStream in = new DataInputStream (requestSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(requestSocket.getOutputStream())){

            handleRequest(in, out);

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(DataInputStream in, DataOutputStream out) throws IOException, JAXBException {
        // Parameter: Command
        PeerRequestType commandType = PeerRequestType.values()[in.readInt()];

        switch(commandType) {
            case AcceptPeers: { handleAcceptPeers(in); break; }
            case Ping: { handlePing(out); break; }
            case UploadFile: { handleUploadFile(in, out); break; }
            default: {
                responseError(out, "Cannot handle command: " + commandType.toString());
                break;
            }
        }
    }

    private void handleAcceptPeers(DataInputStream in) throws IOException, JAXBException {
        int nrOfPeers = in.readInt();

        for(int i = 0; i < nrOfPeers; i++){
            byte[] peerBytes = new byte[in.readInt()];
            in.readFully(peerBytes);
            peers.add(PeerMapper.fromBytes(peerBytes));
        }
    }

    private void handlePing(DataOutputStream out) throws IOException {
        responseSuccess(out);
    }

    private void handleUploadFile(DataInputStream in, DataOutputStream out) throws IOException {

        // Parameter: Peer
        byte[] peerBytes = new byte[in.readInt()];
        in.readFully(peerBytes);
        PeerInfo peer = PeerMapper.fromBytes(peerBytes);
        String peerName = peer.getName();

        // Parameter: File Name
        byte[] fileNameBytes = new byte[in.readInt()];
        in.readFully(fileNameBytes);
        String fileName = new String(fileNameBytes);

        // Parameter: File Bytes
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);

        localVault.store(peerName, fileName, bytes);

        responseSuccess(out);
    }

    private void responseSuccess(DataOutputStream out) throws IOException {
        out.writeInt(PeerResponseType.Success.ordinal());
        out.flush();
    }

    private void responseError(DataOutputStream out, String message) throws IOException {
        out.writeInt(PeerResponseType.Error.ordinal());
        out.writeInt(message.length());
        out.writeBytes(message);
        out.flush();
    }
}
