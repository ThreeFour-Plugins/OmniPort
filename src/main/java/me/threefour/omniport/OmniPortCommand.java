package me.threefour.omniport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import java.awt.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command executor for OmniPort plugin
 */
public class OmniPortCommand implements CommandExecutor {
    private final OmniPort plugin;
    
    // Color constants for consistent styling
    private static final String HEADER_START = "#5D3FD3";  // Purple
    private static final String HEADER_END = "#00AAFF";    // Blue
    private static final String FOOTER_START = "#FFA500";  // Gold/Orange
    private static final String FOOTER_END = "#FF0000";    // Red
    private static final String COMMAND_COLOR = "#00AAFF"; // Blue
    private static final String DESC_COLOR = "#AAAAAA";    // Light Gray
    private static final String SUCCESS_COLOR = "#00FF7F"; // Green
    private static final String VALUE_COLOR = "#FFAA00";   // Orange
    
    // Unicode symbols for better UI
    private static final String BULLET = "• ";
    private static final String RIGHT_ARROW = "» ";
    private static final String CHECKMARK = "✓ ";
    private static final String X_MARK = "✗ ";

    public OmniPortCommand(OmniPort plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            // Display help information with gradient colors
            String title = "===== OmniPort Commands =====";
            sender.sendMessage(ColorUtils.gradient(title, HEADER_START, HEADER_END));
            
            sender.sendMessage(ChatColor.of(COMMAND_COLOR) + RIGHT_ARROW + "/omniport status " + 
                              ChatColor.of(DESC_COLOR) + "- Display server status");
            sender.sendMessage(ChatColor.of(COMMAND_COLOR) + RIGHT_ARROW + "/omniport connections " + 
                              ChatColor.of(DESC_COLOR) + "- List active connections");
            sender.sendMessage(ChatColor.of(COMMAND_COLOR) + RIGHT_ARROW + "/omniport block <port> " + 
                              ChatColor.of(DESC_COLOR) + "- Block a port temporarily");
            sender.sendMessage(ChatColor.of(COMMAND_COLOR) + RIGHT_ARROW + "/omniport unblock <port> " + 
                              ChatColor.of(DESC_COLOR) + "- Unblock a port");
            sender.sendMessage(ChatColor.of(COMMAND_COLOR) + RIGHT_ARROW + "/omniport reload " + 
                              ChatColor.of(DESC_COLOR) + "- Reload configuration");
            
            sender.sendMessage(ColorUtils.gradient("=============================", FOOTER_START, FOOTER_END));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "status":
                // Show the server status with detailed information and gradient colors
                displayDetailedStatus(sender);
                return true;
                
            case "connections":
                // Show detailed connection information
                displayConnections(sender);
                return true;
                
            case "block":
                // Check permissions
                if (!sender.hasPermission("omniport.admin")) {
                    sender.sendMessage(ChatColor.RED + X_MARK + "You do not have permission to use this command.");
                    return true;
                }
                
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /omniport block <port>");
                    return true;
                }
                
                try {
                    int port = Integer.parseInt(args[1]);
                    if (plugin.setPortBlocked(port, true)) {
                        sender.sendMessage(ChatColor.GREEN + CHECKMARK + "Port " + port + " is now blocked.");
                    } else {
                        sender.sendMessage(ChatColor.RED + X_MARK + "Port " + port + " is not an active OmniPort port.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + X_MARK + "Port must be a number.");
                }
                return true;
                
            case "unblock":
                // Check permissions
                if (!sender.hasPermission("omniport.admin")) {
                    sender.sendMessage(ChatColor.RED + X_MARK + "You do not have permission to use this command.");
                    return true;
                }
                
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /omniport unblock <port>");
                    return true;
                }
                
                try {
                    int port = Integer.parseInt(args[1]);
                    if (plugin.setPortBlocked(port, false)) {
                        sender.sendMessage(ChatColor.GREEN + CHECKMARK + "Port " + port + " is now unblocked.");
                    } else {
                        sender.sendMessage(ChatColor.RED + X_MARK + "Port " + port + " is not an active OmniPort port.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + X_MARK + "Port must be a number.");
                }
                return true;
            
            case "reload":
                // Check permissions
                if (!sender.hasPermission("omniport.admin")) {
                    sender.sendMessage(ChatColor.RED + X_MARK + "You do not have permission to use this command.");
                    return true;
                }
                
                // Reload the plugin config
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.of(SUCCESS_COLOR) + CHECKMARK + 
                                  ColorUtils.gradient("OmniPort configuration reloaded!", "#00FF7F", "#00AAFF"));
                return true;
                
            default:
                sender.sendMessage(ChatColor.RED + X_MARK + "Unknown command. Type /omniport for help.");
                return true;
        }
    }
    
    /**
     * Display detailed status information with gradient colors
     */
    private void displayDetailedStatus(CommandSender sender) {
        // Title with rainbow effect
        String title = "========== OmniPort Status ==========";
        sender.sendMessage(ColorUtils.gradient(title, HEADER_START, HEADER_END));
        
        // Active connections
        int connections = plugin.getCurrentConnections();
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Active connections: " + 
                          getColoredCount(connections, 50, 100));
        
