package filesystem.xml;

import filesystem.FileSystemEntry;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by timsw on 21/05/2016.
 */
public final class FileSystemEntriesJAXB {

    // Credits: http://howtodoinjava.com/jaxb/jaxb-exmaple-marshalling-and-unmarshalling-list-or-set-of-objects/

    public static List<FileSystemEntry> read(InputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFileSystemEntries.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        XmlFileSystemEntries entries = (XmlFileSystemEntries) jaxbUnmarshaller.unmarshal(stream);

        return entries.getEntries().stream().map(FileSystemEntriesJAXB::mapFromXml).collect(Collectors.toList());
    }

    public static void write(List<FileSystemEntry> entries, OutputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFileSystemEntries.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        XmlFileSystemEntries xmlEntries = new XmlFileSystemEntries();
        xmlEntries.setEntries(entries.stream().map(FileSystemEntriesJAXB::mapToXml).collect(Collectors.toList()));

        jaxbMarshaller.marshal(xmlEntries, stream);
    }

    private static FileSystemEntry mapFromXml(XmlFileSystemEntry xmlEntry) {
        String name = xmlEntry.getName();
        int size = xmlEntry.getSize();
        String hash = xmlEntry.getHash();
        String peerId = xmlEntry.getPeerId();

        return new FileSystemEntry(name, size, hash, peerId);
    }

    private static XmlFileSystemEntry mapToXml(FileSystemEntry entry) {
        XmlFileSystemEntry xmlEntry = new XmlFileSystemEntry();

        xmlEntry.setName(entry.getName());
        xmlEntry.setSize(entry.getSize());
        xmlEntry.setHash(entry.getHash());
        xmlEntry.setPeerId(entry.getPeerId());

        return xmlEntry;
    }
}
