package me.threefour.omniport;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.Bukkit;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;

/**
 * Main plugin class for OmniPort
 */
public final class OmniPort extends JavaPlugin {
    // UI Constants for console messages
    public static final String PREFIX = "§3[§b§lOmniPort§3] ";
    public static final String SUCCESS = "§a✓ ";
    public static final String WARNING = "§e⚠ ";
    public static final String ERROR = "§c✗ ";
    public static final String INFO = "§b→ ";
    
    private List<ServerSocket> serverSockets = new ArrayList<>();
    private List<Integer> activePorts = new ArrayList<>();
    private Map<Integer, Boolean> portBlockStatus = new ConcurrentHashMap<>();
    private Map<UUID, ConnectionInfo> activeConnections = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private boolean isRunning = false;
    private AtomicInteger currentConnections = new AtomicInteger(0);
    private int maxConnections;
    private int connectionTimeout;
    private int mainServerPort = 25565; // Default port
    
    // Default ports to listen on
    private final int[] DEFAULT_PORTS = {25566, 25567, 25568, 25569};

    @Override
    public void onEnable() {
        // Display beautiful startup banner
        logBanner();
        
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Get main server port
        mainServerPort = getServer().getPort();
        log(INFO + "Main server port detected as: §f" + mainServerPort);
        
        // Get ports from config or use defaults
        List<Integer> ports = getConfig().getIntegerList("ports");
        if (ports.isEmpty()) {
            for (int port : DEFAULT_PORTS) {
                ports.add(port);
            }
            log(WARNING + "No ports configured. Using default ports: §f" + ports);
        } else {
            log(INFO + "Using configured ports: §f" + ports);
        }
        
        // Remove main server port from the list if it's there
        ports.removeIf(port -> port == mainServerPort);
        if (ports.isEmpty()) {
            log(ERROR + "No additional ports configured after removing main server port. Plugin will be inactive.");
            return;
        }
        
        // Get connection settings
        connectionTimeout = getConfig().getInt("connection.timeout", 30000);
        maxConnections = getConfig().getInt("connection.max-connections", 100);
        log(INFO + "Connection timeout: §f" + connectionTimeout + "ms§b, Max connections: §f" + maxConnections);
        
        // Create thread pool
        executorService = Executors.newCachedThreadPool();
        
        // Initialize port block status
        for (int port : ports) {
            portBlockStatus.put(port, false);
        }
        
        // Start servers on each port
        startServers(ports);
        
        // Register commands using Paper's command registration system
        registerCommands();

        // Check if Geyser and Floodgate are installed
        boolean geyserInstalled = getServer().getPluginManager().getPlugin("Geyser-Spigot") != null;
        boolean floodgateInstalled = getServer().getPluginManager().getPlugin("floodgate") != null;
        
        if (geyserInstalled) {
            log(SUCCESS + "Detected Geyser plugin - Bedrock players can connect to your server!");
            
            if (floodgateInstalled) {
                log(SUCCESS + "Detected Floodgate plugin - Bedrock players can connect without Java accounts!");
                log(INFO + "OmniPort will support connections across all configured ports for both Java and Bedrock players.");
                
                // If Floodgate is installed, register event listener
                getServer().getPluginManager().registerEvents(new FloodgateListener(this), this);
                log(INFO + "Registered Floodgate event listener");
            } else {
                log(WARNING + "Floodgate not detected - Bedrock players will need Java accounts to connect.");
            }
        } else {
            log(WARNING + "Geyser not detected - only Java players can connect to your server.");
        }
        
        // Show success message
        log(SUCCESS + "OmniPort has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Shutdown the server
        stopServers();
        
        // Shutdown thread pool
        if (executorService != null) {
            executorService.shutdown();
        }
        
        // Clear collections
        activeConnections.clear();
        
        // Goodbye message
        log(INFO + "OmniPort has been disabled. Goodbye!");
    }
    
    /**
     * Log a message with the OmniPort prefix
     */
    public void log(String message) {
        getServer().getConsoleSender().sendMessage(PREFIX + message);
    }
    
    /**
     * Display a beautiful startup banner
     */
    private void logBanner() {
        String[] banner = {
            "§b§l ___  __  __  _  _  ___  ___   ___   ___  _____",
            "§b§l/ _ \\|  \\/  || \\| ||_ _|| _ \\ / _ \\ | _ \\|_   _|",
            "§3§l| (_) | |\\/| || .` | | | |  _/| (_) ||   /  | |",
            "§3§l \\___/|_|  |_||_|\\_||___||_|   \\___/ |_|_\\  |_|",
            " "
        };
        
        for (String line : banner) {
            getServer().getConsoleSender().sendMessage(line);
        }
        
        String version = getDescription().getVersion();
        String versionLine = "§7§o Version " + version + " - By ThreeFour";
        getServer().getConsoleSender().sendMessage(versionLine);
        getServer().getConsoleSender().sendMessage(" ");
    }
    
    /**
     * Get the main Minecraft server port
     * @return the main server port
     */
    public int getMainServerPort() {
        return mainServerPort;
    }
    
    /**
     * Get the connection timeout setting
     * @return connection timeout in milliseconds
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    /**
     * Get the list of active additional ports
     * @return list of active ports
     */
    public List<Integer> getActivePorts() {
        return new ArrayList<>(activePorts);
    }
    
    /**
     * Get all active connections with their details
     * @return map of active connections
     */
    public Map<UUID, ConnectionInfo> getActiveConnections() {
        return new HashMap<>(activeConnections);
    }
    
    /**
     * Get block status for all ports
     * @return map of port block status
     */
    public Map<Integer, Boolean> getPortBlockStatus() {
        return new HashMap<>(portBlockStatus);
    }
    
    /**
     * Block or unblock a port
     * @param port Port to block/unblock
     * @param blocked True to block, false to unblock
     * @return true if successful, false if port not found
     */
    public boolean setPortBlocked(int port, boolean blocked) {
        if (!activePorts.contains(port)) {
            return false;
        }
        
        portBlockStatus.put(port, blocked);
        log((blocked ? WARNING : SUCCESS) + "Port " + port + " is now " + (blocked ? "§c§lblocked" : "§a§lunblocked"));
        return true;
    }
    
    /**
     * Check if a port is blocked
     * @param port Port to check
     * @return true if blocked, false if not or not found
     */
    public boolean isPortBlocked(int port) {
        return portBlockStatus.getOrDefault(port, false);
    }
    
    /**
     * Register plugin commands using Paper's command system
     */
    private void registerCommands() {
        OmniPortCommand commandExecutor = new OmniPortCommand(this);
        
        // Create and register the command
        Bukkit.getCommandMap().register("omniport", new Command("omniport") {
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                return commandExecutor.onCommand(sender, this, commandLabel, args);
            }
            
            @Override
            public List<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String[] args) {
                if (args.length == 1) {
                    return List.of("status", "block", "unblock", "connections", "reload");
                } else if (args.length == 2) {
                    if ("block".equals(args[0]) || "unblock".equals(args[0])) {
                        return activePorts.stream().map(String::valueOf).collect(Collectors.toList());
                    }
                }
                return List.of();
            }
        });
        
