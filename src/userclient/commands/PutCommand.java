package userclient.commands;

import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import hashing.BytesAsHexPrinter;
import hashing.BytesHasher;
import hashing.SHA256MerkleBytesHasher;
import io.local.FileAccess;
import userclient.UserInteraction;

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

        FileSystemIndex index = new FileSystemIndex(files);

        String name = fileName;
        int size = bytes.length;
        String hashString = BytesAsHexPrinter.toString(hash);
        String peerId = "unknown"; // TODO: Send file to peer and set peerId on the file entry

        index.add(new FileSystemEntry(name, size, hashString, peerId));

        user.say("File was put on the system");
    }
}
