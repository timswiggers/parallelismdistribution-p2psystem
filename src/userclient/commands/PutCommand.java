package userclient.commands;

import encryption.Encryptor;
import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;
import peers.PeerInfo;
import peers.network.P2PNetwork;
import userclient.UserInteraction;
import vault.VaultClient;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

public class PutCommand implements Command {

    private final FileAccess files;
    private final FileSystemIndex fileIndex;
    private final P2PNetwork network;

    public PutCommand(FileAccess files, FileSystemIndex fileIndex, P2PNetwork network) {
        this.files = files;
        this.fileIndex = fileIndex;
        this.network = network;
    }

    @Override
    public void execute(UserInteraction user) {
        try {
            executePut(user);
        } catch (IOException | JAXBException | NoSuchProviderException | NoSuchAlgorithmException e) {
            user.sayError(e);
        }
    }

    private void executePut(UserInteraction user) throws IOException, JAXBException, NoSuchAlgorithmException, NoSuchProviderException {
        // Ask the user what file we want to put
        String fileName = user.askForValue("filename", "..\\383MB.exe");
        if(fileName == null) {
            return;
        }

        // Ensure we did not already put the file
        String fileKey = files.getName(fileName);
        if(fileIndex.contains(fileKey)) {
            user.say("This file is already stored on the file system");
            user.say("Check which files are stored on the system by issuing the 'list' command");
            return;
        }

        user.sayPartly("Loading the file... ");
        byte[] bytes = files.getFileBytes(fileName);
        if(bytes == null || bytes.length < 0) {
            user.say(String.format("File '%s' was not found.", fileName));
            return;
        }
        int size = bytes.length;
        user.say("done!");

        user.sayPartly("Encrypting the file... ");
        byte[] key = Encryptor.generateKey();
        byte[] initVector = Encryptor.generateInitVector();
        byte[] encryptedFile = Encryptor.encrypt(key, initVector, bytes);
        user.say("done!");

        user.sayPartly("Hashing the file... ");
        BytesHasher hasher = new SHA256MerkleBytesHasher();
        byte[] hash = hasher.hash(encryptedFile);
        user.say("done!");

        user.sayPartly("Uploading the file... ");
        Optional<PeerInfo> optionalPeer = network.givePeerForFilePut();
        if(optionalPeer == null || !optionalPeer.isPresent()) {
            user.sayError("No peers are available at this time");
            // TODO: Place file info in staging area for later upload
            return;
        }

        PeerInfo peer = optionalPeer.get();
        VaultClient vault = new VaultClient(peer);
        vault.uploadFile(fileKey, encryptedFile);
        user.say("done!");

        fileIndex.add(new FileSystemEntry(fileKey, size, hash, key, initVector, peer));

        user.say("The file was put on the system");
    }
}
