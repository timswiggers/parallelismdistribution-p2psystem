package userclient.commands;

import filesystem.FileSystemIndex;
import io.local.FileAccess;
import userclient.UserInteraction;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class GetCommand implements Command {

    private final FileAccess files;

    public GetCommand(FileAccess files) {
        this.files = files;
    }

    @Override
    public void execute(UserInteraction user) {
        try {
            executeGet(user);
        } catch (Exception e) {
            user.sayError(e);
        }
    }

    private void executeGet(UserInteraction user) throws IOException, JAXBException {
        String fileName = user.askForValue("filename", "383MB.exe");
        if(fileName == null) {
            return;
        }

        FileSystemIndex index = new FileSystemIndex(files);
        if(!index.contains(fileName)) {
            user.say("The file system does not contain a file named '" + fileName + "'");
            return;
        }
    }
}
