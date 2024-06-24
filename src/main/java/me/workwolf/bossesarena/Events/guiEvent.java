package me.workwolf.bossesarena.Events;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.Menu.ArenasMenu;
import me.workwolf.bossesarena.Utils.Menu.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.Map;
import java.util.UUID;

public class guiEvent implements Listener {
    private final BossesArena plugin;

    private final Map<UUID, Boolean> creationMap;

    private final FileManager fileManager;

    private final ConfigManager settings;

    private final Map<UUID, String> bossMap;

    private final Map<UUID, String> SpawnLocMap;

    private final Map<UUID, String> BossLocMap;

    public guiEvent(BossesArena plugin) {
        this.plugin = plugin;

        this.fileManager = new FileManager();
        this.settings = new ConfigManager(plugin);
        ArenasMenu arenasMenu = new ArenasMenu(new MainMenu(plugin), fileManager.getItems(plugin), plugin);
        creationMap = arenasMenu.getCreationMap();
        bossMap = ArenasMenu.ArenaMenu.BossMap;
        SpawnLocMap = ArenasMenu.ArenaMenu.SpawnLocMap;
        BossLocMap = ArenasMenu.ArenaMenu.BossLocMap;
    }

    @EventHandler
    public void onArenaCreation(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!creationMap.containsKey(player.getUniqueId())) return;

        String name = event.getMessage().trim();

        if (name.equals("Exit")) {
            event.setCancelled(true);
            creationMap.clear();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            String commandLine = "ba create " + name;
            plugin.getServer().dispatchCommand(player, commandLine);
        });

        player.sendMessage(settings.CreatedArena());
        event.setCancelled(true);
        creationMap.clear();
    }

    @EventHandler
    public void onBossCreation(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!bossMap.containsKey(player.getUniqueId())) return;

        String bossName = event.getMessage().trim();
        String arenaName = bossMap.get(player.getUniqueId());

        if (bossName.equals("Exit")) {
            event.setCancelled(true);
            bossMap.clear();
            return;
        }

        fileManager.setBoss(plugin, arenaName, bossName);
        player.sendMessage(settings.getSuccesfullyMob());

        event.setCancelled(true);
        bossMap.clear();
    }

    @EventHandler
    public void onSpawnLoc(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!SpawnLocMap.containsKey(player.getUniqueId())) return;

        String message = event.getMessage().trim();
        String arenaName = SpawnLocMap.get(player.getUniqueId());

        if (message.equals("Exit")) {
            event.setCancelled(true);
            SpawnLocMap.clear();

        } else if (message.equals("location")) {

            Bukkit.getScheduler().runTask(plugin, () -> {
                String commandLine = "ba setplayerloc " + arenaName;
                plugin.getServer().dispatchCommand(player, commandLine);
            });

            event.setCancelled(true);
            SpawnLocMap.clear();
        }
    }

    @EventHandler
    public void onBossLoc(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!BossLocMap.containsKey(player.getUniqueId())) return;

        String message = event.getMessage().trim();
        String arenaName = BossLocMap.get(player.getUniqueId());

        if (message.equals("Exit")) {

            event.setCancelled(true);
            BossLocMap.clear();

        } else if (message.equals("location")) {

            Bukkit.getScheduler().runTask(plugin, () -> {
                String commandLine = "ba setbossloc " + arenaName;
                plugin.getServer().dispatchCommand(player, commandLine);
            });

            event.setCancelled(true);
            BossLocMap.clear();
        }
    }
}
