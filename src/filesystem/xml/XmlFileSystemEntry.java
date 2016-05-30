package filesystem.xml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFileSystemEntry {

    private String name;
    private int size;
    private String hash;
    private String peerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPeerId() { return peerId; }

    public void setPeerName(String peerName) { this.peerId = peerName; }
}
