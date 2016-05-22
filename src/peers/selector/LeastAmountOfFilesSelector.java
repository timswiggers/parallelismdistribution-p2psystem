package peers.selector;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import peers.PeerIndex;
import peers.PeerInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeastAmountOfFilesSelector implements PeerSelector {
    private final FileSystemIndex fileIndex;
    private final PeerIndex peerIndex;

    public LeastAmountOfFilesSelector(FileSystemIndex fileIndex, PeerIndex peerIndex) {
        this.fileIndex = fileIndex;
        this.peerIndex = peerIndex;
    }

    public PeerInfo select() {
        Collection<FileSystemEntry> storedFiles = fileIndex.list();
        if(storedFiles.size() == 0) {
            return peerIndex.list().stream().findFirst().get();
        }

        Map<String, List<FileSystemEntry>> filesByPeerId = storedFiles.stream().collect(Collectors.groupingBy(FileSystemEntry::getPeerId));
        Map<String, Integer> fileCountByPeerId = filesByPeerId.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((List)e.getValue()).size()));

        String peerIdWithLeastFiles = fileCountByPeerId.entrySet().stream().min((o1, o2) -> o1.getValue() - o2.getValue()).get().getKey();

        return peerIndex.get(peerIdWithLeastFiles);
    }
}
