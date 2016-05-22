package peers.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "peers")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPeers {

    @XmlElement(name = "peer")
    private List<XmlPeer> peers = null;

    public List<XmlPeer> getPeers() {
        return peers;
    }

    public void setPeers(List<XmlPeer> peers) {
        this.peers = peers;
    }
}
