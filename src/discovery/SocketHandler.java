package discovery;

import peers.PeerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SocketHandler extends Thread {
    private final CopyOnWriteArrayList<PeerInfo> peers;
    private final ConcurrentLinkedQueue<SocketHandler> peersRequestsQueue;
    private final Socket socket;

    private PeerInfo requester;
    private BufferedReader requestStream;
    private PrintWriter responseWriter;

    public SocketHandler(Socket socket, CopyOnWriteArrayList<PeerInfo> peers, ConcurrentLinkedQueue<SocketHandler> peersRequestsQueue) throws IOException {
        this.socket = socket;
        this.peers = peers;
        this.peersRequestsQueue = peersRequestsQueue;
    }

    @Override
    public void run() {
        try {
            this.requestStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.responseWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            handleRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Client Request Router
    * */

    private void handleRequest() throws IOException {
        String peerId = requestStream.readLine();
        DiscoveryCommandType commandType = DiscoveryCommandType.valueOf(requestStream.readLine());

        switch(commandType) {
            case Join: {
                handleJoin(peerId);
                break;
            }
            case Leave: {
                requester = getRequesterPeer(peerId);
                handleLeave();
                break;
            }
            case RequestPeers: {
                requester = getRequesterPeer(peerId);
                handleRequestPeers();
                break;
            }
            default: {
                responseError("Cannot handle command: " + commandType.toString());
                break;
            }
        }
    }

    /*
    * Client Request: Join
    * */

    private void handleJoin(String peerId){
        PeerInfo peer = createPeerFromRequester(peerId, socket);

        if(peerAlreadyJoined(peer)){
            responseSuccess();
            return; // don't add this peer twice
        }

        peers.add(peer);

        System.out.printf("%s | Peer joined the network: %s\n", new Date(), peer.getId());

        responseSuccess();
    }

    /*
    * Client Request: Leave
    * */

    private void handleLeave(){
        if (requester == null) {
            responseSuccess();
            return;
        }

        if(peers.contains(requester)){
            peers.remove(requester);
        }

        System.out.printf("%s | Peer left the network: %s\n", new Date(), requester.getId());

        responseSuccess();
    }

    /*
    * Client Request: RequestPeers
    * */

    private void handleRequestPeers() {
        if (requester == null) {
            responseError("First you the network before you can request any peers");
            return;
        }

        Collection<PeerInfo> responsePeers = peersWithout(requester);

        if(responsePeers.size() >= 3) {
            responseWithPeers(responsePeers);
            System.out.printf("%s | Peers requested: by %s, returned %d peers.\n", new Date(), requester.getId(), responsePeers.size());

            if(!peersRequestsQueue.isEmpty()){
                SocketHandler handlerFromQueue;
                while((handlerFromQueue = peersRequestsQueue.poll()) != null){
                    handlerFromQueue.resumeRequestPeers();
                }
            }
        } else {
            peersRequestsQueue.add(this);
            System.out.printf("%s | Peers requested: by %s but only %d available, waiting for more.\n", new Date(), requester.getId(), responsePeers.size());
        }
    }

    private void resumeRequestPeers(){
        Collection<PeerInfo> responsePeers = peersWithout(requester);
        responseWithPeers(responsePeers);
        System.out.printf("%s | Peers available: for %s, returned %d peers\n", new Date(), requester.getId(), responsePeers.size());
    }

    private Collection<PeerInfo> peersWithout(PeerInfo self){
        return peers.stream().filter(p -> !p.getId().equals(self.getId())).collect(Collectors.toList());
    }

    /*
    * Responses
    * */

    private void responseWithPeers(Collection<PeerInfo> peers){
        responseWriter.printf("%s\n", DiscoveryResponseType.Success);
        peers.stream().map(SocketHandler::serializePeer).forEach(peerString -> {
            responseWriter.printf("%s\n", peerString);
        });
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    private static String serializePeer(PeerInfo peer){
        String id = peer.getId();
        String ipAddress = peer.getIpAddress();
        int vaultPort = peer.getVaultPort();

        return String.format("%s|%s|%d", id, ipAddress, vaultPort);
    }

    private void responseSuccess(){
        responseWriter.printf("%s\n", DiscoveryResponseType.Success);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    private void responseError(String message){
        responseWriter.printf("%s\n", DiscoveryResponseType.Success);
        responseWriter.printf("%s\n", message);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    /*
    * Helper Methods
    * */

    private boolean peerAlreadyJoined(PeerInfo peer){
        return peers.stream().anyMatch(p -> peer.getId().equals(p.getId()));
    }

    private PeerInfo createPeerFromRequester(String peerId, Socket socket){
        InetAddress peerAddress = socket.getInetAddress();
        int port = socket.getPort();
        String ipAddress = peerAddress.getHostAddress();

        return new PeerInfo(peerId, ipAddress, port);
    }

    private PeerInfo getRequesterPeer(String peerId){
        Optional<PeerInfo> optionalPeer = peers.stream().filter(p -> p.getId().equals(peerId)).findFirst();

        if(optionalPeer == null || !optionalPeer.isPresent()){
            responseSuccess();
            return null;
        }

        return optionalPeer.get();
    }
}
