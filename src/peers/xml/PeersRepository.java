package peers.xml;

import peers.PeerInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PeersRepository {

    // Credits: http://howtodoinjava.com/jaxb/jaxb-exmaple-marshalling-and-unmarshalling-list-or-set-of-objects/

    public static List<PeerInfo> read(InputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlPeers.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        XmlPeers entries = (XmlPeers) jaxbUnmarshaller.unmarshal(stream);

        return entries.getPeers().stream().map(PeersRepository::mapFromXml).collect(Collectors.toList());
    }

    public static void write(Collection<PeerInfo> peers, OutputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlPeers.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        XmlPeers xmlPeers = new XmlPeers();
        xmlPeers.setPeers(peers.stream().map(PeersRepository::mapToXml).collect(Collectors.toList()));

        jaxbMarshaller.marshal(xmlPeers, stream);
    }

    private static PeerInfo mapFromXml(XmlPeer xmlPeer) {
        String id = xmlPeer.getId();
        String ipAddress = xmlPeer.getIpAddress();
        int port = xmlPeer.getVaultPort();

        return new PeerInfo(id, ipAddress, port);
    }

    private static XmlPeer mapToXml(PeerInfo peer) {
        XmlPeer xmlPeer = new XmlPeer();

        xmlPeer.setId(peer.getId());
        xmlPeer.setIpAddress(peer.getIpAddress());
        xmlPeer.setVaultPort(peer.getVaultPort());

        return xmlPeer;
    }
}
