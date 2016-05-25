package peers.selector;

import peers.PeerInfo;

import java.util.Optional;

public interface PeerSelector {
    Optional<PeerInfo> select();
}
