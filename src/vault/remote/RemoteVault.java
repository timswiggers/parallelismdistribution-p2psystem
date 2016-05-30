package vault.remote;

import peers.PeerInfo;

public class RemoteVault {
    private final PeerInfo peerInfo;

    public RemoteVault(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    public boolean connect() {
        throw new RuntimeException("NOT IMPLEMENTED YET");
    }

    public byte[] downloadFile(String name) {
        throw new RuntimeException("NOT IMPLEMENTED YET");
    }

    public void uploadFile(String name, byte[] bytes) {
        throw new RuntimeException("NOT IMPLEMENTED YET");
    }
}
