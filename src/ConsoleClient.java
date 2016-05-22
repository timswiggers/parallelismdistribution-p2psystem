import userclient.console.CommandExecutor;
import userclient.console.ConsoleUserInteraction;
import io.local.FileAccess;
import io.local.LocalFileSystem;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleClient {
    public static void main(String[] args)
    {
        try {
            Path appRoot = Paths.get(System.getProperty("user.home"), "p2p");
            FileAccess files = new LocalFileSystem(appRoot);



            // TODO: Check peers, (possibly connect to discovery service)
            // TODO: Spin up vault

            ConsoleUserInteraction user = new ConsoleUserInteraction(System.in, System.out);
            CommandExecutor commandExecutor = new CommandExecutor(user, files);

            user.say(
                    "Parallelism and Distributed Systems - Project 2016 - P2P File System\n" +
                    "Created by Tim Swiggers (0528435)\n" +
                    "Running from " + appRoot.toString() + "\n");

            commandExecutor.executeUserCommands();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
