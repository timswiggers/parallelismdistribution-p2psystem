package userclient.commands;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import hashing.BytesAsHexPrinter;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.selector.LeastAmountOfFilesSelector;
import peers.selector.PeerSelector;
import userclient.UserInteraction;
import vault.remote.RemoteVault;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class PutCommand implements Command {

    private final FileAccess files;

    public PutCommand(FileAccess files) {
        this.files = files;
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
        String fileName = user.askForValue("filename", "383MB.exe");
        if(fileName == null) {
            return;
        }

        byte[] bytes = files.getFileBytes(fileName);
        if(bytes == null || bytes.length < 0) {
            user.say(String.format("File '%s' was not found.", fileName));
            return;
        }

        BytesHasher hasher = new SHA256MerkleBytesHasher(1000 * 1000, true);
        byte[] hash = hasher.hash(bytes);


        int size = bytes.length;
        String hashString = BytesAsHexPrinter.toString(hash);

        FileSystemIndex fileIndex = new FileSystemIndex(files);
        PeerIndex peerIndex = new PeerIndex(files);
        PeerSelector peerSelector = new LeastAmountOfFilesSelector(fileIndex, peerIndex);
        PeerInfo peer = peerSelector.select();

        if(peer == null) {
            // TODO: get more peers! and select again.
            peer = peerSelector.select();
        }

        RemoteVault vault = new RemoteVault(peer);
        vault.uploadFile(fileName, bytes);

        fileIndex.add(new FileSystemEntry(fileName, size, hashString, peer.getId()));

        user.say("File was put on the system");
    }
}
