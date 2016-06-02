package vault.remote;

import peers.PeerInfo;
import peers.communication.PeerRequestType;
import peers.communication.PeerResponseType;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RemoteVault {
    private final PeerInfo peerInfo;

    public RemoteVault(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    public boolean ping() {
        InetSocketAddress address = new InetSocketAddress(peerInfo.getIpAddress(), peerInfo.getPort());

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                writer.printf("%s\n", PeerRequestType.Ping.toString());
                writer.flush();

                String responseString = reader.readLine();
                PeerResponseType response = PeerResponseType.valueOf(responseString);

                if(response == PeerResponseType.Error){
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public byte[] downloadFile(String name) {
        throw new RuntimeException("NOT IMPLEMENTED YET");
    }

    public void uploadFile(String name, byte[] bytes) {
        InetSocketAddress address = new InetSocketAddress(peerInfo.getIpAddress(), peerInfo.getPort());

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (OutputStream outputStream = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                writer.printf("%s\n", PeerRequestType.UploadFile.toString());
                writer.printf("%s\n", name);
                writer.printf("%d", bytes.length);
                writer.flush();

                outputStream.write(bytes);
                outputStream.flush();

                String responseString = reader.readLine();
                PeerResponseType response = PeerResponseType.valueOf(responseString);

                if(response == PeerResponseType.Error){
                    String errorMessage = reader.readLine();
                    throw new RuntimeException(errorMessage);
                }
            }

        } catch (IOException e) {
            return false;
        }
    }
}
