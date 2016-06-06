package peers.selector;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.network.P2PNetwork;
import vault.VaultClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeastFilesPeerSelector implements PeerSelector {
    private final PeerIndex peerIndex;
    private final FileSystemIndex fileIndex;

    public LeastFilesPeerSelector(PeerIndex peerIndex, FileSystemIndex fileIndex) {
        this.peerIndex = peerIndex;
        this.fileIndex = fileIndex;
    }

    public Optional<PeerInfo> select() {
        Collection<PeerInfo> peers = peerIndex.list();
        if(peers.size() == 0) {
            return null; // If we don't know of any peers, just return null
        }

        // List all known peers that respond to a ping
        Stream<PeerInfo> availablePeers = peers.stream().filter(peer -> {
            VaultClient vault = new VaultClient(peer);
            return vault.ping();
        });

        Collection<FileSystemEntry> storedFiles = fileIndex.list();
        if(storedFiles.size() == 0) {
            return availablePeers.findFirst(); // If we didn't store any files yet, just return the first peer
        }

        PeerInfo peerWithLeastFiles = availablePeers.collect(
                Collectors.toMap(p -> p,
                                 p -> storedFiles.stream().filter(entry -> entry.getPeer().equals(p)).count()))
                .entrySet().stream().min((o1, o2) -> (int)(o1.getValue() - o2.getValue())).get().getKey();

        return Optional.of(peerWithLeastFiles);
    }
}
