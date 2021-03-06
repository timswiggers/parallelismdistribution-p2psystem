package common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server extends Thread {
    private int port;
    private String ipAddress;
    private ServerSocket server;
    private boolean running;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            this.server = server;
            this.running = true;
            this.port = server.getLocalPort();
            this.ipAddress = server.getInetAddress().getHostAddress();

            while (running) {
                try {
                    Socket requestSocket = server.accept();

                    // Runs the handling of the request in a new thread, allowing other requesthandlers to be handled.
                    createRequestHandler(requestSocket).start();

                } catch (IOException e) {
                    if(!e.getMessage().contains("socket closed")){
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    protected abstract Thread createRequestHandler(Socket requestSocket);

    public void stopServer() {
        running = false;
        if (server != null &&!server.isClosed()) {
            try {
                server.close();
            } catch (IOException e) { /* swallow */ }
        }
    }
}
