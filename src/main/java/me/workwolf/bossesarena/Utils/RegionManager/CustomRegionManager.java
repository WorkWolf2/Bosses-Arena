package me.workwolf.bossesarena.Utils.RegionManager;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CustomRegionManager {
    private final Map<String, CustomRegion> regions = new HashMap<>();
    private final File regionsFile;
    private final YamlConfiguration regionsConfig;

    public CustomRegionManager(File file) {
        this.regionsFile = file;
        this.regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
        loadRegionsFromConfig();
    }

    private void loadRegionsFromConfig() {
        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");
        if (regionsSection != null) {
            for (String regionName : regionsSection.getKeys(false)) {
                ConfigurationSection regionData = regionsSection.getConfigurationSection(regionName);
                if (regionData != null) {
                    Location minPoint = getLocationFromConfig(regionData.getConfigurationSection("minPoint"));
                    Location maxPoint = getLocationFromConfig(regionData.getConfigurationSection("maxPoint"));
                    if (minPoint != null && maxPoint != null) {
                        CustomRegion region = new RegionManager(regionName, minPoint, maxPoint);
                        regions.put(regionName, region);
                    }
                }
            }
        }
    }

    private Location getLocationFromConfig(ConfigurationSection section) {
        if (section != null) {
            World world = Bukkit.getWorlds().get(0);
            double x = section.getDouble("x");
            double y = section.getDouble("y");
            double z = section.getDouble("z");
            return new Location(world, x, y, z);
        }
        return null;
    }

    public CustomRegion getRegionByName(String name) {
        return regions.get(name);
    }

    public void createRegion(String name, Location minPoint, Location maxPoint) {
        CustomRegion region = new RegionManager(name, minPoint, maxPoint);
        regions.put(name, region);
        saveRegionToFile(name, minPoint, maxPoint);
    }

    private void saveRegionToFile(String name, Location minPoint, Location maxPoint) {
        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");
        if (regionsSection == null) {
            regionsSection = regionsConfig.createSection("regions");
        }

        ConfigurationSection regionSection = regionsSection.createSection(name);
        serializeLocation(regionSection.createSection("minPoint"), minPoint);
        serializeLocation(regionSection.createSection("maxPoint"), maxPoint);

        try {
            regionsConfig.save(regionsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getData(JavaPlugin plugin) {
        File regionsFile = new File(plugin.getDataFolder(), "regions.yml");

        return YamlConfiguration.loadConfiguration(regionsFile);
    }

    public Location getMinPoint(String regionID, JavaPlugin plugin, World world) {
        YamlConfiguration region = getData(plugin);

        double x = region.getDouble("regions." + regionID + ".minPoint.x");
        double z = region.getDouble("regions." + regionID + ".minPoint.y");
        double y = region.getDouble("regions." + regionID + ".minPoint.z");

        return new Location(world, x, y, z);
    }

    public Location getMaxPoint(String regionID, JavaPlugin plugin, World world) {
        YamlConfiguration region = getData(plugin);

        double x = region.getDouble("regions." + regionID + ".maxPoint.x");
        double z = region.getDouble("regions." + regionID + ".maxPoint.y");
        double y = region.getDouble("regions." + regionID + ".maxPoint.z");

        return new Location(world, x, y, z);
    }

    private void serializeLocation(ConfigurationSection section, Location location) {
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
    }

    public boolean isPlayerInsideRegion(String playerName, String regionName) {
        CustomRegion region = regions.get(regionName);

        if (region != null) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                return region.isInsideRegion(player.getLocation());
            }
        }
        return false;
    }

    public boolean isMobInsideRegion(String regionName, String MythicMobID) {
        CustomRegion region = regions.get(regionName);

        if (region != null) {
            Collection<ActiveMob> activeMobs = MythicBukkit.inst().getMobManager().getActiveMobs(am -> am.getMobType().equals(MythicMobID));

            for (ActiveMob activeMob : activeMobs) {
                AbstractLocation mobLoc = activeMob.getEntity().getLocation();

                Location mobLocation = BukkitAdapter.adapt(mobLoc);

                System.out.println(region.isInsideRegion(mobLocation));
                return region.isInsideRegion(mobLocation);
            }


        }
        return false;
    }

    public List<Player> getPlayersInsideRegion(String regionName) {
        CustomRegion region = regions.get(regionName);
        if (region != null) {
            return region.getPlayerInsideRegion();
        }
        return new ArrayList<>();
    }

    public Collection<ActiveMob> getMobsInsideRegion(String regionName) {
        CustomRegion region = regions.get(regionName);
        if (region != null) {
            return region.getMobInsideRegion();
        }
        return new ArrayList<>();
    }

    public String regionNameFinder (JavaPlugin plugin, double x, double y, double z) {
        String filePath = plugin.getDataFolder().getPath() + File.separator + "regions.yml";

        try {
            InputStream input = new FileInputStream(filePath);
            Yaml yaml = new Yaml();

            Map<String, Map<String, Map<String, Double>>> regions = yaml.load(input);

            String regionName = findRegion(regions, x, y, z);

            // Stampalo
            if (regionName != null) {
                return regionName;
            } else {
                return "NaN";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String findRegion(Map<String, Map<String, Map<String, Double>>> regions, double x, double y, double z) {
        for (Map.Entry<String, Map<String, Map<String, Double>>> entry : regions.entrySet()) {
            Map<String, Map<String, Double>> region = entry.getValue();

            for (Map.Entry<String, Map<String, Double>> regione : region.entrySet()) {

                Object minPointObj = regione.getValue().get("minPoint");
                Object maxPointObj = regione.getValue().get("maxPoint");

                if (minPointObj instanceof LinkedHashMap) {
                    LinkedHashMap<String, Double> minPoint = (LinkedHashMap<String, Double>) minPointObj;
                    LinkedHashMap<String, Double> maxPoint = (LinkedHashMap<String, Double>) maxPointObj;

                    if (maxPoint != null) {
                        double minX = minPoint.get("x");
                        double minY = minPoint.get("y");
                        double minZ = minPoint.get("z");

                        double maxX = maxPoint.get("x");
                        double maxY = maxPoint.get("y");
                        double maxZ = maxPoint.get("z");

                        if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                            return regione.getKey();
                        }
                    }
                } else if (minPointObj instanceof Double) {
                    // Handle the case where minPoint is a Double (if this makes sense in your context)
                    Double minPoint = (Double) minPointObj;

                    // Additional logic to handle this case if necessary
                }
            }
        }
        return null; // No region found
    }

    public boolean doesArenaExist(String arenaName) {
        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");

        if (regionsSection != null) {
            for (String arena : regionsSection.getKeys(false)) {
                if (arena.equalsIgnoreCase(arenaName)) {
                    return true; // L'arena esiste già
                }
            }
        }

        return false; // L'arena non esiste
    }
}
