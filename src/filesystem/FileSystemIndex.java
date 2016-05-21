package filesystem;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import filesystem.xml.FileSystemEntriesJAXB;
import io.local.FileAccess;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by timsw on 21/05/2016.
 */
public class FileSystemIndex {
    private static final String fileName = "index.xml";
    private final List<FileSystemEntry> entries;
    private final FileAccess files;

    public FileSystemIndex(FileAccess files) throws IOException, JAXBException {
        this.files = files;

        this.entries = new ArrayList<>(getEntries());
    }

    public void add(FileSystemEntry entry) throws IOException, JAXBException {
        // TODO: What if the file already exists?
        entries.add(entry);
        saveChanges();
    }

    public List<FileSystemEntry> list() {
        return entries;
    }

    private Collection<FileSystemEntry> getEntries() throws IOException, JAXBException {
        if(!indexFileExists())
            return new ArrayList<>();

        return FileSystemEntriesJAXB.read(new FileInputStream(files.getPath(fileName)));
    }

    private void saveChanges() throws IOException, JAXBException {
        ByteOutputStream xmlStream = new ByteOutputStream();
        FileSystemEntriesJAXB.write(entries, xmlStream);

        // fix: JAXB appends null bytes at the end
        String asString = new String(xmlStream.getBytes());
        asString = asString.trim();

        files.saveFileBytes(fileName, asString.getBytes());
    }

    private boolean indexFileExists(){
        return files.exists(fileName);
    }
}
