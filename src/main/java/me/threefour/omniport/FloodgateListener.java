package me.threefour.omniport;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import java.util.logging.Logger;

/**
 * Listener for Floodgate events to handle Bedrock players
 * This class is loaded dynamically only when Floodgate is available
 */
public class FloodgateListener implements Listener {
    private final OmniPort plugin;
    private final Logger logger;
    private Object floodgateApi; // Using Object to avoid direct dependency
    private String welcomeMessage;

    public FloodgateListener(OmniPort plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.welcomeMessage = plugin.getConfig().getString("floodgate.welcome-message", 
                            "§aWelcome to the server! You are connected via Bedrock Edition.");
        
        try {
            // Dynamically get the FloodgateApi class and instance
            Class<?> floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            this.floodgateApi = floodgateApiClass.getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            logger.warning("Failed to initialize FloodgateApi: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (floodgateApi == null) return;
        
        Player player = event.getPlayer();
        try {
            // Dynamically invoke the isFloodgatePlayer method
            boolean isBedrockPlayer = (boolean) floodgateApi.getClass()
                .getMethod("isFloodgatePlayer", java.util.UUID.class)
                .invoke(floodgateApi, player.getUniqueId());
                
            if (isBedrockPlayer) {
                // Log the connection with the port information if available
                String joinMessage = "Bedrock player " + player.getName() + " joined through Floodgate!";
                if (player.getAddress() != null) {
                    joinMessage += " (Connected to port: " + player.getAddress().getPort() + ")";
                }
                logger.info(joinMessage);
                
                // Send welcome message if configured
                if (welcomeMessage != null && !welcomeMessage.isEmpty()) {
                    player.sendMessage(welcomeMessage);
                }
            }
        } catch (Exception e) {
            logger.warning("Error checking if player is from Bedrock: " + e.getMessage());
        }
    }
} 