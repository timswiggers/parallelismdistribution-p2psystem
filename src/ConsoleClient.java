import discovery.DiscoveryClient;
import peers.network.P2PNetwork;
import userclient.console.CommandExecutor;
import userclient.console.ConsoleUserInteraction;
import io.local.FileAccess;
import io.local.LocalFileSystem;

import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleClient {
    public static void main(String[] args)
    {
        try {
            Path appRoot = Paths.get(System.getProperty("user.home"), "p2p");
            FileAccess localFiles = new LocalFileSystem(appRoot);

            DiscoveryClient discoveryClient = new DiscoveryClient(InetAddress.getLocalHost(), DiscoveryService.port);
            P2PNetwork network = new P2PNetwork(localFiles, discoveryClient);
            network.connect();

            // TODO: Check peers, (possibly connect to discovery service)
            // TODO: Spin up vault

            ConsoleUserInteraction user = new ConsoleUserInteraction(System.in, System.out);
            CommandExecutor commandExecutor = new CommandExecutor(user, localFiles, network);

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
