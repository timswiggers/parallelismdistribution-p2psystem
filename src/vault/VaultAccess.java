package vault;

/**
 * Created by timsw on 07/05/2016.
 */
public interface VaultAccess {
    void store(String key, byte[] data);
    byte[] load(String key);
}
