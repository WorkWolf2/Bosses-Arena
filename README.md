# BossesArena

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spigot](https://img.shields.io/badge/Spigot-1.18+-green.svg)](https://www.spigotmc.org/)
[![Version](https://img.shields.io/badge/Version-2.0-blue.svg)](https://builtbybit.com/resources/unmaintained-bossesarena.46646)

A powerful Minecraft plugin that allows you to create custom boss arenas with MythicMobs integration, key-based access, and comprehensive management features.

### The recode is not complete!!

## 📋 Table of Contents

- [Features](#-features)
- [Dependencies](#-dependencies)
- [Installation](#-installation)
- [Commands](#-commands)
- [Configuration](#-configuration)
- [API Integration](#-api-integration)
- [Building](#-building)
- [Support](#-support)
- [License](#-license)

## ✨ Features

- **Custom Boss Arenas**: Create unique boss fight experiences with custom regions
- **MythicMobs Integration**: Seamlessly integrate with MythicMobs for custom boss mobs
- **Key-Based Access**: Use physical keys to enter boss arenas
- **Economy Support**: Integrate with Vault for economy features
- **PlaceholderAPI Support**: Use placeholders in messages and titles
- **MMOCore Integration**: Optional integration with MMOCore for enhanced RPG features
- **Oraxen Furniture Support**: Use Oraxen Furniture as a keylock
- **MySQL Database**: Persistent data storage with MySQL support
- **GUI Management**: User-friendly interface for arena management
- **Custom Sounds**: Configurable sound effects for arena events
- **Title System**: Custom titles when entering/exiting arenas
- **Damage Tracking**: Track player damage to bosses
- **Region Management**: Define custom regions for each arena

## 🔧 Dependencies

### Required Dependencies
- **MythicMobs** - For custom boss mobs
- **Vault** - For economy integration
- **PlaceholderAPI** - For placeholder support

### Optional Dependencies
- **MMOCore** - For enhanced RPG features
- **Oraxen** - For custom furniture support

## 📦 Installation

1. **Download the plugin** from [BuiltByBit](https://builtbybit.com/resources/unmaintained-bossesarena.46646)
2. **Install required dependencies**:
   - MythicMobs
   - Vault
   - PlaceholderAPI
3. **Place the JAR file** in your server's `plugins` folder
4. **Start/restart your server**
5. **Configure the plugin** using the generated `config.yml`

## 🎮 Commands

### Player Commands
- `/b enter <arena>` - Enter a boss arena
- `/b editor` - Open the arena editor menu

### Admin Commands
- `/b create <arena>` - Create a new arena
- `/b setregion <arena>` - Set the arena region
- `/b setbosslocation <arena>` - Set the boss spawn location
- `/b setspawnlocation <arena>` - Set the player spawn location
- `/b setmythicmobid <arena> <mobId>` - Set the MythicMob ID for the boss
- `/b setkey <arena>` - Set the key item for arena access
- `/b setkeylock <arena>` - Set the key lock location
- `/b setlevel <arena> <level>` - Set the required level for the arena
- `/b setmoney <arena> <amount>` - Set the cost to enter the arena
- `/b givekey <player> <arena> [amount]` - Give a key to a player

## ⚙️ Configuration

The plugin generates a `config.yml` file with the following options:

```yaml
arena:
  # Sound when entering an arena
  enter-sound: UI_TOAST_CHALLENGE_COMPLETE
  
  # Use keys to enter boss arenas
  use-key: true
  
  # Time to exit boss room after killing boss (seconds)
  end-time: 5
  
  # Show title when entering boss room
  use-title: true
  
  # Custom end title
  end-title:
    title: ""
    subtitle: ""
```

## 🔌 API Integration

### MythicMobs
BossesArena integrates with MythicMobs to spawn custom boss mobs in arenas. Configure your MythicMobs and use the `/bsetmythicmobid` command to assign them to arenas.

### Vault Economy
The plugin supports Vault for economy integration, allowing you to set entry costs for arenas.

### PlaceholderAPI
Use PlaceholderAPI placeholders in messages and titles throughout the plugin.

### MMOCore (Optional)
When MMOCore is installed, the plugin can integrate with it for enhanced RPG features like level requirements.

### Oraxen (Optional)
Support for placing Oraxen furniture items in arenas for enhanced decoration.

## 🏗️ Building

To build the plugin from source:

1. **Clone the repository**
2. **Ensure you have Java 21+ installed**
3. **Run the build command**:
   ```bash
   ./gradlew shadowJar
   ```
4. **Find the built JAR** in `build/libs/BossesArena-2.0.jar`

### Build Requirements
- Java 21 or higher
- Gradle 8.0+
- Spigot 1.18+ API

## 📊 Metrics

This plugin uses bStats to collect anonymous usage statistics. This helps us understand how the plugin is being used and improve it. You can opt out of this in the bStats config.

## 🆘 Support

- **BuiltByBit Resource**: [BossesArena](https://builtbybit.com/resources/unmaintained-bossesarena.46646)
- **Issues**: Report bugs and request features through the BuiltByBit page
- **Documentation**: Check the plugin's built-in help commands

## 📄 License

This project is licensed under the MIT License. See the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or report issues.

## 📝 Changelog

### Version 2.0
- Complete rewrite with modern architecture
- Enhanced GUI system
- Improved database integration
- Better MythicMobs integration
- Added Oraxen furniture support
- Enhanced configuration system
- Performance improvements

---

**Note**: This plugin is currently marked as unmaintained on BuiltByBit. For the latest updates and support, please check the resource page for any announcements or community forks.
