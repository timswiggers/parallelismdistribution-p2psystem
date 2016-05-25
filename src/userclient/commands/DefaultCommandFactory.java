package userclient.commands;

import io.local.FileAccess;
import peers.network.P2PNetwork;

public class DefaultCommandFactory implements CommandFactory {

    private final FileAccess files;
    private final P2PNetwork network;

    public DefaultCommandFactory(FileAccess files, P2PNetwork network) {
        this.files = files;
        this.network = network;
    }

    @Override
    public Command CreatePutCommand() { return new PutCommand(files, network); }

    @Override
    public Command CreateGetCommand() { return new GetCommand(files, network); }

    @Override
    public Command CreateHashCommand() {
        return new HashCommand(files);
    }

    @Override
    public Command CreateListCommand()  { return new ListCommand(files); }
}
