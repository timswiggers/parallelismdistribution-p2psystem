package filesystem.xml;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "entries")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFileSystemEntries {

    @XmlElement(name = "entry")
    private List<XmlFileSystemEntry> entries = null;

    public List<XmlFileSystemEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<XmlFileSystemEntry> entries) {
        this.entries = entries;
    }
}
