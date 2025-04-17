package me.threefour.omniport;

import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling colors and gradients
 */
public class ColorUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    
    /**
     * Formats a string with hex color codes
     * 
     * @param message the string to format
     * @return the formatted string
     */
    public static String formatHex(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group();
            matcher.appendReplacement(buffer, ChatColor.of(hex).toString());
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    /**
     * Creates a gradient string from start to end color
     * 
     * @param str the string to apply gradient to
     * @param start the starting color
     * @param end the ending color
     * @return the gradient formatted string
     */
    public static String gradient(String str, Color start, Color end) {
        StringBuilder builder = new StringBuilder();
        
        // Remove existing color codes for accurate character count
        String stripped = ChatColor.stripColor(str);
        
        if (stripped.isEmpty()) {
            return "";
        }
        
        int length = stripped.length();
        
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            
            int red = (int) (start.getRed() * (1 - ratio) + end.getRed() * ratio);
            int green = (int) (start.getGreen() * (1 - ratio) + end.getGreen() * ratio);
            int blue = (int) (start.getBlue() * (1 - ratio) + end.getBlue() * ratio);
            
            Color color = new Color(red, green, blue);
            builder.append(ChatColor.of(color).toString()).append(stripped.charAt(i));
        }
        
        return builder.toString();
    }
    
    /**
     * Creates a gradient string from start to end hex colors
     * 
     * @param str the string to apply gradient to
     * @param startHex the starting hex color code
     * @param endHex the ending hex color code
     * @return the gradient formatted string
     */
    public static String gradient(String str, String startHex, String endHex) {
        Color start = Color.decode(startHex);
        Color end = Color.decode(endHex);
        return gradient(str, start, end);
    }
    
    /**
     * Creates a rainbow effect for the given string
     * 
     * @param str the string to apply rainbow to
     * @return the rainbow formatted string
     */
    public static String rainbow(String str) {
        StringBuilder builder = new StringBuilder();
        
        // Remove existing color codes for accurate character count
        String stripped = ChatColor.stripColor(str);
        
        if (stripped.isEmpty()) {
            return "";
        }
        
        int length = stripped.length();
        
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
            builder.append(ChatColor.of(color).toString()).append(stripped.charAt(i));
        }
        
        return builder.toString();
    }
} 