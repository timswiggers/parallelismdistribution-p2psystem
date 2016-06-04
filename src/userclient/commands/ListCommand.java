package userclient.commands;

import hashing.BytesAsHex;
import userclient.UserInteraction;
import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand implements Command {

    private final FileSystemIndex fileIndex;

    public ListCommand(FileSystemIndex fileIndex)  {
        this.fileIndex = fileIndex;
    }

    @Override
    public void execute(UserInteraction user) {
        try {
            executeList(user);
        } catch (IOException | JAXBException e) {
            user.sayError(e);
        }
    }

    public void executeList(UserInteraction user) throws IOException, JAXBException {
        Collection<FileSystemEntry> entries = fileIndex.list();
        List<String> entriesAsStrings = entries.stream().map(ListCommand::toPrettyString).collect(Collectors.toList());

        if(entriesAsStrings.isEmpty()){
            user.say("No files are stored");
            return;
        }

        user.sayList(entriesAsStrings.toArray(new String[entriesAsStrings.size()]));
    }

    private static String toPrettyString(FileSystemEntry entry){
        String name = entry.getName();
        String size = humanReadableByteCount(entry.getSize(), true);
        String hash = BytesAsHex.toString(entry.getHash());

        return String.format("%-30s %-30s %s", name, size, hash);
    }

    // Credit: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
