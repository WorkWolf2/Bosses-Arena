package me.workwolf.bossesarena.Events;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.subcommands.regionCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.BoxSelection;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;

import java.io.File;

import java.util.Map;
import java.util.UUID;

public class RegionSetter implements Listener {

    private final BossesArena plugin;

    private final regionCommand regioncommand;

    private final ConfigManager settings;

    private final FileManager fileManager = new FileManager();

    private Map<UUID, BoxSelection> selectionMap;

    private Map<UUID, String> arenaName;

    public RegionSetter(BossesArena plugin) {
        this.plugin = plugin;
        this.regioncommand = new regionCommand(plugin);
        this.settings = new ConfigManager(plugin);
        this.selectionMap = getRegionCommand().getSelectionMap();
        this.arenaName = getRegionCommand().getArenaNameMap();
    }

    public regionCommand getRegionCommand() {
        return regioncommand;
    }

    @EventHandler
    public void regionSetter(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!selectionMap.containsKey(player.getUniqueId())) return;
        BoxSelection boxSelection = selectionMap.get(player.getUniqueId());

        Block block = event.getClickedBlock();
        if (block == null) return;

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                event.setCancelled(true);

                boxSelection.setFirst(block);
                boxSelection.setFirstWorld(block.getWorld());

                player.sendMessage(settings.FirstPosition());
                break;
            case RIGHT_CLICK_BLOCK:
                event.setCancelled(true);

                boxSelection.setSecond(block);
                boxSelection.setSecondWorld(block.getWorld());

                player.sendMessage(settings.SecondPosition());
                break;
        }

        if (boxSelection.isComplete()) {
            selectionMap.clear();

            CustomRegionManager region = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));

            BoundingBox boundingBox = boxSelection.buildBox();

            if (boundingBox == null) {
                player.sendMessage(settings.differentWorlds());
                arenaName.clear();
                return;
            }

            World world = boxSelection.getWorld();

            Location min = new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
            Location max = new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());

            String ArenaName = arenaName.get(player.getUniqueId());

            region.createRegion(ArenaName, min, max);

            if (region != null) {
                player.sendMessage(settings.SuccessFullyRegion());

                fileManager.replaceRegionID(ArenaName, ArenaName, plugin);
            } else {
                player.sendMessage(settings.impossibleToCreateRegion());
            }

            arenaName.clear();
        }

    }
}