        // Main server port
        int mainPort = plugin.getMainServerPort();
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Main server port: " + 
                          ChatColor.of(SUCCESS_COLOR) + mainPort);
        
        // Active ports with block status
        Map<Integer, Boolean> portStatus = plugin.getPortBlockStatus();
        
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Additional ports:");
        if (portStatus.isEmpty()) {
            sender.sendMessage("  " + ChatColor.RED + "None");
        } else {
            for (Map.Entry<Integer, Boolean> entry : portStatus.entrySet()) {
                String statusSymbol = entry.getValue() ? X_MARK : CHECKMARK;
                String statusColor = entry.getValue() ? ChatColor.RED.toString() : ChatColor.GREEN.toString();
                
                sender.sendMessage("  " + ChatColor.of("#00FFAA") + entry.getKey() + " " + 
                    statusColor + statusSymbol + (entry.getValue() ? "Blocked" : "Open"));
            }
        }
        
        // Server version
        String serverVersion = Bukkit.getVersion();
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Server version: " + 
                          ChatColor.of(VALUE_COLOR) + serverVersion);
        
        // Connection timeout
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Connection timeout: " + 
            ChatColor.of(DESC_COLOR) + plugin.getConnectionTimeout() + "ms");
            
        // Geyser/Floodgate status
        boolean geyserInstalled = Bukkit.getPluginManager().getPlugin("Geyser-Spigot") != null;
        boolean floodgateInstalled = Bukkit.getPluginManager().getPlugin("floodgate") != null;
        
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Geyser support: " + 
            (geyserInstalled ? ChatColor.GREEN + CHECKMARK + "Enabled" : ChatColor.RED + X_MARK + "Disabled"));
            
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Floodgate support: " + 
            (floodgateInstalled ? ChatColor.GREEN + CHECKMARK + "Enabled" : ChatColor.RED + X_MARK + "Disabled"));
            
        // Plugin version
        sender.sendMessage(ChatColor.of(COMMAND_COLOR) + BULLET + "Plugin version: " + 
            ChatColor.of(VALUE_COLOR) + plugin.getDescription().getVersion());
            
        // Footer
        sender.sendMessage(ColorUtils.gradient("===============================", FOOTER_START, FOOTER_END));
    }
    
    /**
     * Display detailed connection information
     */
    private void displayConnections(CommandSender sender) {
        // Title
        String title = "========== Active Connections ==========";
        sender.sendMessage(ColorUtils.gradient(title, HEADER_START, HEADER_END));
        
        // Get connections
        Map<UUID, ConnectionInfo> connections = plugin.getActiveConnections();
        
        if (connections.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No active connections.");
        } else {
            sender.sendMessage(ChatColor.of(COMMAND_COLOR) + "Total connections: " + 
                              ChatColor.of(SUCCESS_COLOR) + connections.size());
            sender.sendMessage("");
            
            // Group by port
            Map<Integer, List<ConnectionInfo>> byPort = connections.values().stream()
                .collect(Collectors.groupingBy(ConnectionInfo::getPort));
                
            for (Map.Entry<Integer, List<ConnectionInfo>> entry : byPort.entrySet()) {
                int port = entry.getKey();
                List<ConnectionInfo> portConnections = entry.getValue();
                
                // Port header with block status
                boolean blocked = plugin.isPortBlocked(port);
                String statusSymbol = blocked ? X_MARK : CHECKMARK;
                String statusColor = blocked ? ChatColor.RED.toString() : ChatColor.GREEN.toString();
                
                // Use gradient for port header
                String portHeader = "Port " + port + ": " + portConnections.size() + " connections";
                sender.sendMessage(
                    ColorUtils.gradient(portHeader, "#FFAA00", "#00FF7F") + " " +
                    statusColor + statusSymbol + (blocked ? "BLOCKED" : "OPEN")
                );
                
                // Connection details
                for (ConnectionInfo info : portConnections) {
                    sender.sendMessage(
                        "  " + ChatColor.of(DESC_COLOR) + info.getAddress().getHostAddress() + 
                        ChatColor.WHITE + " - Connected " + ChatColor.of("#AAAAFF") + info.getConnectionDuration() + 
                        ChatColor.WHITE + " ago at " + ChatColor.YELLOW + info.getFormattedConnectTime()
                    );
                }
                
                sender.sendMessage("");
            }
        }
        
        // Footer
        sender.sendMessage(ColorUtils.gradient("===============================", FOOTER_START, FOOTER_END));
    }
    
    /**
     * Get a colored representation of a count based on thresholds
     */
    private String getColoredCount(int count, int warningThreshold, int criticalThreshold) {
        if (count >= criticalThreshold) {
            return ChatColor.RED + "" + count + ChatColor.GRAY + " (High)";
        } else if (count >= warningThreshold) {
            return ChatColor.GOLD + "" + count + ChatColor.GRAY + " (Medium)";
        } else {
            return ChatColor.GREEN + "" + count + ChatColor.GRAY + " (Low)";
        }
    }
} 