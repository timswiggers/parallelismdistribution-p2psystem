package userclient.commands;

import io.local.FileAccess;

public class DefaultCommandFactory implements CommandFactory {

    private final FileAccess files;

    public DefaultCommandFactory(FileAccess files) {
        this.files = files;
    }

    @Override
    public Command CreatePutCommand() { return new PutCommand(files); }

    @Override
    public Command CreateGetCommand() { return new GetCommand(files); }

    @Override
    public Command CreateHashCommand() {
        return new HashCommand(files);
    }

    @Override
    public Command CreateListCommand()  { return new ListCommand(files); }
}
