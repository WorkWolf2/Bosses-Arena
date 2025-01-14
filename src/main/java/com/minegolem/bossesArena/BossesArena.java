package com.minegolem.bossesArena;

import co.aikar.commands.PaperCommandManager;
import com.minegolem.bossesArena.commands.*;
import com.minegolem.bossesArena.database.Database;
import com.minegolem.bossesArena.database.MysqlDb;
import com.minegolem.bossesArena.listeners.*;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.configManagers.Settings;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.menu.GUIManager;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import com.minegolem.bossesArena.utils.Logger;
import com.minegolem.bossesArena.listeners.PlayerKillBossListener;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Getter
public final class BossesArena extends JavaPlugin {

    public static BossesArena INSTANCE = null;

    private FileManager fileManager;
    private RegionManager regionManager;
    private PaperCommandManager commandManager;
    private ConfigManager configManager;
    private Settings settings;
    private GUIManager guiManager;

    private Economy econ = null;

    public static NamespacedKey keyNSK = null;
    public static NamespacedKey blockNSK = null;
    public static NamespacedKey mobNSK = null;

    public static boolean mmoCoreInstalled = false;

    private final HashMap<Arena, Boolean> closingArena = new HashMap<>();
    private final HashMap<Arena, HashMap<UUID, Double>> mobDamageMap = new HashMap<>();

    private BukkitAudiences adventure;

    private Database data;

    @Override
    public void onEnable() {
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.SUCCESS, "BOSSES ARENA LOADING...");

        INSTANCE = this;
        this.commandManager = new PaperCommandManager(this);
        this.adventure = BukkitAudiences.create(this);
        this.registerMetrics();

        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading Config");

        this.saveDefaultConfig();

        Logger.log(Logger.LogLevel.SUCCESS, "Config loaded Successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading dependencies...");
        Logger.log(Logger.LogLevel.OUTLINE, " ");

        this.guiManager = new GUIManager();

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.ERROR, "BOSSESARENA: Install MythicMobs on this server!");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEconomy()) {
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.ERROR, "BOSSESARENA: Install Vault on this server!");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.OUTLINE, "----*------------------------------------*----");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (setupOraxen()) {
            Logger.log(Logger.LogLevel.SUCCESS, "Oraxen founded. Hooked in it");
        }

        if(setupMmoCore()) {
            Logger.log(Logger.LogLevel.SUCCESS, "MMOCore founded. Hooked in it");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
        }

        if (!setupPlaceholderAPI()) {
            Logger.log(Logger.LogLevel.OUTLINE, "----*----------------------------------------*----");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.ERROR, "BOSSESARENA: Install PlaceholderAPI on this server!");
            Logger.log(Logger.LogLevel.OUTLINE, " ");
            Logger.log(Logger.LogLevel.OUTLINE, "----*----------------------------------------*----");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Logger.log(Logger.LogLevel.SUCCESS, "Dependencies loaded Successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading Commands...");

        this.commandManager.registerCommand(new BCreateCMD(this));
        this.commandManager.registerCommand(new BSetLevelCMD(this));
        this.commandManager.registerCommand(new BSetKeyCMD(this));
        this.commandManager.registerCommand(new BSetMoneyCMD(this));
        this.commandManager.registerCommand(new BSetMythicMobIdCMD(this));
        this.commandManager.registerCommand(new BSetRegionCMD(this));
        this.commandManager.registerCommand(new BSetSpawnLocation(this));
        this.commandManager.registerCommand(new BGiveKeyCMD(this));
        this.commandManager.registerCommand(new BSetKeyLockCMD(this));
        this.commandManager.registerCommand(new BSetBossLocationCMD(this));
        this.commandManager.registerCommand(new BEnterCMD(this));
        this.commandManager.registerCommand(new BEditorCMD(this));

        Logger.log(Logger.LogLevel.SUCCESS, "Commands loaded successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading listeners...");

        getServer().getPluginManager().registerEvents(new PlayerSetArenaRegionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerUseKeyListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerKillBossListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerSetLockListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this.guiManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageBossListener(this), this);

        Logger.log(Logger.LogLevel.SUCCESS, "Listeners loaded successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.INFO, "Loading...");

        keyNSK = new NamespacedKey(this, "key");
        blockNSK = new NamespacedKey(this, "block");
        mobNSK = new NamespacedKey(this, "mob");

        this.fileManager = FileManager.getInstance(this);
        this.configManager = new ConfigManager();
        this.settings = new Settings(this);
        this.regionManager = new RegionManager(this, new File(this.getDataFolder() + File.separator + "regions.yml"));

        this.fileManager.initializeFolder();
        this.registerCompletion(this.commandManager);

        Database.Type type = Database.Type.valueOf(this.settings.database_type);
        this.data = switch (type) {
            case MYSQL, MARIADB -> new MysqlDb(this);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        this.data.initialize();

        Logger.log(Logger.LogLevel.SUCCESS, "Plugin loaded successfully");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
    }

    @Override
    public void onDisable() {
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.WARNING, "BOSSESARENA: DISABLING PLUGIN...");
        Logger.log(Logger.LogLevel.INFO, "Unloading arenas...");
        this.fileManager.getArena().clear();
        Logger.log(Logger.LogLevel.INFO, "Closing database connection...");
        this.data.terminate();
        Logger.log(Logger.LogLevel.INFO, "Unloading adventure...");
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        Logger.log(Logger.LogLevel.WARNING, "BOSSESARENA: Plugin disbaled successfully...");
        Logger.log(Logger.LogLevel.OUTLINE, " ");
        Logger.log(Logger.LogLevel.OUTLINE, "----*-----------------------------------------------*----");
    }

    private void registerMetrics() {
        try {
            new Metrics(this, 21591);
            Logger.log(Logger.LogLevel.SUCCESS, "Bstats loaded correctly!");
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "Bstats loading failed", e);
        }
    }

    private void registerCompletion(PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("arenas", c -> {
            try {
                return FileManager.getArenaList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            Logger.log(Logger.LogLevel.WARNING, "If you are using CMI try to install CMIEInjector or use the recompiled version");
            Logger.log(Logger.LogLevel.WARNING, "of Vault that support CMI Economy.");
            return false;
        }

        this.econ = rsp.getProvider();

        return true;
    }

    private boolean setupOraxen() {
        if (getServer().getPluginManager().getPlugin("Oraxen") == null) {
            return false;
        }

        getServer().getPluginManager().registerEvents(new OraxenFurniturePlaceListener(this), this);
        return true;
    }

    private boolean setupMmoCore() {
        if (getServer().getPluginManager().getPlugin("MMOCore") == null) {
            return false;
        }

        mmoCoreInstalled = true;

        return true;
    }

    private boolean setupPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return false;
        }


        return true;
    }
}
