package discovery.responses;

import peers.PeerInfo;

/**
 * Created by timsw on 30/05/2016.
 */
public class ResponseHandler extends Thread {
    private final PeerInfo requester;

    public ResponseHandler(PeerInfo requester) {
        this.requester = requester;
    }

    @Override
    public void run() {

    }
}
