package userclient.commands;

/**
 * Created by timsw on 07/05/2016.
 */
public interface CommandFactory {
    Command CreatePutCommand();
    Command CreateGetCommand();
    Command CreateListCommand();
    Command CreateHashCommand();
}
