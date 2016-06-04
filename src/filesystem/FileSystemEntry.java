package filesystem;

import peers.PeerInfo;

public class FileSystemEntry {

    private final String name;
    private final int size;
    private final String hash;
    private final String key;
    private final PeerInfo peer;

    public FileSystemEntry(String name, int size, String hash, String key, PeerInfo peer) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.peer = peer;
        this.key = key;
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

    public String getKey() { return key; }

    public PeerInfo getPeer() { return peer; }
}
