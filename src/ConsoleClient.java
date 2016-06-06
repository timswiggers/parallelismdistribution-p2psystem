import discoveryserver.client.DiscoveryClient;
import filesystem.FileSystemIndex;
import peers.PeerIndex;
import peers.PeerInfo;
import peers.networkclient.PeerServer;
import peers.network.P2PNetwork;
import userclient.UserInteraction;
import userclient.console.CommandExecutor;
import userclient.console.ConsoleUserInteraction;
import io.local.FileAccess;
import io.local.LocalFileSystem;
import vault.Vault;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleClient {
    public static void main(String[] args) throws IOException {

        try {
            // We use this interface to interact with the user through the console
            ConsoleUserInteraction user = new ConsoleUserInteraction(System.in, System.out);

            // It's possible to use multiple clients on the same system. We cannot identify clients by their
            // IP address alone because multiple clients could use the same one.
            int clientPort = determineClientPort(args, user);

            // Each client will work from his/her own work space within the 'p2p' workspace.
            Path appRoot = Paths.get(System.getProperty("user.home"), "p2p", "client" + clientPort);
            FileAccess localFiles = new LocalFileSystem(appRoot);
            FileSystemIndex fileIndex = new FileSystemIndex(localFiles);
            PeerIndex peers = new PeerIndex(localFiles);

            user.say("Starting P2P Console Client");

            Path vaultRoot = Paths.get(appRoot.toString(), "vault");
            Vault vault = new Vault(vaultRoot);

            // We use the DiscoveryClient to send requests to the discoveryserver server
            // We use the PeerServer to receive responses from the network (other peers & discoveryserver server)
            DiscoveryClient discoveryClient = new DiscoveryClient(clientPort, InetAddress.getLocalHost(), DiscoveryService.port);
            PeerServer peerServer = new PeerServer(clientPort, peers, vault);

            // We connect to the P2P network by registering this client with the discoveryserver server.
            P2PNetwork network = new P2PNetwork(user, peers, fileIndex, discoveryClient, peerServer);
            user.sayPartly("Connecting to the network... ");
            peerServer.setNetwork(network);
            network.connect();
            user.say("connected!\n");

            PeerInfo thisPeer = network.getThisPeer();

            // The command executor accepts command from the console and then executes them
            CommandExecutor commandExecutor = new CommandExecutor(user, thisPeer, localFiles, fileIndex, network);

            user.say(
                    "Parallelism and Distributed Systems - Project 2016 - P2P File System\n" +
                    "Created by Tim Swiggers (0528435)\n" +
                    "Running from " + appRoot.toString() + "\n");

            // Now we loop until the user issues the quit command
            commandExecutor.executeUserCommands();

            // The user issued the quit command
            network.disconnect();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static int determineClientPort(String[] args, UserInteraction user) throws IOException {
        if(args.length > 1) {
            return Integer.parseInt(args[1]);
        }

        // We ask the user for the port the client should listen on, so we can identify the P2P client to use.
        String portString = user.ask("What port do you want to run on (random)?");
        if(portString != null && !portString.isEmpty()){
            user.newLine();
            return Integer.parseInt(portString);
        }

        // If the user did not supply a port, we select one randomly
        // Chances are this clashes with an existing client, but for demo purposes, we take that chance
        try(ServerSocket s = new ServerSocket(0)){
            return s.getLocalPort();
        }
    }
}
