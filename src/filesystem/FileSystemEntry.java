package filesystem;

import peers.PeerInfo;

public class FileSystemEntry {

    private final String name;
    private final int size;
    private final String hash;
    private final String key;
    private final String iv;
    private final PeerInfo peer;

    public FileSystemEntry(String name, int size, String hash, String key, String iv, PeerInfo peer) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.key = key;
        this.iv = iv;
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

    public String getKey() { return key; }

    public String getIV() { return iv; }

    public PeerInfo getPeer() { return peer; }
}
