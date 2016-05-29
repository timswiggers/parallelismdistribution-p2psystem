package peers.network;

import discovery.DiscoveryClient;
import io.local.FileAccess;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.selector.LeastAmountOfFilesSelector;
import peers.selector.PeerSelector;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;

public class P2PNetwork {
    private final PeerIndex peerIndex;
    private final PeerSelector peerSelector;
    private final DiscoveryClient discoveryClient;

    private NetworkState networkState;
    private Timer networkHealthCheckTimer;

    enum NetworkState{
        Disconnecting,
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
            //runPeriodicHealthChecks();
            networkState = NetworkState.Connected;
        } catch(IOException e){
            e.printStackTrace();
            networkState = NetworkState.Disconnected;
        }
    }

    public void disconnect(){
        if(networkState != NetworkState.Connected){
            return;
        }
        networkState = NetworkState.Disconnecting;

        try{
            disconnectSafe();
            networkState = NetworkState.Disconnected;
        } catch(IOException e){
            e.printStackTrace();
            networkState = NetworkState.Connected;
        }
    }

    private void connectSafe() throws IOException {

        discoveryClient.joinPeers();

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

    private void disconnectSafe() throws IOException {
        // TODO:
        // Close own health check socket
        networkHealthCheckTimer.purge();
        discoveryClient.leavePeers();
        networkState = NetworkState.Disconnected;
    }

    private boolean doHealthCheck(){

        Collection<PeerInfo> peers = peerIndex.list();
        Collection<PeerInfo> healthyPeers = doHealthCheckOn(peers);

        updateIndexOnHealthCheck(peers, healthyPeers);

        return healthyPeers.size() >= 3;
    }

    private Collection<PeerInfo> doHealthCheckOn(Collection<PeerInfo> peers) {
        return new ArrayList<>(); // TODO: only return health peers, for now none are healthy
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
            PeerInfo[] newPeers = discoveryClient.requestPeers();

            for(PeerInfo peer : newPeers){
                peerIndex.add(peer);
            }
        } catch (JAXBException |IOException e) {
            System.out.println("\nCould not connect to discovery server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<PeerInfo> givePeerForFilePut() {
        return peerSelector.select();
    }

    public Collection<PeerInfo> listPeers(){
        return peerIndex.list();
    }
}
