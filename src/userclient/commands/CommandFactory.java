package userclient.commands;

/**
 * Created by timsw on 07/05/2016.
 */
public interface CommandFactory {
    Command CreateGetCommand();
    Command CreateHashCommand();
    Command CreateListCommand();
    // TODO: Add other commands
}
