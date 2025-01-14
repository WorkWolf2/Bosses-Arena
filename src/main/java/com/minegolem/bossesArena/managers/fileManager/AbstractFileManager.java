package com.minegolem.bossesArena.managers.fileManager;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.utils.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class AbstractFileManager {
    private final BossesArena plugin;
    private static String folder;
    private static Path path;

    public void initialize() {
        folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        path = Paths.get(folder);
    }

    public abstract boolean createArena(@NotNull String name) throws IOException;

    public abstract void loadAllArenasFromFolder() throws IOException;

    public abstract void setKey(@NotNull Arena arena, @NotNull ItemStack key) throws IOException;

    public abstract void setRegionId(@NotNull Arena arena, @NotNull String regionId) throws IOException;

    public abstract void setMmoCoreLevel(@NotNull Arena arena, int mmoCoreLevel) throws IOException;

    public abstract void setVaultMoney(@NotNull Arena arena, double vaultMoney) throws IOException;

    public abstract void setBossLocation(@NotNull Arena arena, @NotNull Location location) throws IOException;

    public abstract void setMythicMobID(@NotNull Arena arena, @NotNull String mythicMobID) throws IOException;

    public abstract void setTeleportLocation(@NotNull Arena arena, @NotNull Location location) throws IOException;

    public void initializeFolder() {
        if (Files.exists(path) && Files.isDirectory(path)) return;

        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to create folder: " + folder, e);
        }
    }

    public static int totalArenas() {
        if (!Files.isDirectory(path)) return 0;

        try (Stream<Path> paths = Files.list(path)) {
            return (int) paths.filter(Files::isRegularFile).count();
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to list files: " + folder, e);
            return 0;
        }
    }

    public static List<String> getArenaList() throws IOException {
        List<String> arenaList = new ArrayList<>();

        try (Stream<Path> paths = Files.list(path)) {
            paths.filter(Files::isRegularFile)
                    .forEach(f -> arenaList.add(f.toFile().getName().replace(".yml", "")));
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to list files: " + folder, e);
        }

        return arenaList;
    }

    public static void reloadArenas() {
        if (!Files.isDirectory(path)) {
            return;
        }

        try (Stream<Path> paths = Files.list(path)) {
            paths.filter(Files::isRegularFile).forEach(f -> {
                try {
                    YamlConfiguration.loadConfiguration(f.toFile()).save(f.toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
