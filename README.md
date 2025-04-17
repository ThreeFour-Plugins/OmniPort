<div align="center">

# 🚪 OmniPort

**Multiple ports, one server**

[![Paper](https://img.shields.io/badge/Paper-1.21+-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.0-green.svg)](https://github.com/yourusername/OmniPort/releases)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Geyser](https://img.shields.io/badge/Geyser-Compatible-purple.svg)](https://geysermc.org/)

![OmniPort Banner](https://i.imgur.com/placeholder.png)

</div>

## ✨ Overview

OmniPort is a powerful Minecraft plugin that allows your server to listen on multiple ports simultaneously, all connecting to the same server instance. Perfect for servers with players behind restrictive firewalls or network configurations.

OmniPort forwards incoming connections from additional ports to your main server port, creating a seamless experience for all players.

## 🌟 Features

- 🔌 **Multi-Port Support**: Listen on multiple ports (25565, 25566, 25567, etc.)
- 🔄 **Real-Time Port Management**: Block/unblock ports on the fly without server restart
- 📊 **Connection Monitoring**: Track all active connections with detailed statistics
- 🛡️ **Geyser/Floodgate Integration**: Full support for Bedrock players across all ports
- 🎨 **Modern UI**: Beautiful gradient colors and intuitive command interface
- 🚀 **Performance Optimized**: Low overhead proxy implementation

## 📥 Installation

1. Download the latest version of OmniPort from [GitHub Releases](https://github.com/yourusername/OmniPort/releases)
2. Place the jar file in your server's `plugins` folder
3. Restart your server
4. Edit the `config.yml` file in the `plugins/OmniPort` folder (if needed)
5. Use `/omniport status` to verify the plugin is working correctly

## ⚙️ Configuration

OmniPort is designed to work out of the box with sensible defaults. To customize it, edit the `config.yml` file:

```yaml
# List of ports to listen on (in addition to the main server port)
# These ports will forward traffic to the main Minecraft server port
# The main server port (usually 25565) will be automatically excluded
ports:
  - 25566
  - 25567
  - 25568
  - 25569
  - 19132  # Also listen on the default Bedrock port when using Geyser

# Connection settings
connection:
  # Maximum time (in milliseconds) to wait for client data before timing out
  timeout: 30000
  # Maximum number of concurrent connections across all ports
  max-connections: 100

# Floodgate integration
floodgate:
  # Welcome message for Bedrock players (set to "" to disable)
  welcome-message: "§aWelcome to the server! You connected via Bedrock Edition using OmniPort."
  # Whether to show platform icons next to player names (requires a compatible chat plugin)
  show-platform-icons: true
```

## 🎮 Commands

OmniPort comes with a set of intuitive commands to manage your multi-port setup:

| Command | Description | Permission |
|---------|-------------|------------|
| `/omniport status` | Display server status with port info | `omniport.use` |
| `/omniport connections` | List all active connections | `omniport.use` |
| `/omniport block <port>` | Block a port temporarily | `omniport.admin` |
| `/omniport unblock <port>` | Unblock a port | `omniport.admin` |
| `/omniport reload` | Reload configuration | `omniport.admin` |

## 📱 Geyser & Floodgate Support

OmniPort works seamlessly with Geyser and Floodgate to provide a unified experience for both Java and Bedrock players:

- **Geyser**: Allows Bedrock players to connect to your Java server
- **Floodgate**: Allows Bedrock players to connect without a Java account
- **Port Flexibility**: Bedrock players can connect through any of your configured ports

When both Geyser and Floodgate are installed, OmniPort will automatically detect and integrate with them, no additional configuration needed!

## 📸 Screenshots

<div align="center">

### Server Status Command
![Status Command](https://i.imgur.com/placeholder1.png)

### Connections List
![Connections](https://i.imgur.com/placeholder2.png)

### Port Management
![Port Blocking](https://i.imgur.com/placeholder3.png)

</div>

## 🚫 Common Issues

| Issue | Solution |
|-------|----------|
| Port already in use | Make sure no other service is using your configured ports |
| Connection refused | Check your firewall settings and ensure ports are forwarded |
| Empty port list | Configure ports in config.yml or use the default ports |
| Bedrock players can't connect | Make sure Geyser and Floodgate are properly set up |

## 🔧 Developer API

OmniPort provides a simple API for other plugins to interact with:

```java
// Get the OmniPort instance
OmniPort omniPort = (OmniPort) Bukkit.getPluginManager().getPlugin("OmniPort");

// Get active ports
List<Integer> activePorts = omniPort.getActivePorts();

// Get current connections
int connectionCount = omniPort.getCurrentConnections();

// Block a port
omniPort.setPortBlocked(25566, true);
```

## 📜 License

OmniPort is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Credits

- **Developer**: Amineos
- **Contributors**: Amineos
- **Special Thanks**:
  - The Paper team for their amazing server software
  - The Geyser and Floodgate teams for their incredible cross-platform support

---

<div align="center">

Made with ❤️ for the Minecraft community

[Report a Bug](https://github.com/yourusername/OmniPort/issues) | [Request a Feature](https://github.com/yourusername/OmniPort/issues)

</div> 