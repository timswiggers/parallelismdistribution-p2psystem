package filesystem.xml;

import filesystem.FileSystemEntry;
import hashing.BytesAsHex;
import peers.PeerInfo;
import peers.PeerMapper;

import javax.xml.bind.*;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class FileSystemEntriesRepository {

    // Credits: http://howtodoinjava.com/jaxb/jaxb-exmaple-marshalling-and-unmarshalling-list-or-set-of-objects/

    public static List<FileSystemEntry> read(InputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFileSystemEntries.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        XmlFileSystemEntries entries = (XmlFileSystemEntries) jaxbUnmarshaller.unmarshal(stream);

        return entries.getEntries().stream().map(FileSystemEntriesRepository::mapFromXml).collect(Collectors.toList());
    }

    public static void write(Collection<FileSystemEntry> entries, OutputStream stream) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFileSystemEntries.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        XmlFileSystemEntries xmlEntries = new XmlFileSystemEntries();
        xmlEntries.setEntries(entries.stream().map(FileSystemEntriesRepository::mapToXml).collect(Collectors.toList()));

        jaxbMarshaller.marshal(xmlEntries, stream);
    }

    private static FileSystemEntry mapFromXml(XmlFileSystemEntry xmlEntry) {
        String name = xmlEntry.getName();
        int size = xmlEntry.getSize();
        byte[] hash = BytesAsHex.toBytes(xmlEntry.getHash());
        byte[] key = BytesAsHex.toBytes(xmlEntry.getKey());
        byte[] iv = BytesAsHex.toBytes(xmlEntry.getIV());
        PeerInfo peer = PeerMapper.fromString(xmlEntry.getPeer());

        return new FileSystemEntry(name, size, hash, key, iv, peer);
    }

    private static XmlFileSystemEntry mapToXml(FileSystemEntry entry) {
        XmlFileSystemEntry xmlEntry = new XmlFileSystemEntry();

        xmlEntry.setName(entry.getName());
        xmlEntry.setSize(entry.getSize());
        xmlEntry.setHash(BytesAsHex.toString(entry.getHash()));
        xmlEntry.setKey(BytesAsHex.toString(entry.getKey()));
        xmlEntry.setIV(BytesAsHex.toString(entry.getIV()));
        xmlEntry.setPeer(PeerMapper.asString(entry.getPeer()));

        return xmlEntry;
    }
}
