package peers.network;

import discoveryserver.DiscoveryClient;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.networkclient.CommunicationClient;
import peers.selector.SuccessfullPingSelector;
import peers.selector.PeerSelector;
import userclient.UserInteraction;
import vault.VaultClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class P2PNetwork {
    private final PeerIndex peerIndex;
    private final PeerSelector peerSelector;
    private final DiscoveryClient discoveryClient;
    private final CommunicationClient communicationClient;
    private final UserInteraction user;

    private NetworkState networkState;
    private Timer networkHealthCheckTimer;

    enum NetworkState{
        Disconnecting,
        Disconnected,
        Connecting,
        Connected
    }

    public P2PNetwork(UserInteraction user, PeerIndex peers, DiscoveryClient discoveryClient, CommunicationClient communicationClient) {
        this.user = user;
        this.peerIndex = peers;
        this.discoveryClient = discoveryClient;
        this.communicationClient = communicationClient;
        this.peerSelector = new SuccessfullPingSelector(this);
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
        if(networkState != NetworkState.Connected){
            return;
        }
        networkState = NetworkState.Disconnecting;

        try{
            disconnectSafe();
            networkState = NetworkState.Disconnected;
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            networkState = NetworkState.Connected;
        }
    }

    private void connectSafe() throws IOException {

        communicationClient.start();
        boolean successfullyJoined = discoveryClient.joinPeers();
        if(!successfullyJoined){
            throw new RuntimeException("Could not connect to the discovery client");
        }

        boolean networkIsHealthy = doHealthCheck();
        if (!networkIsHealthy) {
            boolean peersAvailable = discoveryClient.requestPeers();
            // TODO: notify when Wait returned and we have to wait for peers
        }
    }

    private void disconnectSafe() throws IOException, InterruptedException {
        networkHealthCheckTimer.cancel();
        networkHealthCheckTimer.purge();
        communicationClient.stopServer();
        discoveryClient.leavePeers();
        networkState = NetworkState.Disconnected;
    }

    private boolean doHealthCheck(){
        Collection<PeerInfo> peers = peerIndex.list();
        Collection<PeerInfo> healthyPeers = doHealthCheckOn(peers);

        return healthyPeers.size() >= 3;
    }

    private Collection<PeerInfo> doHealthCheckOn(Collection<PeerInfo> peers) {
        return peers.stream().filter(peer -> {
            VaultClient vault = new VaultClient(peer);
            return vault.ping();
        }).collect(Collectors.toList());
    }

    private void runPeriodicHealthChecks() {
        final long fiveMinutes = 5 * 60 * 1000;

        networkHealthCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean networkIsHealthy = doHealthCheck();
                if (!networkIsHealthy) {
                    try {
                        discoveryClient.requestPeers();
                    } catch (IOException e) { /* swallow */ }
                }
            }
        }, fiveMinutes, fiveMinutes);
    }

    public Optional<PeerInfo> givePeerForFilePut() throws IOException {
        Optional<PeerInfo> peer = peerSelector.select();
        if(peer == null || !peer.isPresent()) {
            discoveryClient.requestPeers();
        }
        return peer;
    }

    public Collection<PeerInfo> listPeers(){
        return peerIndex.list();
    }
}
