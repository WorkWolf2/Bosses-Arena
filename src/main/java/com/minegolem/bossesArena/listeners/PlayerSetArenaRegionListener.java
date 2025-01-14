package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.commands.BSetRegionCMD;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.BoxSelection;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerSetArenaRegionListener implements Listener {
    private final BossesArena plugin;

    private static final Map<UUID, BoxSelection> selectionMap = BSetRegionCMD.getSelectionMap();
    private static final Map<UUID, String> arenaNameMap = BSetRegionCMD.getArenaNameMap();


    @EventHandler
    public void onPlayerSetArenaRegion(PlayerInteractEvent event) throws IOException {
        Player player = event.getPlayer();

        if (!selectionMap.containsKey(player.getUniqueId())) return;

        BoxSelection selection = selectionMap.get(player.getUniqueId());

        Block block = event.getClickedBlock();

        assert block != null;

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> {
                event.setCancelled(true);

                selection.setFirst(block);
                selection.setFirstWorld(block.getWorld());

                player.sendMessage("suca");
            }
            case RIGHT_CLICK_BLOCK -> {
                event.setCancelled(true);

                selection.setSecond(block);
                selection.setSecondWorld(block.getWorld());

                player.sendMessage("suca destro");
            }
        }

        if (selection.isComplete()) {
            selectionMap.remove(player.getUniqueId());

            RegionManager rm = plugin.getRegionManager();
            FileManager fm = plugin.getFileManager();

            BoundingBox boundingBox = selection.buildBox();

            if (boundingBox == null) {
                player.sendMessage("suca mondi diversi");
                arenaNameMap.remove(player.getUniqueId());
                return;
            }

            World world = selection.getWorld();

            Location min = new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
            Location max = new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());

            String arenaName = arenaNameMap.get(player.getUniqueId());

            rm.createRegion(arenaName, min, max);

            player.sendMessage("suca l'hai creata");

            fm.setRegionId(fm.getArena().get(arenaName), arenaName);

            arenaNameMap.remove(player.getUniqueId());
        }
    }
}
