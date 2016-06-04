package filesystem.xml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFileSystemEntry {

    private String name;
    private int size;
    private String hash;
    private String key;
    private String iv;
    private String peer;

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

    public String getKey() { return key; }

    public String getIV() { return iv; }

    public void setIV(String key) { this.iv = iv; }

    public void setKey(String key) { this.key = key; }

    public String getPeer() { return peer; }

    public void setPeer(String peer) { this.peer = peer; }
}
