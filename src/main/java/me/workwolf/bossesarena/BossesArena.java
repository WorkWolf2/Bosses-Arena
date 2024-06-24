package me.workwolf.bossesarena;

import me.workwolf.bossesarena.Commands.CommandManager;
import me.workwolf.bossesarena.Commands.TabCompleter;
import me.workwolf.bossesarena.Events.*;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Logger;
import me.workwolf.bossesarena.Utils.Placeholders.BossesArenaExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class BossesArena extends SimplePlugin {

    private final ConfigManager settings = new ConfigManager(this);
    private final int PLUGIN_ID = 21591;

    @Override
    public void onPluginStart() {
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.SUCCESS, "BOSSES ARENA LOADING...");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading Config");
        saveDefaultConfig();
        Logger.log(Logger.LogLevel.SUCCESS, "Config loaded Successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading dependencies...");
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.ERROR, "BOSSESARENA: Install MythicMobs on this server!");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        System.out.println(new BossesArenaExpansion(this).isRegistered());
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if (new BossesArenaExpansion(this).isRegistered()) {
                System.out.println(-1);
            } else {
                new BossesArenaExpansion(this).register();
            }
        }
        registerMetrics(PLUGIN_ID);
        Logger.log(Logger.LogLevel.SUCCESS, "Dependencies loaded Successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading Commands...");
        getCommand("ba").setExecutor(new CommandManager(this));
        getCommand("ba").setTabCompleter(new TabCompleter(this));
        Logger.log(Logger.LogLevel.SUCCESS, "Commands loaded successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading events...");
        getServer().getPluginManager().registerEvents(new RegionSetter(this), this);
        getServer().getPluginManager().registerEvents(new KeyLockEvent(this), this);
        getServer().getPluginManager().registerEvents(new onDeath(this), this);
        getServer().getPluginManager().registerEvents(new enterEvent(this), this);
        getServer().getPluginManager().registerEvents(new guiEvent(this), this);
        Logger.log(Logger.LogLevel.SUCCESS, "Events loaded successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading...");
        Location globalSpawn = settings.getGlobalSpawn();
        World globalSpawnWorld = globalSpawn.getWorld();
        if (globalSpawnWorld == null) {
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.ERROR, "BOSSESARENA: Set global spawn point first!");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Logger.log(Logger.LogLevel.SUCCESS, "Plugin loaded successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
    }

    @Override
    public void onPluginStop() {
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.WARNING, "BOSSESARENA: DISABLING PLUGIN...");
        new BossesArenaExpansion(this).unregister();
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
    }

    public void ReloadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void registerMetrics(int metricId) {
        try {
            new Metrics(this, metricId);
            Logger.log(Logger.LogLevel.INFO, "Bstats loaded correctly!");
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "Bstats loading failed");
            throw e;
        }
    }
}
