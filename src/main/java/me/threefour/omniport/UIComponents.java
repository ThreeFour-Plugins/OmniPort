package me.threefour.omniport;

import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.entity.Player;

/**
 * Provides modern UI components for the OmniPort plugin
 * Uses gradients and stylish formatting to create a visually appealing interface
 */
public class UIComponents {
    // Unicode symbols
    private static final String DIAMOND = "◆";
    private static final String CIRCLE = "●";
    private static final String SQUARE = "■";
    private static final String RIGHT_ARROW = "→";
    private static final String BAR = "│";
    private static final String DOUBLE_BAR = "║";
    private static final String HORIZONTAL_BAR = "─";
    private static final String CORNER_TL = "╭";
    private static final String CORNER_TR = "╮";
    private static final String CORNER_BL = "╰";
    private static final String CORNER_BR = "╯";

    // Predefined colors
    private static final ChatColor PRIMARY = ChatColor.of("#4287f5");
    private static final ChatColor SECONDARY = ChatColor.of("#42c5f5");
    private static final ChatColor HIGHLIGHT = ChatColor.of("#f542aa");
    private static final ChatColor SUCCESS = ChatColor.of("#42f57e");
    private static final ChatColor WARNING = ChatColor.of("#f5c542");
    private static final ChatColor ERROR = ChatColor.of("#f54242");
    private static final ChatColor INFO = ChatColor.of("#42a1f5");
    private static final ChatColor TEXT = ChatColor.WHITE;
    private static final ChatColor MUTED = ChatColor.of("#a0a0a0");

    private static final String PRIMARY_COLOR = "#4287f5";
    private static final String SECONDARY_COLOR = "#42c5f5";
    private static final String ACCENT_COLOR = "#f542e3";
    private static final String SUCCESS_COLOR = "#42f55a";
    private static final String ERROR_COLOR = "#f54242";
    private static final String WARNING_COLOR = "#f5a742";
    private static final String HIGHLIGHT_COLOR = "#f5f542";
    
    // Divider styles
    private static final String DIVIDER = ColorUtils.gradient("================================", "#6e7df5", "#c16ef5");
    private static final String SMALL_DIVIDER = ColorUtils.gradient("----------------", "#6e7df5", "#c16ef5");

    /**
     * Creates a bordered panel with a title
     */
    public static void sendPanel(CommandSender sender, String title, Consumer<CommandSender> contentRenderer) {
        // Top border with title
        String topBorder = CORNER_TL + HORIZONTAL_BAR.repeat(3) + " ";
        String endBorder = " " + HORIZONTAL_BAR.repeat(3) + CORNER_TR;
        
        sender.sendMessage(PRIMARY + topBorder + 
                           ColorUtils.gradient(title, Color.decode("#4287f5"), Color.decode("#cb42f5")) + 
                           PRIMARY + endBorder);
        
        // Content (with side borders)
        contentRenderer.accept(sender);
        
        // Bottom border
        sender.sendMessage(PRIMARY + CORNER_BL + HORIZONTAL_BAR.repeat(30) + CORNER_BR);
    }
    
    /**
     * Creates a modern title
     */
    public static void sendTitle(CommandSender sender, String title) {
        sender.sendMessage(DIVIDER);
        sender.sendMessage(ColorUtils.gradient("   " + title + "   ", PRIMARY_COLOR, ACCENT_COLOR));
        sender.sendMessage(DIVIDER);
    }
    
    /**
     * Creates a section header
     */
    public static void sendSectionHeader(CommandSender sender, String header) {
        sender.sendMessage("");
        sender.sendMessage(ColorUtils.gradient("● " + header + ":", SECONDARY_COLOR, ACCENT_COLOR));
        sender.sendMessage(SMALL_DIVIDER);
    }
    
    /**
     * Creates a styled key-value row
     */
    public static void sendKeyValue(CommandSender sender, String key, String value) {
        sender.sendMessage(PRIMARY + BAR + " " + 
                          SECONDARY + key + ": " + 
                          TEXT + value);
    }
    
