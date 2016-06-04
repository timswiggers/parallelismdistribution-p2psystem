package userclient.commands;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;
import peers.PeerInfo;
import peers.PeerIndex;
import peers.network.P2PNetwork;
import userclient.UserInteraction;
import vault.remote.RemoteVault;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GetCommand implements Command {

    private final FileAccess files;
    private final FileSystemIndex fileIndex;
    private final P2PNetwork network;

    public GetCommand(FileAccess files, FileSystemIndex fileIndex, P2PNetwork network) {
        this.files = files;
        this.fileIndex = fileIndex;
        this.network = network;
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

        FileSystemEntry fileEntry = fileIndex.get(fileName);
        PeerInfo peerInfo = fileEntry.getPeer();

        RemoteVault vault = new RemoteVault(peerInfo);
        boolean couldConnect = vault.ping();
        if(!couldConnect){
            user.sayError("Could not connect to peer owner of the file");
            user.say("Please try again later");
            return;
        }

        user.say("Downloading file...");
        byte[] downloadedBytes = vault.downloadFile(fileEntry.getName());
        if(downloadedBytes == null){
            user.sayError("The peer owner of the file could not locate the file");
            return;
        }

        user.say("Comparing hash...");
        BytesHasher hasher = new SHA256MerkleBytesHasher(1000 * 1000, true);
        byte[] downloadedFileHash = hasher.hash(downloadedBytes);
        byte[] originalHash = fileEntry.getHash().getBytes();

        boolean hashOK = Arrays.equals(originalHash, downloadedFileHash);
        if(!hashOK) {
            user.sayError("The downloaded file has been tampered with!");
            user.say("Out of security reasons, we won't download the file");
            return;
        }

        files.saveFileBytes(fileEntry.getName(), downloadedBytes);

        user.say("The file was successfully downloaded");
    }
}
