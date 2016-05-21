package userclient.commands;

import io.local.FileAccess;

/**
 * Created by timsw on 08/05/2016.
 */
public class DefaultCommandFactory implements CommandFactory {

    private final FileAccess files;

    public DefaultCommandFactory(FileAccess files) {
        this.files = files;
    }

    @Override
    public Command CreateGetCommand() {
        return new GetCommand();
    }

    @Override
    public Command CreateHashCommand() {
        return new HashCommand(files);
    }

    @Override
    public Command CreateListCommand()  { return new ListCommand(files); }
}
