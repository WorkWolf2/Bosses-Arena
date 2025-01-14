package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.User;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import com.minegolem.bossesArena.utils.ChatUtils;
import com.minegolem.bossesArena.utils.Logger;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerKillBossListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onPlayerKillBoss(EntityDeathEvent event) {
        LivingEntity ent = event.getEntity();
        ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(ent));

        if (mob == null) return;

        double mobHealt = mob.getType().getHealth(mob);

        PersistentDataContainer pdc = BukkitAdapter.adapt(mob.getEntity()).getPersistentDataContainer();

        if (!pdc.has(BossesArena.mobNSK, PersistentDataType.STRING)) return;

        Player killer = event.getEntity().getKiller();

        FileManager fm = plugin.getFileManager();
        ConfigManager cm = plugin.getConfigManager();

        Arena arena = fm.getArena().get(pdc.get(BossesArena.mobNSK, PersistentDataType.STRING));

        String arenaName = arena.getName();

        int endTime = cm.endTime;

        List<Player> playersInRegion = RegionManager.getPlayersInsideRegion(arenaName);
        playersInRegion.forEach(p -> p.sendMessage(ChatUtils.serialize(
                String.format("<red>L'arena si conluderà in %d secondi</red>", endTime)
        )));

        plugin.getClosingArena().put(arena, true);

        double threshold = 100.0 / playersInRegion.size();

        System.out.println("MobHealt" + mobHealt);

        System.out.println("Threshold" + threshold);
        System.out.println(plugin.getMobDamageMap());

        if (plugin.getMobDamageMap().containsKey(arena)) {
            HashMap<UUID, Double> damageMap = plugin.getMobDamageMap().get(arena);

            for (Map.Entry<UUID, Double> entry : damageMap.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());

                if (player != null) {
                    double percentage = (entry.getValue() / mobHealt) * 100;

                    System.out.println("Entry " + entry.getValue() + " " + percentage);
                    System.out.println("Percentage " + percentage);

                    if (percentage >= threshold) {
                        plugin.getData().getUserByUuid(player.getUniqueId()).ifPresent(value -> plugin.getData().updateKill(value, value.kill() + 1));
                        Logger.log(Logger.LogLevel.INFO, "Statistic saved successfully...");
                    }
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playersInRegion.forEach(p -> {
                plugin.getClosingArena().remove(arena);

                if (p.getBedSpawnLocation() != null) {
                    p.teleport(p.getBedSpawnLocation());
                } else {
                    p.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
                }
            });
        }, endTime * 20L);
    }
}
