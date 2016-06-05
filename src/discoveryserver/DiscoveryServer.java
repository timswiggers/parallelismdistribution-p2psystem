package discoveryserver;

import common.Server;
import peers.PeerInfo;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryServer extends Server {
    private final ConcurrentLinkedQueue<PeerInfo> peersWaitingForPeersQueue;
    private final CopyOnWriteArrayList<PeerInfo> peers;

    public DiscoveryServer(int port) {
        super(port);

        this.peersWaitingForPeersQueue = new ConcurrentLinkedQueue<>();
        this.peers = new CopyOnWriteArrayList<>();
    }

    @Override
    protected RequestHandler createRequestHandler(Socket socket) {
        return new RequestHandler(socket, peers, peersWaitingForPeersQueue);
    }
}
