package filesystem;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import filesystem.xml.FileSystemEntriesRepository;
import io.local.FileAccess;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

public class FileSystemIndex {
    private static final String fileName = "index.xml";
    private final Set<FileSystemEntry> entries;
    private final FileAccess files;

    public FileSystemIndex(FileAccess files) throws IOException, JAXBException {
        this.files = files;
        this.entries = new HashSet<>(getEntries());
    }

    public synchronized void add(FileSystemEntry entry) throws IOException, JAXBException {
        // TODO: What if the file already exists?
        entries.add(entry);
        saveChanges();
    }

    public Collection<FileSystemEntry> list() {
        return entries;
    }

    public boolean contains(String fileName) {
        return entries.stream().filter(e -> e.getName().equals(fileName)).count() == 1;
    }

    public FileSystemEntry get(String fileName) {
        Optional<FileSystemEntry> possibleEntry = entries.stream().filter(e -> e.getName().equals(fileName)).findFirst();

        if(!possibleEntry.isPresent()) {
            throw new RuntimeException("Unknown file '" + fileName + "'");
        }

        return possibleEntry.get();
    }

    private Collection<FileSystemEntry> getEntries() throws IOException, JAXBException {
        if(!indexFileExists())
            return new ArrayList<>();

        return FileSystemEntriesRepository.read(new FileInputStream(files.getPath(fileName)));
    }

    private void saveChanges() throws IOException, JAXBException {
        ByteOutputStream xmlStream = new ByteOutputStream();
        FileSystemEntriesRepository.write(entries, xmlStream);

        // fix: JAXB appends null bytes at the end
        String asString = new String(xmlStream.getBytes());
        asString = asString.trim();

        files.saveFileBytes(fileName, asString.getBytes());
    }

    private boolean indexFileExists(){
        return files.exists(fileName);
    }
}
