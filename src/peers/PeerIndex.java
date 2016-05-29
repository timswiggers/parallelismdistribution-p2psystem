package peers;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import filesystem.FileSystemEntry;
import filesystem.xml.FileSystemEntriesRepository;
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

    public PeerIndex(FileAccess files)  {
        this.files = files;
        this.peers = new HashSet<>();

        try {
            this.peers.addAll(getPeers());
        } catch(IOException | JAXBException e){
            e.printStackTrace();
        }
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

    public void add(PeerInfo peer) throws IOException, JAXBException {
        // TODO: What if the file already exists?
        peers.add(peer);
        saveChanges();
    }

    private void saveChanges() throws IOException, JAXBException {
        ByteOutputStream xmlStream = new ByteOutputStream();
        PeersRepository.write(peers, xmlStream);

        // fix: JAXB appends null bytes at the end
        String asString = new String(xmlStream.getBytes());
        asString = asString.trim();

        files.saveFileBytes(fileName, asString.getBytes());
    }
}
