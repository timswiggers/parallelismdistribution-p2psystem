package peers.xml;

import filesystem.FileSystemEntry;
import filesystem.xml.XmlFileSystemEntries;
import filesystem.xml.XmlFileSystemEntry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PeersRepository {

    // Credits: http://howtodoinjava.com/jaxb/jaxb-exmaple-marshalling-and-unmarshalling-list-or-set-of-objects/

    public static List<FileSystemEntry> read(InputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFileSystemEntries.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        XmlFileSystemEntries entries = (XmlFileSystemEntries) jaxbUnmarshaller.unmarshal(stream);

        return entries.getEntries().stream().map(PeersRepository::mapFromXml).collect(Collectors.toList());
    }

    public static void write(Collection<FileSystemEntry> entries, OutputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFileSystemEntries.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        XmlFileSystemEntries xmlEntries = new XmlFileSystemEntries();
        xmlEntries.setEntries(entries.stream().map(PeersRepository::mapToXml).collect(Collectors.toList()));

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
