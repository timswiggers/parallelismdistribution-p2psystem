package discovery.responses;

import peers.PeerInfo;
import peers.communication.PeerRequestType;

import java.io.PrintWriter;
import java.util.Collection;

public class PeersAvailableResponseHandler extends ResponseHandler {
    private final Collection<PeerInfo> availablePeers;

    public PeersAvailableResponseHandler(PeerInfo requester, Collection<PeerInfo> availablePeers) {
        super(requester);
        this.availablePeers = availablePeers;
    }

    @Override
    protected void writeResponse(PrintWriter response) {
        response.println(PeerRequestType.PeersAvailable.toString());
        availablePeers.stream().map(PeersAvailableResponseHandler::serializePeer).forEach(response::println);
    }

    private static String serializePeer(PeerInfo peer){
        String ipAddress = peer.getIpAddress();
        int port = peer.getPort();

        return String.format("%s|%d", ipAddress, port);
    }
}
