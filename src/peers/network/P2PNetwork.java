package peers.network;

import discovery.DiscoveryClient;
import io.local.FileAccess;
import userclient.UserInteraction;

import java.io.IOException;

public class P2PNetwork {
    private final FileAccess fileAccess;
    private final DiscoveryClient discoveryClient;

    public P2PNetwork(FileAccess fileAccess, DiscoveryClient discoveryClient) {
        this.fileAccess = fileAccess;
        this.discoveryClient = discoveryClient;
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

        // TODO:
        // 1 Load in known peers
        // 2 Determine if the network is available enough
        // - Do a health check on known peers
        // - If the network is not healthy enough, contact the discovery service for more peers
        resupplyPeers();
    }

    private void runPeriodicHealthChecks() {

    }

    private void resupplyPeers() {
        try {
            discoveryClient.joinPeers();
        } catch (IOException e) {
            System.out.println("Could not connect to discovery service");
            e.printStackTrace();
        }
    }
}
