package discoveryserver.server;

import common.Request;
import common.Response;
import discoveryserver.client.DiscoveryRequestType;
import discoveryserver.server.responsehandlers.PeersAvailableResponseHandler;
import discoveryserver.server.responsehandlers.ResponseHandler;
import peers.PeerInfo;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

class RequestHandler extends Thread {
    private final CopyOnWriteArrayList<PeerInfo> peers;
    private final ConcurrentLinkedQueue<PeerInfo> peersWaitingForPeersQueue;
    private final Socket socket;

    public RequestHandler(Socket socket, CopyOnWriteArrayList<PeerInfo> peers, ConcurrentLinkedQueue<PeerInfo> peersWaitingForPeersQueue) {
        super("Discovery.RequestHandler");
        this.socket = socket;
        this.peers = peers;
        this.peersWaitingForPeersQueue = peersWaitingForPeersQueue;
    }

    @Override
    public void run() {try(DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            Request request = new Request(inputStream);
            Response response = new Response(outputStream);

            routeRequest(request, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Request Router
    * */

    private void routeRequest(Request request, Response response) throws IOException {
        // Parse request
        String peerAddress = socket.getInetAddress().getHostAddress();
        int peerPort = request.getIntParameter();
        PeerInfo requester = getOrCreateRequesterPeer(peerAddress, peerPort);

        DiscoveryRequestType commandType = DiscoveryRequestType.values()[request.getIntParameter()];

        // Route request
        switch(commandType) {
            case Join: { handleJoin(response, requester); break; }
            case Leave: { handleLeave(response, requester); break; }
            case RequestPeers: { handleRequestPeers(response, requester); break; }
            default: {
                response.error("Cannot handle command: " + commandType.toString());
                break;
            }
        }
    }

    /*
    * Peer Request: Join
    * */

    private void handleJoin(Response response, PeerInfo requester) throws IOException {
        // Handle request
        if(peerAlreadyJoined(requester)){
            response.success();
            return; // don't add this peer twice
        }

        peers.add(requester);
        response.success();

        if(!peersWaitingForPeersQueue.isEmpty() && peers.size() > 3){
            respondWaitingPeers();
        }

        System.out.printf("%s | Peer joined the network: %s\n", new Date(), requester.getName());
    }

    /*
    * Peer Request: Leave
    * */

    private void handleLeave(Response response, PeerInfo requester) throws IOException {
        // Handle request
        if (requester == null) {
            response.success();
            return;
        }

        if(peers.contains(requester)){
            peers.remove(requester);
        }

        // Send response
        response.success();

        System.out.printf("%s | Peer left the network: %s\n", new Date(), requester.getName());
    }

    /*
    * Peer Request: RequestPeers
    * */

    private void handleRequestPeers(Response response, PeerInfo requester) throws IOException {
        // Handle request
        if (requester == null) {
            response.error("First you have to join the network before you can request any peers");
            return;
        }

        // If the peer failed to join, add him to the list of available peers anyway
        if(!peers.contains(requester)){
            peers.add(requester);
        }

        Collection<PeerInfo> availablePeers = peersWithout(requester);
        if(availablePeers.size() >= 3){
            response.success();

            ResponseHandler responseHandler = new PeersAvailableResponseHandler(requester, availablePeers);
            responseHandler.start();

            System.out.printf("%s | Peers requested: by %s, returned %d peers.\n", new Date(), requester.getName(), availablePeers.size());

            return;
        }

        peersWaitingForPeersQueue.add(requester);

        // Send response
        response.waiting();

        System.out.printf("%s | Peers requested: by %s but only %d available, waiting for more.\n", new Date(), requester.getName(), availablePeers.size());
    }

    private void respondWaitingPeers(){
        PeerInfo waitingPeer;
        while((waitingPeer = peersWaitingForPeersQueue.poll()) != null){
            Collection<PeerInfo> peersWithoutThisOne = peersWithout(waitingPeer);
            ResponseHandler responseHandler = new PeersAvailableResponseHandler(waitingPeer, peersWithoutThisOne);
            responseHandler.start();
        }
    }

    /*
    * Helper Methods
    * */

    private boolean peerAlreadyJoined(PeerInfo peer){
        return peers.stream().anyMatch(peer::equals);
    }

    private Collection<PeerInfo> peersWithout(PeerInfo self){
        return peers.stream().filter(p -> !p.equals(self)).collect(Collectors.toList());
    }

    private PeerInfo getOrCreateRequesterPeer(String requesterIpAddress, int requesterCommunicationPort){
        PeerInfo peer = new PeerInfo(requesterIpAddress, requesterCommunicationPort);

        Optional<PeerInfo> optionalPeer = peers.stream().filter(peer::equals).findFirst();

        if(optionalPeer == null || !optionalPeer.isPresent()){
            return peer;
        }

        return optionalPeer.get();
    }
}
