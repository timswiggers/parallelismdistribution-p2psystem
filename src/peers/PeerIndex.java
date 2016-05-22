package peers;

import io.local.FileAccess;

public class PeerIndex {
    private final FileAccess files;

    public PeerIndex(FileAccess files) {
        this.files = files;
    }

    public PeerInfo get(String peerId) {
        return null; // TODO: Implement
    }
}
