import discovery.DiscoveryClient;
import peers.network.P2PNetwork;
import userclient.console.CommandExecutor;
import userclient.console.ConsoleUserInteraction;
import io.local.FileAccess;
import io.local.LocalFileSystem;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleClient {
    public static void main(String[] args) throws IOException {

        try {
            // We use this interface to interact with the user through the console
            ConsoleUserInteraction user = new ConsoleUserInteraction(System.in, System.out);

            // We ask the user for his/her name so we can identify the P2P client to use.
            // It's possible to use multiple clients on the same system. We cannot identify clients by their
            // IP address because multiple clients could use the same one.
            String clientId = user.ask("What's your name?");
            user.say("Starting P2P Console Client");

            // Each client will work from his/her own work space within the 'p2p' workspace.
            Path appRoot = Paths.get(System.getProperty("user.home"), "p2p", clientId);
            FileAccess localFiles = new LocalFileSystem(appRoot);

            // We connect to the P2P network by registering this client with the discovery server and requesting peers.
            DiscoveryClient discoveryClient = new DiscoveryClient(clientId, InetAddress.getLocalHost(), DiscoveryService.port);
            P2PNetwork network = new P2PNetwork(localFiles, discoveryClient);
            user.sayPartly("Connecting to the network... ");
            network.connect();
            user.say("connected!\n");

            // TODO: Spin up vault

            // The command executor accepts command from the console and then executes them
            CommandExecutor commandExecutor = new CommandExecutor(user, localFiles, network);

            user.say(
                    "Parallelism and Distributed Systems - Project 2016 - P2P File System\n" +
                    "Created by Tim Swiggers (0528435)\n" +
                    "Running from " + appRoot.toString() + "\n");

            commandExecutor.executeUserCommands(); // Will loop until the user issues the quit command
            network.disconnect();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
