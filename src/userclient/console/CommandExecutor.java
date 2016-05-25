package userclient.console;

import peers.network.P2PNetwork;
import userclient.UserInteraction;
import userclient.commands.Command;
import userclient.commands.CommandFactory;
import userclient.commands.DefaultCommandFactory;
import io.local.FileAccess;

import java.util.HashMap;
import java.util.function.Supplier;

public class CommandExecutor {
    private final UserInteraction user;

    private final HashMap<String, Supplier<Command>> executorFactories;

    public CommandExecutor(UserInteraction user, FileAccess files, P2PNetwork network) {
        this.user = user;

        CommandFactory commandFactory = new DefaultCommandFactory(files, network);

        executorFactories = new HashMap<>();
        executorFactories.put("put", commandFactory::CreatePutCommand);
        executorFactories.put("get", commandFactory::CreateGetCommand);
        executorFactories.put("list", commandFactory::CreateListCommand);
        executorFactories.put("hash", commandFactory::CreateHashCommand);
    }

    public void executeUserCommands() {
        String commandName = null;

        user.say("Available commands: " + listAvailableCommands() + "\n");
        user.say("Press 'q' to quit.\n");

        while(!"q".equals(commandName)) {

            if(isKnownCommand(commandName = user.prompt("P2P Files").toLowerCase())) {

                Supplier<Command> commandCreator = executorFactories.get(commandName);
                Command command = commandCreator.get();

                executeSafe(command, user);

                user.newLine();
            }
        }

        user.say("\nExiting userclient.");
    }

    private boolean isKnownCommand(String command) {
        return executorFactories.containsKey(command);
    }

    private void executeSafe(Command command, UserInteraction user) {
        if(command == null) {
            return;
        }

        try {
            command.execute(user);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private String listAvailableCommands(){
        return String.join(", ", executorFactories.keySet());
    }
}
