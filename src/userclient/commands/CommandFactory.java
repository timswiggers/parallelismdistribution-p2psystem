package userclient.commands;

public interface CommandFactory {
    Command CreatePutCommand();
    Command CreateGetCommand();
    Command CreateListCommand();
    Command CreateHashCommand();
}
