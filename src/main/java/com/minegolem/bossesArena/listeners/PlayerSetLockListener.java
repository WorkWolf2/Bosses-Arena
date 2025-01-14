package com.minegolem.bossesArena.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.commands.BSetKeyLockCMD;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerSetLockListener implements Listener {
    private final BossesArena plugin;

    private static final Map<UUID, String> arenaNameMap = BSetKeyLockCMD.getArenaNameMap();

    @EventHandler
    public void onPlayerSetLock(PlayerInteractEvent event) {
        assert event.getClickedBlock() != null;
        if (!arenaNameMap.containsKey(event.getPlayer().getUniqueId())) return;
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        PersistentDataContainer pdc = new CustomBlockData(block, plugin);

        String arenaName = arenaNameMap.get(player.getUniqueId());

        if (!pdc.isEmpty()) return;

        event.setCancelled(true);

        pdc.set(BossesArena.blockNSK, PersistentDataType.STRING, arenaName);

        ArmorStand textDisplay = (ArmorStand) block.getWorld().spawn(block.getLocation().add(.5, 1, .5), ArmorStand.class, (text) -> {
            text.setCustomName(arenaName);
            text.setGravity(false);
            text.setSmall(true);
            text.setCustomNameVisible(true);
            text.setVisible(false);

            Bukkit.getScheduler().runTaskLater(plugin, text::remove, 60L);
        });

        block.getWorld().playSound(block.getLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.NEUTRAL, 1f, 1f);

        arenaNameMap.remove(player.getUniqueId());
    }

}
