package userclient.commands;

import filesystem.FileSystemIndex;
import io.local.FileAccess;
import peers.PeerInfo;
import peers.network.P2PNetwork;

public class DefaultCommandFactory implements CommandFactory {

    private final PeerInfo thisPeer;
    private final FileAccess files;
    private final FileSystemIndex fileIndex;
    private final P2PNetwork network;

    public DefaultCommandFactory(PeerInfo thisPeer, FileAccess files, FileSystemIndex fileIndex, P2PNetwork network) {
        this.thisPeer = thisPeer;
        this.files = files;
        this.fileIndex = fileIndex;
        this.network = network;
    }

    @Override
    public Command CreatePutCommand() { return new PutCommand(thisPeer, files, fileIndex, network); }

    @Override
    public Command CreateGetCommand() { return new GetCommand(thisPeer, files, fileIndex); }

    @Override
    public Command CreateHashCommand() {
        return new HashCommand(files);
    }

    @Override
    public Command CreateListCommand()  { return new ListCommand(fileIndex); }
}
