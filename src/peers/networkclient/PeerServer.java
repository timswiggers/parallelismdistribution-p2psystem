package peers.networkclient;

import common.Server;
import peers.PeerIndex;
import peers.network.P2PNetwork;
import vault.Vault;

import java.net.Socket;

public class PeerServer extends Server {
    private final PeerIndex peers;
    private final Vault vault;

    private P2PNetwork network;

    public PeerServer(int port, PeerIndex peers, Vault vault) {
        super(port);

        this.peers = peers;
        this.vault = vault;
    }

    public void setNetwork(P2PNetwork network){
        this.network = network;
    }

    @Override
    protected RequestHandler createRequestHandler(Socket socket) {
        return new RequestHandler(socket, peers, vault, network);
    }
}
