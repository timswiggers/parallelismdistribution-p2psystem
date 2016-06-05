package userclient.commands;

import encryption.Encryptor;
import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import hashing.BytesAsHex;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;
import peers.PeerInfo;
import userclient.UserInteraction;
import vault.VaultClient;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GetCommand implements Command {

    private final FileAccess files;
    private final FileSystemIndex fileIndex;

    public GetCommand(FileAccess files, FileSystemIndex fileIndex) {
        this.files = files;
        this.fileIndex = fileIndex;
    }

    @Override
    public void execute(UserInteraction user) {
        try {
            executeGet(user);
        } catch (Exception e) {
            user.sayError(e);
        }
    }

    private void executeGet(UserInteraction user) throws IOException, JAXBException, NoSuchAlgorithmException {
        String fileName = user.askForValue("filename", "383MB.exe");
        if(fileName == null) {
            return;
        }

        if(!fileIndex.contains(fileName)) {
            user.sayError("The file system does not contain a file named '" + fileName + "'");
            return;
        }

        // Search the file in the list of files we stored
        FileSystemEntry fileEntry = fileIndex.get(fileName);
        PeerInfo peerInfo = fileEntry.getPeer();

        VaultClient vault = new VaultClient(peerInfo);
        boolean couldConnect = vault.ping();
        if(!couldConnect){
            user.sayError("Could not connect to peer owner of the file");
            user.say("Please try again later");
            return;
        }

        user.sayPartly("Downloading file... ");
        byte[] encryptedBytes = vault.downloadFile(fileEntry.getName());
        if(encryptedBytes == null){
            user.sayError("The peer owner of the file could not locate the file");
            return;
        }
        user.say("done!");

        user.sayPartly("Comparing hash... ");
        BytesHasher hasher = new SHA256MerkleBytesHasher(1000 * 1000, true);
        byte[] downloadedFileHash = hasher.hash(encryptedBytes);
        byte[] originalHash = fileEntry.getHash();

        boolean hashOK = Arrays.equals(originalHash, downloadedFileHash);
        if(!hashOK) {
            user.sayError("The downloaded file has been tampered with!");
            user.say(String.format("Original hash: %s", BytesAsHex.toString(originalHash)));
            user.say(String.format("Download hash: %s", BytesAsHex.toString(downloadedFileHash)));
            return;
        }
        user.say("ok!");

        user.sayPartly("Decrypting the file... ");
        byte[] key = fileEntry.getKey();
        byte[] initVector = fileEntry.getIV();
        byte[] decryptedBytes = Encryptor.decrypt(key, initVector, encryptedBytes);
        user.say("done!");

        user.sayPartly("Saving the file... ");
        files.saveFileBytes(fileEntry.getName(), decryptedBytes);
        user.say("done!");

        user.say("The file was successfully downloaded");
    }
}
