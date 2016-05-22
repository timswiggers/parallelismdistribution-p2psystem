package vault.remote;

import peers.PeerInfo;

public class RemoteVault {
    private final PeerInfo peerInfo;

    public RemoteVault(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    public boolean connect() {
        return false;
    }

    public byte[] downloadFile(String name) {
        return new byte[0];
    }
}
