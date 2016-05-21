import userclient.console.CommandExecutor;
import userclient.console.ConsoleUserInteraction;
import io.local.FileAccess;
import io.local.LocalFileSystem;

public class Main {
    public static void main(String[] args)
    {
        try {
            String mainDir = System.getProperty("user.dir");
            FileAccess files = new LocalFileSystem(mainDir);

            ConsoleUserInteraction user = new ConsoleUserInteraction(System.in, System.out);
            CommandExecutor commandExecutor = new CommandExecutor(user, files);

            // TODO: Check peers, (possibly connect to discovery service)
            // TODO: Spin up vault

            user.say(
                    "Parallelism and Distributed Systems - Project 2016 - P2P File System\n" +
                    "Created by Tim Swiggers (0528435)\n" +
                    "Running from " + mainDir + "\n");
            commandExecutor.executeUserCommands();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
