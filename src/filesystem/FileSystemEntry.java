package filesystem;

public class FileSystemEntry {

    private final String name;
    private final int size;
    private final String hash;
    private final String peerName;

    public FileSystemEntry(String name, int size, String hash, String peerName) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.peerName = peerName;
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

    public String getPeerName() { return peerName; }
}
