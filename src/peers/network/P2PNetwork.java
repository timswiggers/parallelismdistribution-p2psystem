package peers.network;

import discovery.DiscoveryClient;
import io.local.FileAccess;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.selector.LeastAmountOfFilesSelector;
import peers.selector.PeerSelector;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class P2PNetwork {
    private final PeerIndex peerIndex;
    private final PeerSelector peerSelector;
    private final DiscoveryClient discoveryClient;

    private NetworkState networkState;
    private Timer networkHealthCheckTimer;

    enum NetworkState{
        Disconnected,
        Connecting,
        Connected
    }

    public P2PNetwork(FileAccess fileAccess, DiscoveryClient discoveryClient) {
        this.peerIndex = new PeerIndex(fileAccess);
        this.discoveryClient = discoveryClient;
        this.peerSelector = new LeastAmountOfFilesSelector(this);
        this.networkState  = NetworkState.Disconnected;
        this.networkHealthCheckTimer = new Timer();
    }

    public void connect(){
        if (networkState != NetworkState.Disconnected) {
            return;
        }
        networkState = NetworkState.Connecting;

        try{
            connectSafe();
            runPeriodicHealthChecks();
            networkState = NetworkState.Connected;
        } catch(IOException e){
            e.printStackTrace();
            networkState = NetworkState.Disconnected;
        }
    }

    public void disconnect(){
        // TODO:
        // Close own health check socket
        networkHealthCheckTimer.purge();
        networkState = NetworkState.Disconnected;
    }

    private void connectSafe() throws IOException {

        boolean networkIsHealthy = doHealthCheck();
        if (!networkIsHealthy) {
            resupplyPeers();
        }

        // TODO:
        // 1 Load in known peers
        // 2 Determine if the network is available enough
        // - Do a health check on known peers
        // - If the network is not healthy enough, contact the discovery service for more peers
        // 3 Start own health check socket
    }

    private boolean doHealthCheck(){

        Collection<PeerInfo> peers = peerIndex.list();
        Collection<PeerInfo> healthyPeers = doHealthCheckOn(peers);

        updateIndexOnHealthCheck(peers, healthyPeers);

        return healthyPeers.size() > 5;
    }

    private Collection<PeerInfo> doHealthCheckOn(Collection<PeerInfo> peers) {
        return peers; // TODO: only return health peers
    }

    private void runPeriodicHealthChecks() {
        final long fiveMinutes = 5 * 60 * 1000;

        networkHealthCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean networkIsHealthy = doHealthCheck();
                if (!networkIsHealthy) {
                    resupplyPeers();
                }
            }
        }, fiveMinutes, fiveMinutes);
    }

    private void updateIndexOnHealthCheck(Collection<PeerInfo> originalPeers, Collection<PeerInfo> healthyPeers){

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
