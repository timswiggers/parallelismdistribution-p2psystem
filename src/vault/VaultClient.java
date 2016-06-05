package vault;

import common.ResponseCode;
import peers.PeerInfo;
import peers.PeerMapper;
import peers.networkclient.PeerRequestType;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class VaultClient {
    private final PeerInfo peerInfo;

    public VaultClient(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    public boolean ping() {
        InetSocketAddress address = new InetSocketAddress(peerInfo.getIpAddress(), peerInfo.getPort());

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try (DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

                // Send request
                outputStream.writeInt(PeerRequestType.Ping.ordinal());
                outputStream.flush();

                // Get response
                ResponseCode response = ResponseCode.values()[inputStream.readInt()];

                if(response == ResponseCode.Error){
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void uploadFile(String fileName, byte[] bytes) throws IOException {
        InetSocketAddress address = new InetSocketAddress(peerInfo.getIpAddress(), peerInfo.getPort());

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try(DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

                // Send request
                // Parameter: Command
                outputStream.writeInt(PeerRequestType.UploadFile.ordinal());
                // Parameter: Peer
                outputStream.writeInt(PeerMapper.asBytes(peerInfo).length);
                outputStream.write(PeerMapper.asBytes(peerInfo));
                // Parameter: File Name
                outputStream.writeInt(fileName.length());
                outputStream.writeBytes(fileName);
                // Parameter: File Bytes
                outputStream.writeInt(bytes.length);
                outputStream.write(bytes);

                outputStream.flush();

                // Get response
                ResponseCode response = ResponseCode.values()[inputStream.readInt()];

                if(response == ResponseCode.Error){
                    byte[] messageBytes = new byte[inputStream.readInt()];
                    inputStream.readFully(messageBytes);
                    String message = new String(messageBytes);

                    throw new RuntimeException(message);
                }
            }
        }
    }

    public byte[] downloadFile(String fileName) throws IOException {
        InetSocketAddress address = new InetSocketAddress(peerInfo.getIpAddress(), peerInfo.getPort());

        try (Socket socket = new Socket()) {

            socket.connect(address);

            try(DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

                // Send request
                // Parameter: Command
                outputStream.writeInt(PeerRequestType.DownloadFile.ordinal());
                // Parameter: Peer
                outputStream.writeInt(PeerMapper.asBytes(peerInfo).length);
                outputStream.write(PeerMapper.asBytes(peerInfo));
                // Parameter: File Name
                outputStream.writeInt(fileName.length());
                outputStream.writeBytes(fileName);

                outputStream.flush();

                // Get response
                ResponseCode response = ResponseCode.values()[inputStream.readInt()];

                if(response == ResponseCode.File){

                    byte[] fileBytes = new byte[inputStream.readInt()];
                    inputStream.readFully(fileBytes);

                    return fileBytes;

                } else if(response == ResponseCode.Error) {
                    byte[] messageBytes = new byte[inputStream.readInt()];
                    inputStream.readFully(messageBytes);
                    String message = new String(messageBytes);

                    throw new RuntimeException(message);
                } else{
                    throw new RuntimeException("Unexpected response: " + response.toString());
                }
            }
        }
    }
}
