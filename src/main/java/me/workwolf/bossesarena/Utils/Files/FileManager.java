package me.workwolf.bossesarena.Utils.Files;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Menu.ArenasItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileManager {

    public FileManager() {}

    public void addHashmap(Map<String, Object> data, Map<String, Object> location, Map<String, Object> key, Map<String, Object> spawnLoc, String name) {
        location.put("x", 0.0);
        location.put("y", 0.0);
        location.put("z", 0.0);
        location.put("yaw", 0.0);
        location.put("pitch", 0.0);
        location.put("world", "unset");

        spawnLoc.put("x", 0.0);
        spawnLoc.put("y", 0.0);
        spawnLoc.put("z", 0.0);
        spawnLoc.put("yaw", 0.0);
        spawnLoc.put("pitch", 0.0);
        spawnLoc.put("world", "unset");

        List<String> lore = new ArrayList<>();
        lore.add("");

        key.put("material", "unset");
        key.put("name", "unset");
        key.put("lore", lore);

        data.put("name", name);
        data.put("regionID", "unset");
        data.put("mythicmobID", "unset");
        data.put("locationboss", location);
        data.put("spawnLoc", spawnLoc);
        data.put("key", key);
    }

    public void createFolder(JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        Path path = Paths.get(folder);

        if (!Files.exists(path) && !Files.isDirectory(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createArena(String name, Player player, JavaPlugin plugin) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> location = new HashMap<>();
        Map<String, Object> key = new HashMap<>();
        Map<String, Object> spawnLoc = new HashMap<>();
        addHashmap(data, location, key, spawnLoc, name);

        createFolder(plugin);

        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";

        String filePath = folder + File.separator + name + ".yml";
        Path path = Paths.get(folder);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        if (Files.exists(path) && Files.isDirectory(path)) {
            try {
                var arenaFiles = Files.list(path).filter(p -> p.toString().endsWith(".yml")).map(Path::getFileName).map(Path::toString).collect(Collectors.toList());

                ConfigManager settings = new ConfigManager((BossesArena) plugin);

                if (!arenaFiles.contains(name + ".yml")) {
                    try (FileWriter writer = new FileWriter(filePath)) {
                        yaml.dump(data, writer);
                        player.sendMessage(settings.CreatedArena());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(settings.AlreadyArena());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean checkFiles(JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        Path path = Paths.get(folder);

        if (!Files.isDirectory(path)) {
            return false;
        }

        try {
            long fileCount = Files.list(path).filter(Files::isRegularFile).count();

            return fileCount > 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int TotalFiles(JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        Path path = Paths.get(folder);

        if (!Files.isDirectory(path)) {
            return 0;
        }

        try {
            int fileCount = (int) Files.list(path).filter(Files::isRegularFile).count();

            return fileCount;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // @ msg1 = success; msg2 = fail
    public void deleteFile(JavaPlugin plugin, String filename, Component msg1, Component msg2, Player player) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas" + File.separator + filename + ".yml";

        File deletedFile = new File(folder);

        boolean deleted = deletedFile.delete();

        if (deleted) {
            player.sendMessage(msg1);
        } else {
            player.sendMessage(msg2);
        }
    }

    public List<String> getFiles(JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        Path path = Paths.get(folder);

        if (!Files.isDirectory(path)) {
            return null;
        }

        try {
            List<String> filesName = new ArrayList<>();

            Files.list(path).filter(Files::isRegularFile).forEach(f -> filesName.add(f.getFileName().toString().replace(".yml", "")));

            return filesName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ArenasItems> getItems(JavaPlugin plugin) {
        List<String> Arenas = getFiles(plugin);
        List<ArenasItems> arenaItems = new ArrayList<>();

        for (String i : Arenas) {
            arenaItems.add(new ArenasItems(i));
        }

        return arenaItems;
    }

    public void reloadFiles(JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        Path path = Paths.get(folder);

        if (!Files.isDirectory(path)) {
            return;
        }

        try {
            Files.list(path).filter(Files::isRegularFile).forEach(f -> {
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

    public boolean checkFileInFolder(String fileName, JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        Path path = Paths.get(folder, fileName);

        return Files.exists(path) && Files.isRegularFile(path);
    }

    public Map<String, Object> getData(String name, JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        Path path = Paths.get(filePath);

        if (Files.exists(path) && Files.isRegularFile(path)) {
            try {
                Yaml yaml = new Yaml();

                Map<String, Object> data = yaml.load(Files.newBufferedReader(path));

                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
        return null;
    }

    public void replaceRegionID(String name, String newRegionID, JavaPlugin plugin) {
        File arenasFolder = new File(plugin.getDataFolder().getPath() + File.separator + "arenas");
        File arenaFile = new File(arenasFolder, name + ".yml");

        if (!arenaFile.exists()) {
            return;
        }

        YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        arenaConfig.set("regionID", newRegionID);

        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBossLoc(JavaPlugin plugin, @NotNull String name, @NotNull String world_name, double x, double y, double z, float yaw, float pitch) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        File arenaFile = new File(filePath);

        if (!arenaFile.exists()) {
            return;
        }

        YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        arenaConfig.set("locationboss.world", world_name);
        arenaConfig.set("locationboss.x", x);
        arenaConfig.set("locationboss.y", y);
        arenaConfig.set("locationboss.z", z);
        arenaConfig.set("locationboss.yaw", yaw);
        arenaConfig.set("locationboss.pitch", pitch);

        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBoss(JavaPlugin plugin, @NotNull String name, @NotNull String mobID) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        File arenaFile = new File(filePath);

        if (!arenaFile.exists()) {
            return;
        }

        YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        arenaConfig.set("mythicmobID", mobID);

        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTeleportLoc(JavaPlugin plugin, @NotNull String name, @NotNull String world_name, double x, double y, double z, float yaw, float pitch) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        File arenaFile = new File(filePath);

        if (!arenaFile.exists()) {
            return;
        }

        YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        arenaConfig.set("spawnLoc.world", world_name);
        arenaConfig.set("spawnLoc.x", x);
        arenaConfig.set("spawnLoc.y", y);
        arenaConfig.set("spawnLoc.z", z);
        arenaConfig.set("spawnLoc.yaw", yaw);
        arenaConfig.set("spawnLoc.pitch", pitch);

        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBoss(JavaPlugin plugin, String secondValue) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";

        File arenasFolder = new File(folder);

        File[] arenaFiles = arenasFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));

        if (arenaFiles == null || arenaFiles.length == 0) {
            return null;
        }

        for (File arenaFile : arenaFiles) {
            Yaml yaml = new Yaml();

            try (FileInputStream inputStream = new FileInputStream(arenaFile)) {
                Map<String, Object> data = yaml.load(inputStream);

                if (data != null && data.containsKey("mythicmobID")) {
                    String mythicMobsID = data.get("mythicmobID").toString();

                    if (mythicMobsID.equals(secondValue)) {
                        return arenaFile.getName();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Location getPlayerLoc(JavaPlugin plugin, String name) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        File arenaFile = new File(filePath);

        Yaml yaml = new Yaml();

            try (FileInputStream inputStream = new FileInputStream(arenaFile)) {
                Map<String, Object> data = yaml.load(inputStream);

                if (data != null && data.containsKey("spawnLoc")) {
                    Map<String, Object> spawnLoc = (Map<String, Object>) data.get("spawnLoc");

                    return new Location(Bukkit.getWorld((String) spawnLoc.get("world")), (Double) spawnLoc.get("x"), (Double) spawnLoc.get("y"), (Double) spawnLoc.get("y"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }

    public Location getBossLoc(JavaPlugin plugin, String name) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        File arenaFile = new File(filePath);

        Yaml yaml = new Yaml();

        try (FileInputStream inputStream = new FileInputStream(arenaFile)) {
            Map<String, Object> data = yaml.load(inputStream);

            if (data != null && data.containsKey("locationboss")) {
                Map<String, Object> spawnLoc = (Map<String, Object>) data.get("locationboss");

                return new Location(Bukkit.getWorld((String) spawnLoc.get("world")), (Double) spawnLoc.get("x"), (Double) spawnLoc.get("y"), (Double) spawnLoc.get("y"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBossName(JavaPlugin plugin, String name) {
        String folder = plugin.getDataFolder().getPath() + File.separator + "arenas";
        String filePath = folder + File.separator + name + ".yml";

        File arenaFile = new File(filePath);

        Yaml yaml = new Yaml();

        try (FileInputStream inputStream = new FileInputStream(arenaFile)) {
            Map<String, Object> data = yaml.load(inputStream);

                if (data != null && data.containsKey("mythicmobID")) {
                    String mythicMobsID = data.get("mythicmobID").toString();

                    return mythicMobsID;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

}
