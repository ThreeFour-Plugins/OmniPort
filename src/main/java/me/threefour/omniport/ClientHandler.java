package me.threefour.omniport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles individual client connections to the OmniPort server by proxying them to the main Minecraft port
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final int port;
    private final Logger logger;
    private final OmniPort plugin;
    private final int mainServerPort;
    private final UUID connectionId;
    private static final int BUFFER_SIZE = 8192;

    public ClientHandler(Socket clientSocket, int port, OmniPort plugin, UUID connectionId) {
        this.clientSocket = clientSocket;
        this.port = port;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.mainServerPort = plugin.getMainServerPort();
        this.connectionId = connectionId;
    }

    @Override
    public void run() {
        Socket serverSocket = null;
        try {
            // Connect to the main Minecraft server
            serverSocket = new Socket("127.0.0.1", mainServerPort);
            plugin.log(plugin.INFO + "Proxying client from §b" + clientSocket.getInetAddress().getHostAddress() + 
                      "§3:§b" + port + " §3→ main server at port §b" + mainServerPort);
            
            // Get streams
            final InputStream clientIn = clientSocket.getInputStream();
            final OutputStream clientOut = clientSocket.getOutputStream();
            final InputStream serverIn = serverSocket.getInputStream();
            final OutputStream serverOut = serverSocket.getOutputStream();
            
            // Create two threads to proxy data in both directions
            Thread clientToServer = new Thread(() -> {
                try {
                    proxy(clientIn, serverOut);
                } catch (IOException e) {
                    // This is normal when a client disconnects
                    if (e.getMessage() == null || !e.getMessage().contains("Socket closed")) {
                        plugin.log(plugin.INFO + "§8Client to server proxy closed §7(" + 
                                  clientSocket.getInetAddress().getHostAddress() + ")");
                    }
                }
            });
            
            Thread serverToClient = new Thread(() -> {
                try {
                    proxy(serverIn, clientOut);
                } catch (IOException e) {
                    // This is normal when a server disconnects a client
                    if (e.getMessage() == null || !e.getMessage().contains("Socket closed")) {
                        plugin.log(plugin.INFO + "§8Server to client proxy closed §7(" + 
                                  clientSocket.getInetAddress().getHostAddress() + ")");
                    }
                }
            });
            
            // Start the proxy threads
            clientToServer.start();
            serverToClient.start();
            
            // Wait for the threads to finish
            clientToServer.join();
            serverToClient.join();
            
        } catch (IOException e) {
            plugin.log(plugin.ERROR + "Error establishing proxy connection on port §c" + port + 
                      " §8(" + e.getMessage() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            plugin.log(plugin.WARNING + "Proxy thread interrupted §8(" + e.getMessage() + ")");
        } finally {
            // Close the sockets
            closeQuietly(clientSocket);
            closeQuietly(serverSocket);
            
            // Log connection closed
            plugin.log(plugin.INFO + "§7Connection closed from §8" + 
                      clientSocket.getInetAddress().getHostAddress() + "§7:§8" + port);
            
            // Remove the connection from tracking
            plugin.removeConnection(connectionId);
        }
    }
    
    /**
     * Proxy data from input stream to output stream
     */
    private void proxy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            out.flush();
        }
    }
    
    /**
     * Close a socket quietly, ignoring any exceptions
     */
    private void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
} 