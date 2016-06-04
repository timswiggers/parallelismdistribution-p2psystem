package discoveryserver.responses;

import peers.PeerInfo;
import peers.PeerMapper;
import peers.networkclient.PeerRequestType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class PeersAvailableResponseHandler extends ResponseHandler {
    private final Collection<PeerInfo> availablePeers;

    public PeersAvailableResponseHandler(PeerInfo requester, Collection<PeerInfo> availablePeers) {
        super(requester);
        this.availablePeers = availablePeers;
    }

    @Override
    protected void writeResponse(DataOutputStream response) throws IOException {
        response.writeInt(PeerRequestType.AcceptPeers.ordinal());
        response.writeInt(availablePeers.size());

        availablePeers.stream().map(PeerMapper::asBytes).forEach(peer -> {
            try {
                response.writeInt(peer.length);
                response.write(peer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
