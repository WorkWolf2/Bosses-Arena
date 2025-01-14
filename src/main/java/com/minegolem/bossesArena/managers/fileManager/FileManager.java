package com.minegolem.bossesArena.managers.fileManager;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.utils.ItemStackUtils;
import com.minegolem.bossesArena.utils.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileManager extends AbstractFileManager {
    private static FileManager instance = null;

    @SuppressWarnings("FieldCanBeLocal")
    private final BossesArena plugin;
    @Getter
    public final Map<String, Arena> arena = new HashMap<>();

    private final String folder;
    private final Path path;

    private FileManager(BossesArena plugin) {
        super(plugin);

        initialize();

        this.plugin = plugin;

        this.folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        this.path = Paths.get(this.folder);

        this.loadAllArenasFromFolder();
    }

    /**
     * Create an arena
     *
     * @param name Arena name
     * @return true if an arena exists, false is an arena doesn't exist
     */
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean createArena(@NotNull String name) {
        String filePath = folder + File.separator + name + ".yml";
        File file = new File(filePath);

        if(file.exists()) return true;

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Location bossLocation = new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0);
        Location spawnLocation = new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0, 0f, 0f);

        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);

        config.set("name", name);
        config.set("region-id", "unset");
        config.set("mythic-mob-id", "unset");
        config.set("boss-location", bossLocation.serialize());
        config.set("spawn-location", spawnLocation.serialize());
        config.set("key", ItemStackUtils.serializeItemStack(key));
        config.set("mmocore-level", -1);
        config.set("vault-money", -1);
        config.set("rewards", new ArrayList<>());

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        arena.put(name, new Arena(name, file, config));
        return false;
    }

    @Override
    public void loadAllArenasFromFolder() {
        if (!Files.isDirectory(path)) return;

        try (Stream<Path> files = Files.list(path)) {
            files.filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".yml"))
                    .forEach(file -> {
                        File fileConfiguration = file.toFile();
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(fileConfiguration);

                        arena.put(fileConfiguration.getName().replace(".yml", ""),
                                new Arena(fileConfiguration.getName().replace(".yml", ""), fileConfiguration, config));
                    });
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to load arenas from folder: " + path, e);
        }
    }

    @Override
    public void setKey(@NotNull Arena arena, @NotNull ItemStack key) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("key", ItemStackUtils.serializeItemStack(key));
        config.save(arena.getFile());
    }

    @Override
    public void setRegionId(@NotNull Arena arena, @NotNull String regionId) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("region-id", regionId);
        config.save(arena.getFile());
        config.options().copyDefaults(true);
    }

    @Override
    public void setMmoCoreLevel(@NotNull Arena arena, int mmoCoreLevel) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("mmocore-level", mmoCoreLevel);
        config.save(arena.getFile());
        config.options().copyDefaults(true);
    }

    @Override
    public void setVaultMoney(@NotNull Arena arena, double vaultMoney) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("vault-money", vaultMoney);
        config.save(arena.getFile());
        config.options().copyDefaults(true);
    }

    @Override
    public void setBossLocation(@NotNull Arena arena, @NotNull Location location) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("boss-location", location.serialize());
        config.save(arena.getFile());
        config.options().copyDefaults(true);
    }

    @Override
    public void setMythicMobID(@NotNull Arena arena, @NotNull String mythicMobID) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("mythic-mob-id", mythicMobID);
        config.save(arena.getFile());
        config.options().copyDefaults(true);
    }

    @Override
    public void setTeleportLocation(@NotNull Arena arena, @NotNull Location location) throws IOException {
        YamlConfiguration config = arena.getConfig();

        config.set("spawn-location", location.serialize());
        config.save(arena.getFile());
        config.options().copyDefaults(true);
    }

    public static FileManager getInstance(BossesArena plugin) {
        if (Objects.isNull(instance)) {
            instance = new FileManager(plugin);
        }
        return instance;
    }
}
