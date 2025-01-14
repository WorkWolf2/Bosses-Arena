package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.User;
import com.minegolem.bossesArena.utils.Logger;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLOutput;

@RequiredArgsConstructor
public class PlayerDeathListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        System.out.println(player.getName());
        System.out.println(player.getKiller());

        if (player.getLastDamageCause().getEntity() == null) return;
        LivingEntity entity = (LivingEntity) player.getLastDamageCause().getEntity();

        ActiveMob mob = null;
        try (MythicBukkit inst = MythicBukkit.inst()) {
            mob = inst.getMobManager().getMythicMobInstance(BukkitAdapter.adapt(entity));
        }



        if (mob == null) return;

        PersistentDataContainer pdc = BukkitAdapter.adapt(mob.getEntity()).getPersistentDataContainer();

        if (!pdc.has(BossesArena.mobNSK, PersistentDataType.STRING)) return;

        System.out.println(pdc.getKeys());

        plugin.getData().getUserByUuid(player.getUniqueId()).ifPresent(value -> plugin.getData().updateDeath(value, value.death() + 1));

        Logger.log(Logger.LogLevel.INFO, "Statistic saved successfully...");
    }
}
