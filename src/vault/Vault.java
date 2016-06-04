package vault;

import io.local.FileAccess;
import io.local.LocalFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Vault {
    private final Path vaultRoot;

    public Vault(Path vaultRoot) {

        this.vaultRoot = vaultRoot;
    }

    public void store(String peerName, String key, byte[] data) throws IOException {
        FileAccess peerFiles = getPeerFiles(peerName);

        if(peerFiles.exists(key)) {
            return; // File always exists, this is an idempotent action.
        }

        peerFiles.saveFileBytes(key, data);
    }

    public byte[] load(String peerName, String key) throws IOException {
        FileAccess peerFiles = getPeerFiles(peerName);

        if(!peerFiles.exists(key)) {
            throw new RuntimeException(String.format("No file named '%s' exists for peer %s", key, peerName));
        }

        return peerFiles.getFileBytes(key);
    }

    private FileAccess getPeerFiles(String peerName){
        Path peerVaultRoot = Paths.get(vaultRoot.toString(), peerName);
        return new LocalFileSystem(peerVaultRoot);
    }

}
