package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class PlayerDamageBossListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        if (!(event.getDamager() instanceof Player)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();
        double damage = event.getDamage();

        FileManager fm = plugin.getFileManager();

        ActiveMob mob = null;
        try (MythicBukkit inst = MythicBukkit.inst()) {
            mob = inst.getMobManager().getMythicMobInstance(BukkitAdapter.adapt(entity));
        }

        if (mob == null) return;

        PersistentDataContainer pdc = BukkitAdapter.adapt(mob.getEntity()).getPersistentDataContainer();
        String arenaName = pdc.get(BossesArena.mobNSK, PersistentDataType.STRING);
        List<Player> playersInRegion = RegionManager.getPlayersInsideRegion(arenaName);

        if (!pdc.has(BossesArena.mobNSK, PersistentDataType.STRING)) return;
        if (!playersInRegion.contains(player)) return;

        Arena arena = fm.getArena().get(arenaName);

        plugin.getMobDamageMap().putIfAbsent(arena, new HashMap<>());

        plugin.getMobDamageMap().get(arena).put(player.getUniqueId(),
                plugin.getMobDamageMap().get(arena).getOrDefault(player.getUniqueId(), 0.0) + damage);
    }
}
