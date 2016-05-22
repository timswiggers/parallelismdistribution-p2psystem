package peers;

public class PeerInfo {
    private final String id;
    private final String ipAddress;
    private final int port;

    public PeerInfo(String id, String ipAddress, int port) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
