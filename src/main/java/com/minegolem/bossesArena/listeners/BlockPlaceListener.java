package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.BossesArena;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class BlockPlaceListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(BossesArena.keyNSK, PersistentDataType.STRING)) event.setCancelled(true);
    }
}
