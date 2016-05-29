package peers.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "peer")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPeer {
    private String id;
    private String ipAddress;
    private int vaultPort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getVaultPort() {
        return vaultPort;
    }

    public void setVaultPort(int vaultPort) {
        this.vaultPort = vaultPort;
    }
}
