package com.minegolem.bossesArena.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import com.minegolem.bossesArena.utils.ArenaUtils;
import com.minegolem.bossesArena.utils.ChatUtils;
import io.th0rgal.oraxen.api.OraxenItems;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

@RequiredArgsConstructor
public class PlayerUseKeyListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onPlayerUseKey(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getItem() == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        ConfigManager cm = plugin.getConfigManager();
        FileManager fm = plugin.getFileManager();

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        ItemStack is = event.getItem();
        ItemMeta meta = is.getItemMeta();

        assert meta != null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        PersistentDataContainer pdcBlock = new CustomBlockData(block, plugin);

        if (pdc.isEmpty() || pdcBlock.isEmpty()) return;

        if (!Objects.equals(pdc.get(BossesArena.keyNSK, PersistentDataType.STRING), pdcBlock.get(BossesArena.blockNSK, PersistentDataType.STRING))) return;

        if (!cm.useKey) {
            player.sendMessage("disabilitato");
            return;
        }

        int itemAmount = is.getAmount();

        String arenaName = pdc.get(BossesArena.keyNSK, PersistentDataType.STRING);
        Arena arena = fm.getArena().get(arenaName);

        if (ArenaUtils.enterInArena(arena, player, plugin)) {
            is.setAmount(itemAmount - 1);
        };

        event.setCancelled(true);
    }
}
