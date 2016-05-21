package userclient.commands;

import io.local.FileAccess;
import userclient.UserInteraction;

import java.io.IOException;

/**
 * Created by timsw on 21/05/2016.
 */
public class PutCommand implements Command {

    private final FileAccess files;

    public PutCommand(FileAccess files) {
        this.files = files;
    }


    @Override
    public void execute(UserInteraction user) {
        try {
            executePut(user);
        } catch (IOException e) {
            user.sayError(e);
        }
    }

    public void executePut(UserInteraction user) throws IOException {
        String fileName = user.askForValue("filename", "383MB.exe");
        if(fileName == null) {
            return;
        }

        byte[] bytes = files.getFileBytes(fileName);
        if(bytes == null || bytes.length < 0) {
            user.say(String.format("File '%s' was not found.", fileName));
            return;
        }


    }
}
