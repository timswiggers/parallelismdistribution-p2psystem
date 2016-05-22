package vault;

public interface VaultAccess {
    void store(String key, byte[] data);
    byte[] load(String key);
}
