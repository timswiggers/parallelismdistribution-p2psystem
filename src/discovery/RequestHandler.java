package discovery;

import discovery.responses.PeersAvailableResponseHandler;
import discovery.responses.ResponseHandler;
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

    private PeerInfo requester;
    private BufferedReader requestStream;
    private PrintWriter responseWriter;

    public RequestHandler(Socket socket, CopyOnWriteArrayList<PeerInfo> peers, ConcurrentLinkedQueue<PeerInfo> peersWaitingForPeersQueue) throws IOException {
        this.socket = socket;
        this.peers = peers;
        this.peersWaitingForPeersQueue = peersWaitingForPeersQueue;
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
        String peerAddress = socket.getInetAddress().getHostAddress();
        int peerPort = Integer.parseInt(requestStream.readLine());

        requester = getOrCreateRequesterPeer(peerAddress, peerPort);
        DiscoveryRequestType commandType = DiscoveryRequestType.valueOf(requestStream.readLine());

        switch(commandType) {
            case Join: { handleJoin(); break; }
            case Leave: { handleLeave(); break; }
            case RequestPeers: { handleRequestPeers(); break; }
            default: {
                responseError("Cannot handle command: " + commandType.toString());
                break;
            }
        }
    }

    /*
    * Client Request: Join
    * */

    private void handleJoin(){
        if(peerAlreadyJoined(requester)){
            responseSuccess();
            return; // don't add this peer twice
        }

        peers.add(requester);
        responseSuccess();

        if(!peersWaitingForPeersQueue.isEmpty() && peers.size() > 3){
            respondWaitingPeers();
        }

        System.out.printf("%s | Peer joined the network: %s\n", new Date(), requester.getName());
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

        responseSuccess();

        System.out.printf("%s | Peer left the network: %s\n", new Date(), requester.getName());
    }

    /*
    * Client Request: RequestPeers
    * */

    private void handleRequestPeers() {
        if (requester == null) {
            responseError("First you have to join the network before you can request any peers");
            return;
        }

        Collection<PeerInfo> availablePeers = peersWithout(requester);
        if(availablePeers.size() >= 3){
            ResponseHandler responseHandler = new PeersAvailableResponseHandler(requester, availablePeers);
            responseHandler.start();
            responseSuccess();

            System.out.printf("%s | Peers requested: by %s, returned %d peers.\n", new Date(), requester.getName(), availablePeers.size());

            return;
        }

        peersWaitingForPeersQueue.add(requester);
        responseWait();

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

    private Collection<PeerInfo> peersWithout(PeerInfo self){
        return peers.stream().filter(p -> !p.equals(self)).collect(Collectors.toList());
    }

    private void responseSuccess(){
        responseWriter.printf("%s\n", DiscoveryResponseType.Success);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    private void responseWait(){
        responseWriter.printf("%s\n", DiscoveryResponseType.Wait);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    private void responseError(String message){
        responseWriter.printf("%s\n", DiscoveryResponseType.Error);
        responseWriter.printf("%s\n", message);
        responseWriter.printf("RESPONSE_END\n");
        responseWriter.flush();
    }

    /*
    * Helper Methods
    * */

    private boolean peerAlreadyJoined(PeerInfo peer){
        return peers.stream().anyMatch(peer::equals);
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