    /**
     * Creates a styled key-value row with custom value color
     */
    public static void sendKeyValue(CommandSender sender, String key, String value, ChatColor valueColor) {
        sender.sendMessage(PRIMARY + BAR + " " + 
                          SECONDARY + key + ": " + 
                          valueColor + value);
    }
    
    /**
     * Creates a connection entry with icon
     */
    public static void sendConnectionEntry(CommandSender sender, ConnectionInfo info, boolean isBlocked) {
        String statusIcon = isBlocked ? ERROR + CIRCLE + " " : SUCCESS + CIRCLE + " ";
        String ipAddress = info.getAddress().getHostAddress();
        String duration = info.getConnectionDuration();
        String time = info.getFormattedConnectTime();
        
        sender.sendMessage(statusIcon + 
                          MUTED + ipAddress + 
                          TEXT + " " + RIGHT_ARROW + " " + 
                          INFO + "Port " + info.getPort() + 
                          TEXT + " [" + SECONDARY + duration + TEXT + " ago, " + 
                          MUTED + time + TEXT + "]");
    }
    
    /**
     * Creates a port entry with block status
     */
    public static void sendPortEntry(CommandSender sender, int port, boolean isBlocked, int connectionCount) {
        String statusIcon = isBlocked ? ERROR + CIRCLE + " " : SUCCESS + CIRCLE + " ";
        String status = isBlocked ? ERROR + "BLOCKED" : SUCCESS + "OPEN";
        
        sender.sendMessage(statusIcon + 
                           PRIMARY + "Port " + port + 
                           TEXT + " (" + status + TEXT + ") - " + 
                           INFO + connectionCount + 
                           TEXT + " connections");
    }
    
    /**
     * Creates a command list entry
     */
    public static void sendCommandEntry(CommandSender sender, String command, String description) {
        String formattedCommand = ColorUtils.formatHex(PRIMARY_COLOR + command);
        String formattedDesc = ColorUtils.formatHex(SECONDARY_COLOR + "- " + description);
        sender.sendMessage(formattedCommand + " " + formattedDesc);
    }
    
    /**
     * Creates a success message
     */
    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(ColorUtils.formatHex(SUCCESS_COLOR + "✓ Success: " + message));
    }
    
    /**
     * Creates an error message
     */
    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(ColorUtils.formatHex(ERROR_COLOR + "✘ Error: " + message));
    }
    
    /**
     * Creates a warning message
     */
    public static void sendWarning(CommandSender sender, String message) {
        sender.sendMessage(ColorUtils.formatHex(WARNING_COLOR + "⚠ Warning: " + message));
    }
    
    /**
     * Creates a logo display
     */
    public static void sendLogo(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ColorUtils.rainbow(" ██████  ███    ███ ███    ██ ██ ██████   ██████  ██████  ████████ "));
        sender.sendMessage(ColorUtils.rainbow("██    ██ ████  ████ ████   ██ ██ ██   ██ ██    ██ ██   ██    ██    "));
        sender.sendMessage(ColorUtils.rainbow("██    ██ ██ ████ ██ ██ ██  ██ ██ ██████  ██    ██ ██████     ██    "));
        sender.sendMessage(ColorUtils.rainbow("██    ██ ██  ██  ██ ██  ██ ██ ██ ██      ██    ██ ██   ██    ██    "));
        sender.sendMessage(ColorUtils.rainbow(" ██████  ██      ██ ██   ████ ██ ██       ██████  ██   ██    ██    "));
        sender.sendMessage("");
    }
    
    /**
     * Sends an information panel with a title and list of entries
     */
    public static void sendPanel(CommandSender sender, String title, List<String> entries) {
        sender.sendMessage(SMALL_DIVIDER);
        sender.sendMessage(ColorUtils.gradient("  " + title + "  ", PRIMARY_COLOR, ACCENT_COLOR));
        sender.sendMessage(SMALL_DIVIDER);
        
        for (String entry : entries) {
            sender.sendMessage(" " + entry);
        }
        
        sender.sendMessage(SMALL_DIVIDER);
    }
    
    /**
     * Sends an actionbar message to a player
     */
    public static void sendActionBar(Player player, String message) {
        if (player != null) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                            ColorUtils.formatHex(message)));
        }
    }
} 