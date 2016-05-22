package vault.remote;

import peers.PeerInfo;

public class RemoteVault {
    private final PeerInfo peerInfo;

    public RemoteVault(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    public boolean connect() {
        return false; // TODO: implement
    }

    public byte[] downloadFile(String name) {
        return new byte[0]; // TODO: implement
    }

    public void uploadFile(String name, byte[] bytes) {
        // TODO: implement
    }
}
