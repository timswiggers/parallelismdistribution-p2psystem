package filesystem;

/**
 * Created by timsw on 21/05/2016.
 */
public class FileSystemEntry {

    private final String name;
    private final int size;
    private final String hash;
    private final String peerId;

    public FileSystemEntry(String name, int size, String hash, String peerId) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.peerId = peerId;
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

    public String getPeerId() { return peerId; }
}
