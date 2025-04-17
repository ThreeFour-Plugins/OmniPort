package me.threefour.omniport;

import java.net.InetAddress;
import java.time.Instant;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.LocalDateTime;

/**
 * Tracks information about a client connection
 */
public class ConnectionInfo {
    private final InetAddress address;
    private final int port;
    private final Instant connectTime;
    private final String clientInfo;
    
    public ConnectionInfo(InetAddress address, int port, String clientInfo) {
        this.address = address;
        this.port = port;
        this.connectTime = Instant.now();
        this.clientInfo = clientInfo;
    }
    
    /**
     * Get the client's IP address
     */
    public InetAddress getAddress() {
        return address;
    }
    
    /**
     * Get the port the client connected to
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Get the client info string (if available)
     */
    public String getClientInfo() {
        return clientInfo != null ? clientInfo : "Unknown";
    }
    
    /**
     * Get the connection time
     */
    public Instant getConnectTime() {
        return connectTime;
    }
    
    /**
     * Get the formatted connection duration
     */
    public String getConnectionDuration() {
        Duration duration = Duration.between(connectTime, Instant.now());
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return String.format("%dm %ds", seconds / 60, seconds % 60);
        } else {
            return String.format("%dh %dm", seconds / 3600, (seconds % 3600) / 60);
        }
    }
    
    /**
     * Get the formatted connection time
     */
    public String getFormattedConnectTime() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(connectTime, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("%s (Port: %d, Connected: %s, Duration: %s)", 
            address.getHostAddress(), port, getFormattedConnectTime(), getConnectionDuration());
    }
} 