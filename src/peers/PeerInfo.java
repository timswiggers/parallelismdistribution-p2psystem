package peers;

public class PeerInfo {
    private final String id;
    private final String ipAddress;
    private final int vaultPort; // The vault port

    public PeerInfo(String id, String ipAddress, int vaultPort) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.vaultPort = vaultPort;
    }

    public String getId() {
        return id;
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public int getVaultPort() {
        return vaultPort;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof PeerInfo)) return false;
        PeerInfo o = (PeerInfo) obj;
        return o.getId().equals(this.getId());
    }
}
