package peers;

public class PeerInfo {
    private final String ipAddress;
    private final int port;

    public PeerInfo(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return String.format("PEER-%s-%d", ipAddress, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof PeerInfo)) return false;
        PeerInfo o = (PeerInfo) obj;
        return o.getIpAddress().equals(this.getIpAddress()) && o.getPort() == this.getPort();
    }
}
