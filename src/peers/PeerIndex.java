package peers;

import io.local.FileAccess;
import peers.xml.PeersRepository;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class PeerIndex {
    private static final String fileName = "peers.xml";
    private final Set<PeerInfo> peers;
    private final FileAccess files;

    public PeerIndex(FileAccess files) throws IOException, JAXBException {
        this.files = files;
        this.peers = new HashSet<>(getPeers());
    }

    public Collection<PeerInfo> list() {
        return peers;
    }

    public PeerInfo get(String peerId) {
        Optional<PeerInfo> possibleEntry = peers.stream().filter(e -> e.getId().equals(peerId)).findFirst();

        if(!possibleEntry.isPresent()) {
            throw new RuntimeException("Unknown peer '" + peerId + "'");
        }

        return possibleEntry.get();
    }

    private Collection<PeerInfo> getPeers() throws IOException, JAXBException {
        if(!peersFileExists())
            return new ArrayList<>();

        return PeersRepository.read(new FileInputStream(files.getPath(fileName)));
    }

    private boolean peersFileExists(){
        return files.exists(fileName);
    }
}
