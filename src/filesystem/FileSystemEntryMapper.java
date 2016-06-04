package filesystem;

import filesystem.xml.XmlFileSystemEntry;
import hashing.BytesAsHex;
import peers.PeerInfo;
import peers.PeerMapper;

public class FileSystemEntryMapper {
    public static FileSystemEntry fromXml(XmlFileSystemEntry xmlEntry) {
        String name = xmlEntry.getName();
        int size = xmlEntry.getSize();
        byte[] hash = BytesAsHex.toBytes(xmlEntry.getHash());
        byte[] key = BytesAsHex.toBytes(xmlEntry.getKey());
        byte[] iv = BytesAsHex.toBytes(xmlEntry.getIV());
        PeerInfo peer = PeerMapper.fromString(xmlEntry.getPeer());

        return new FileSystemEntry(name, size, hash, key, iv, peer);
    }

    public static XmlFileSystemEntry asXml(FileSystemEntry entry) {
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
