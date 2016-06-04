package filesystem;

import peers.PeerInfo;

public class FileSystemEntry {

    private final String name;
    private final int size;
    private final String hash;
    private final PeerInfo peer;

    public FileSystemEntry(String name, int size, String hash, PeerInfo peer) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.peer = peer;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public String getHash() {
        return hash;
    }

    public PeerInfo getPeer() { return peer; }
}
