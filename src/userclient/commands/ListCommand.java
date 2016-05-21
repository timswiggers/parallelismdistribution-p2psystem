package userclient.commands;

import userclient.UserInteraction;
import filesystem.FileSystemEntry;
import filesystem.FileSystemIndex;
import io.local.FileAccess;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by timsw on 21/05/2016.
 */
public class ListCommand implements Command {

    private final FileSystemIndex index;

    public ListCommand(FileAccess files) throws IOException, JAXBException {
        this.index = new FileSystemIndex(files);
    }

    @Override
    public void execute(UserInteraction user) {
        List<FileSystemEntry> entries = index.list();
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
        String hash = entry.getHash().substring(0, 8) + "...";

        return String.format("%s %s %s", name, size, hash);
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
