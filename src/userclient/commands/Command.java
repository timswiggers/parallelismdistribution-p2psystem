package userclient.commands;

import userclient.UserInteraction;

public interface Command {
    void execute(UserInteraction user);
}
