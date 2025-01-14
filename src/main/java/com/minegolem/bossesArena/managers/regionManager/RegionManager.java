package com.minegolem.bossesArena.managers.regionManager;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.utils.Logger;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegionManager extends AbstractRegion{
    private static final Map<String, CustomRegion> regions = new HashMap<>();
    private final File regionsFile;
    private final YamlConfiguration regionsConfig;
    private final ConfigurationSection regionsSection;

    public RegionManager(BossesArena plugin, File regionsFile) {
        super(plugin);
        this.regionsFile = regionsFile;

        initialize(this.regionsFile);

        this.regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);

        if (this.regionsConfig.getConfigurationSection("regions") == null) {
            this.regionsSection = this.regionsConfig.createSection("regions");
        } else {
            this.regionsSection = regionsConfig.getConfigurationSection("regions");
        }

        this.loadRegionsFromConfig();
    }

    @Override
    public void serializeLocation(ConfigurationSection section, Location location) {
        section.set("world", Objects.requireNonNull(location.getWorld()).getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void saveRegionToFile(String name, Location minPoint, Location maxPoint) {
        if (this.regionsSection == null) {
            this.regionsSection.createSection("regions");
        }

        ConfigurationSection section = regionsSection.createSection(name);
        this.serializeLocation(section.createSection("minPoint"), minPoint);
        this.serializeLocation(section.createSection("maxPoint"), maxPoint);

        try {
            regionsConfig.save(regionsFile);
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.ERROR, "Could not save region to " + name, e);
        }
    }

    @Override
    public CustomRegion getRegionByName(String name) {
        return regions.get(name);
    }

    @Override
    public void loadRegionsFromConfig() {
        if (regionsSection == null) return;

        for (String regionName : regionsSection.getKeys(false)) {
            ConfigurationSection regionSection = regionsConfig.getConfigurationSection("regions." + regionName);

            if (regionSection == null) continue;

            Location minPoint = getLocationFromConfig(regionSection.getConfigurationSection("minPoint"));
            Location maxPoint = getLocationFromConfig(regionSection.getConfigurationSection("maxPoint"));

            if(minPoint == null || maxPoint == null) continue;

            CustomRegion region = new Region(regionName, minPoint, maxPoint);
            regions.put(regionName, region);
        }
    }

    @Override
    public void createRegion(String name, Location minPoint, Location maxPoint) {
        CustomRegion region = new Region(name, minPoint, maxPoint);
        regions.put(name, region);
        saveRegionToFile(name, minPoint, maxPoint);
    }

    public static List<Player> getPlayersInsideRegion(String regionName) {
        CustomRegion region = regions.get(regionName);
        if (region == null) return new ArrayList<>();

        return region.getPlayerInsideRegion();
    }

    public static List<ActiveMob> getMobsInsideRegion(String regionName) {
        CustomRegion region = regions.get(regionName);
        if (region == null) return new ArrayList<>();

        return region.getMobInsideRegion();
    }

    public void loadChunks(String regionID, World world) {
        Location minPoint = this.getMinPoint(regionID, world);
        Location maxPoint = this.getMaxPoint(regionID, world);

        int chunkX1 = (int) minPoint.getX() >> 4;
        int chunkZ1 = (int) minPoint.getZ() >> 4;
        int chunkX2 = (int) maxPoint.getX() >> 4;
        int chunkZ2 = (int) maxPoint.getZ() >> 4;

        for (int x = Math.min(chunkX1, chunkX2); x <= Math.max(chunkX1, chunkX2); x++) {
            for (int z = Math.min(chunkZ1, chunkZ2); z <= Math.max(chunkZ1, chunkZ2); z++) {
                Chunk chunk = world.getChunkAt(x, z);
            }
        }
    }
}
