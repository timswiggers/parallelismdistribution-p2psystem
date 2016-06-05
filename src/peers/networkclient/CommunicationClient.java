package peers.networkclient;

import common.Server;
import peers.PeerIndex;
import vault.Vault;

import java.net.Socket;

public class CommunicationClient extends Server {
    private final PeerIndex peers;
    private final Vault vault;

    public CommunicationClient(int port, PeerIndex peers, Vault vault) {
        super(port);

        this.peers = peers;
        this.vault = vault;
    }

    @Override
    protected RequestHandler createRequestHandler(Socket socket) {
        return new RequestHandler(socket, peers, vault);
    }
}
