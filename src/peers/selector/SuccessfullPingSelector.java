package peers.selector;

import peers.PeerInfo;
import peers.network.P2PNetwork;
import vault.VaultClient;

import java.util.Collection;
import java.util.Optional;

public class SuccessfullPingSelector implements PeerSelector {
    private final P2PNetwork p2pNetwork;

    public SuccessfullPingSelector(P2PNetwork p2PNetwork) {
        this.p2pNetwork = p2PNetwork;
    }

    public Optional<PeerInfo> select() {
        Collection<PeerInfo> peers = p2pNetwork.listPeers();
        if(peers.size() == 0) {
            return null;
        }

        return peers.stream().filter(peer -> {
            VaultClient vault = new VaultClient(peer);
            return vault.ping();
        }).findFirst();
    }
}
