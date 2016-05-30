package peers.selector;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.network.P2PNetwork;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeastAmountOfFilesSelector implements PeerSelector {
    private final P2PNetwork p2pNetwork;

    public LeastAmountOfFilesSelector(P2PNetwork p2PNetwork) {
        this.p2pNetwork = p2PNetwork;
    }

    public Optional<PeerInfo> select() {
        Collection<PeerInfo> peers = p2pNetwork.listPeers();
        if(peers.size() == 0) {
            return null;
        }

        //Collection<FileSystemEntry> storedFiles = fileIndex.list();
        //if(storedFiles.size() == 0) {
        //   return peers.stream().findFirst().get();
        //}

        //Map<String, List<FileSystemEntry>> filesByPeerId = storedFiles.stream().collect(Collectors.groupingBy(FileSystemEntry::getPeerName));
        //Map<String, Integer> fileCountByPeerId = filesByPeerId.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((List)e.getValue()).size()));

        //String peerIdWithLeastFiles = fileCountByPeerId.entrySet().stream().min((o1, o2) -> o1.getValue() - o2.getValue()).get().getKey();


        return peers.stream().findFirst();
        //return peerIndex.get(peerIdWithLeastFiles);
    }
}
