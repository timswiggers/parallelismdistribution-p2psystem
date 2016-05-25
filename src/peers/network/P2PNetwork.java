package peers.network;

import discovery.DiscoveryClient;
import filesystem.FileSystemIndex;
import io.local.FileAccess;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.selector.LeastAmountOfFilesSelector;
import peers.selector.PeerSelector;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class P2PNetwork {
    private final PeerIndex peerIndex;
    private final PeerSelector peerSelector;
    private final DiscoveryClient discoveryClient;

    public P2PNetwork(FileAccess fileAccess, DiscoveryClient discoveryClient) {
        this.peerIndex = new PeerIndex(fileAccess);
        this.discoveryClient = discoveryClient;
        this.peerSelector = new LeastAmountOfFilesSelector(this);
    }

    public void connect(){
        try{
            connectSafe();
            runPeriodicHealthChecks();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void connectSafe() throws IOException {

        Collection<PeerInfo> peers = peerIndex.list();
        Collection<PeerInfo> healthyPeers = doHealthCheckOn(peers);

        // TODO:
        // 1 Load in known peers
        // 2 Determine if the network is available enough
        // - Do a health check on known peers
        // - If the network is not healthy enough, contact the discovery service for more peers
        resupplyPeers();
    }

    private Collection<PeerInfo> doHealthCheckOn(Collection<PeerInfo> peers) {
        
    }

    private void runPeriodicHealthChecks() {

    }

    private void resupplyPeers() {
        try {
            discoveryClient.joinPeers();
        } catch (IOException e) {
            System.out.println("Could not connect to discovery service");
        }
    }

    public Optional<PeerInfo> givePeerForFilePut() {
        return peerSelector.select();
    }

    public Collection<PeerInfo> listPeers(){
        return peerIndex.list();
    }
}
