package peers;

import peers.xml.XmlPeer;

public final class PeerMapper {
    public static byte[] asBytes(PeerInfo peer) {
        return asString(peer).getBytes();
    }

    public static PeerInfo fromBytes(byte[] bytes){
        return fromString(new String(bytes));
    }

    public static String asString(PeerInfo peer){
        String ipAddress = peer.getIpAddress();
        int port = peer.getPort();

        return String.format("%s|%d", ipAddress, port);
    }

    public static PeerInfo fromString(String peerString){
        String[] parts = peerString.split("\\|");
        String ipAddress = parts[0];
        int vaultPort = Integer.parseInt(parts[1]);

        return new PeerInfo(ipAddress, vaultPort);
    }

    public static XmlPeer asXml(PeerInfo peer){
        XmlPeer xmlPeer = new XmlPeer();

        xmlPeer.setIpAddress(peer.getIpAddress());
        xmlPeer.setPort(peer.getPort());

        return xmlPeer;
    }

    public static PeerInfo fromXml(XmlPeer xmlPeer){
        String ipAddress = xmlPeer.getIpAddress();
        int port = xmlPeer.getPort();

        return new PeerInfo(ipAddress, port);
    }
}