        log(SUCCESS + "Registered omniport command");
    }
    
    /**
     * Add a client connection to tracking
     */
    public UUID addConnection(ConnectionInfo connectionInfo) {
        UUID id = UUID.randomUUID();
        activeConnections.put(id, connectionInfo);
        return id;
    }
    
    /**
     * Remove a client connection from tracking
     */
    public void removeConnection(UUID id) {
        activeConnections.remove(id);
        connectionClosed();
    }
    
    /**
     * Called when a client connection is closed
     */
    public void connectionClosed() {
        currentConnections.decrementAndGet();
    }
    
    /**
     * Get the current number of active connections
     */
    public int getCurrentConnections() {
        return currentConnections.get();
    }
    
    private void startServers(List<Integer> ports) {
        isRunning = true;
        
        for (int port : ports) {
            final int currentPort = port;
            executorService.submit(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(currentPort);
                    serverSockets.add(serverSocket);
                    activePorts.add(currentPort);
                    portBlockStatus.put(currentPort, false);
                    log(SUCCESS + "Server started on port: §f" + currentPort + "§a (forwarding to §f" + mainServerPort + "§a)");
                    
                    while (isRunning) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            
                            // Check if port is blocked
                            if (isPortBlocked(currentPort)) {
                                log(WARNING + "Rejected connection on §cblocked§e port " + currentPort + " from " + 
                                   clientSocket.getInetAddress());
                                clientSocket.close();
                                continue;
                            }
                            
                            // Check if we're at the connection limit
                            if (currentConnections.get() >= maxConnections) {
                                log(ERROR + "Connection limit reached. Rejecting connection from " + 
                                   clientSocket.getInetAddress());
                                clientSocket.close();
                                continue;
                            }
                            
                            // Set socket timeout
                            clientSocket.setSoTimeout(connectionTimeout);
                            
                            // Increment connection counter
                            currentConnections.incrementAndGet();
                            
                            // Create connection info
                            ConnectionInfo connectionInfo = new ConnectionInfo(
                                clientSocket.getInetAddress(), 
                                currentPort,
                                clientSocket.getInetAddress().getHostName()
                            );
                            
                            // Track the connection
                            UUID connectionId = addConnection(connectionInfo);
                            
                            log(INFO + "Client connected on port §f" + currentPort + "§b from §f" + 
                               clientSocket.getInetAddress() + 
                               "§b (Active connections: §f" + currentConnections.get() + "§b)");
                            
                            // Handle the client using the ClientHandler
                            executorService.submit(new ClientHandler(clientSocket, currentPort, this, connectionId));
                            
                        } catch (IOException e) {
                            if (isRunning) {
                                logWarning("Error accepting client connection on port " + currentPort, e);
                            }
                        }
                    }
                } catch (IOException e) {
                    logError("Could not start server on port " + currentPort, e);
                }
            });
        }
    }
    
    /**
     * Log a warning with the OmniPort prefix
     */
    private void logWarning(String message, Exception e) {
        log(WARNING + message + ": " + e.getMessage());
        if (getConfig().getBoolean("debug", false)) {
            getLogger().log(Level.WARNING, message, e);
        }
    }
    
    /**
     * Log an error with the OmniPort prefix
     */
    private void logError(String message, Exception e) {
        log(ERROR + message + ": " + e.getMessage());
        getLogger().log(Level.SEVERE, message, e);
    }
    
    private void stopServers() {
        isRunning = false;
        
        for (ServerSocket serverSocket : serverSockets) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logWarning("Error closing server socket", e);
            }
        }
        
        serverSockets.clear();
        activePorts.clear();
        portBlockStatus.clear();
        log(INFO + "All OmniPort servers stopped");
    }
}
