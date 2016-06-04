package userclient.commands;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import hashing.BytesAsHex;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;
import peers.PeerInfo;
import peers.network.P2PNetwork;
import userclient.UserInteraction;
import vault.remote.RemoteVault;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
        } catch (IOException | JAXBException | NoSuchAlgorithmException e) {
            user.sayError(e);
        }
    }

    private void executePut(UserInteraction user) throws IOException, JAXBException, NoSuchAlgorithmException {
        String fileName = user.askForValue("filename", "..\\383MB.exe");
        if(fileName == null) {
            return;
        }

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
        user.say("done!");

        user.sayPartly("Hashing the file... ");
        BytesHasher hasher = new SHA256MerkleBytesHasher(1000 * 1000, true);
        byte[] hash = hasher.hash(bytes);

        int size = bytes.length;
        String hashString = BytesAsHex.toString(hash);
        user.say("done!");

        user.sayPartly("Uploading the file... ");
        Optional<PeerInfo> optionalPeer = network.givePeerForFilePut();
        if(optionalPeer == null || !optionalPeer.isPresent()) {
            user.sayError("No peers are available at this time");
            // TODO: Place file info in staging area for later upload
            return;
        }

        PeerInfo peer = optionalPeer.get();
        RemoteVault vault = new RemoteVault(peer);
        vault.uploadFile(fileKey, bytes);
        user.say("done!");

        fileIndex.add(new FileSystemEntry(fileKey, size, hashString, "", "", peer));

        user.say("The file was put on the system");
    }
}
