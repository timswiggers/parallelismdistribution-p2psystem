package userclient.commands;

import userclient.UserInteraction;

/**
 * Created by timsw on 07/05/2016.
 */
public interface Command {
    void execute(UserInteraction user);
}
