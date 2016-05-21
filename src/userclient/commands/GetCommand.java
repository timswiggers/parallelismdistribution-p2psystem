package userclient.commands;

import userclient.UserInteraction;

/**
 * Created by timsw on 07/05/2016.
 */
public class GetCommand implements Command {

    @Override
    public void execute(UserInteraction user) {
        try {
            executeGet(user);
        } catch (Exception e) {

        }
    }

    private void executeGet(UserInteraction user) {

    }
}
