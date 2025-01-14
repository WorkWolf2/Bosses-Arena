package com.minegolem.bossesArena.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.minegolem.bossesArena.BossesArena;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurniturePlaceEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Boss;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class OraxenFurniturePlaceListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onFurniturePlace(OraxenFurniturePlaceEvent event) {
        Block block = event.getBlock();

        PersistentDataContainer pdc = new CustomBlockData(block, plugin);

        if (pdc.has(BossesArena.blockNSK, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
