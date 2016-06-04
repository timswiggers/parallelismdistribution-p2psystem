package filesystem;

import peers.PeerInfo;

public class FileSystemEntry {

    private final String name;
    private final int size;
    private final byte[] hash;
    private final byte[] key;
    private final byte[] iv;
    private final PeerInfo peer;

    public FileSystemEntry(String name, int size, byte[] hash, byte[] key, byte[] iv, PeerInfo peer) {
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

    public byte[] getHash() {
        return hash;
    }

    public byte[] getKey() { return key; }

    public byte[] getIV() { return iv; }

    public PeerInfo getPeer() { return peer; }
}
