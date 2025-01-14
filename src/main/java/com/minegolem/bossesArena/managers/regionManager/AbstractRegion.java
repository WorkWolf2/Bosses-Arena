package com.minegolem.bossesArena.managers.regionManager;

import com.minegolem.bossesArena.BossesArena;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@RequiredArgsConstructor
public abstract class AbstractRegion {
    private final BossesArena plugin;

    /**
     * A simple Location serializer to a YAML File
     *
     * @param section A section where a location will be serialized
     * @param location A Location to serialize in a section
     */
    public abstract void serializeLocation(ConfigurationSection section, Location location);

    /**
     *  Saves a region with it serialized location to the YAML file
     *
     * @param name The region name
     * @param minPoint The region's minimum point
     * @param maxPoint The region's minimum point
     */
    @SuppressWarnings("null")
    public abstract void saveRegionToFile(String name, Location minPoint, Location maxPoint);

    /**
     *  Get the region by its name
     *
     * @param name The region name to get
     * @return CustomRegion from an HashMap
     */
    public abstract CustomRegion getRegionByName(String name);

    /**
     *  Load all regions from the YAML File
     */
    public abstract void loadRegionsFromConfig();

    /**
     *  The only method to create a region
     *
     * @param name the region name
     * @param minPoint The region's minimum point
     * @param maxPoint The region's minimum point
     */
    public abstract void createRegion(String name, Location minPoint, Location maxPoint);

    /**
     * A method to get the region data
     *
     * @return the YAML file configuration
     */
    public YamlConfiguration getData() {
        File regionFile = new File(plugin.getDataFolder(), "regions.yml");

        return YamlConfiguration.loadConfiguration(regionFile);
    }

    public Location getLocationFromConfig(ConfigurationSection section) {
        if (section == null) return null;

        World world = Bukkit.getWorld(section.getString("world"));
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");

        return new Location(world, x, y, z);
    }

    public Location getMinPoint(String regionID, World world) {
        YamlConfiguration regionData = getData();

        double x = regionData.getDouble("regions." + regionID + ".minPoint.x");
        double y = regionData.getDouble("regions." + regionID + ".minPoint.y");
        double z = regionData.getDouble("regions." + regionID + ".minPoint.z");

        return new Location(world, x, y, z);
    }

    public Location getMaxPoint(String regionID, World world) {
        YamlConfiguration regionData = getData();

        double x = regionData.getDouble("regions." + regionID + ".maxPoint.x");
        double y = regionData.getDouble("regions." + regionID + ".maxPoint.y");
        double z = regionData.getDouble("regions." + regionID + ".maxPoint.z");

        return new Location(world, x, y, z);
    }

    public static void initialize(File file) {
        if(file.exists()) return;

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
