package filesystem;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
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
        if(!indexFilesExists())
            return new ArrayList<>();

        byte[] xmlBytes = files.getFileBytes(fileName);
        InputStream xmlStream = new ByteInputStream(xmlBytes, xmlBytes.length);

        return FileSystemEntriesJAXB.read(xmlStream);
    }

    private void saveChanges() throws IOException, JAXBException {
        ByteOutputStream xmlStream = new ByteOutputStream();
        FileSystemEntriesJAXB.write(entries, xmlStream);

        files.saveFileBytes(fileName, xmlStream.getBytes());
    }

    private boolean indexFilesExists(){
        return files.exists(fileName);
    }
}
