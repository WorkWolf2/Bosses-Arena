package com.minegolem.bossesArena.utils;

import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.experimental.UtilityClass;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ArenaUtils {

    public static boolean enterInArena(Arena arena, Player player, BossesArena plugin) {
        if (BossesArena.INSTANCE.getClosingArena().containsKey(arena)) {
            player.sendMessage(ChatUtils.serialize("<red>L'arena è in fase di chiusura aspetta prima di entrare!</red>"));
            return false;
        }

        YamlConfiguration config = arena.getConfig();
        RegionManager rm = plugin.getRegionManager();
        ConfigManager cm = plugin.getConfigManager();

        Map<String, Object> serializedBossLocation = YAMLUtils.yamlKeyToMap(Objects.requireNonNull(config.getConfigurationSection("boss-location")));
        Location bossLocation = Location.deserialize(serializedBossLocation);

        String regionID = config.getString("region-id");
        String mobID = config.getString("mythic-mob-id");

        double money = config.getDouble("vault-money");
        Integer level = null;

        if (BossesArena.mmoCoreInstalled) {
            level = config.getInt("mmocore-level");
        }

        assert mobID != null;
        assert regionID != null;

        if (mobID.equals("unset")) {
            player.sendMessage(ChatUtils.serialize("<red>Ricordati di settare il mob prima</red>"));
            return false;
        }


        if (regionID.equals("unset")) {
            player.sendMessage(ChatUtils.serialize("<red>Ricordati di settare la region prima</red>"));
            return false;
        }

        if (money != -1) {
            EconomyResponse response = plugin.getEcon().withdrawPlayer(player, money);

            if (!response.transactionSuccess()) {
                player.sendMessage(ChatUtils.serialize("<red>Bilancio insufficiente</red>"));
                return false;
            }
        }

        if (level != null && level != -1) {
            PlayerData playerData = PlayerData.get(player);

            if (!(playerData.getLevel() >= level)) {
                player.sendMessage(ChatUtils.serialize("<red>Livello troppo basso</red>"));
                return false;
            }
        }

        List<Player> playersInRegion = RegionManager.getPlayersInsideRegion(regionID);
        List<ActiveMob> mobsInRegion = RegionManager.getMobsInsideRegion(regionID);

        if (playersInRegion.isEmpty()) {
            rm.loadChunks(regionID, bossLocation.getWorld());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                despawnMobs(mobsInRegion);
                spawnMob(mobID, bossLocation, arena.getName());

            }, 10L);
        }

        Map<String, Object> serializedSpawnLocation = YAMLUtils.yamlKeyToMap(Objects.requireNonNull(config.getConfigurationSection("spawn-location")));
        Location spawnLocation = Location.deserialize(serializedSpawnLocation);

        Objects.requireNonNull(spawnLocation.getWorld()).playSound(spawnLocation, cm.enterSound, 3f, 1f);
        player.teleport(spawnLocation);

        cm.sendTitle(player);
        return true;
    }

    public static void despawnMobs(String mobID) {
        List<ActiveMob> mobs = new ArrayList<>();

        try (MythicBukkit instance = MythicBukkit.inst()) {
            instance.getMobManager().getActiveMobs(activeMob -> {
                if (activeMob.getMobType().equals(mobID)) {
                    mobs.add(activeMob);
                }
                return false;
            });
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "Impossible to despawn mobs", e);
        }

        for (ActiveMob mob : mobs) {
            mob.despawn();
        }
    }

    public static void despawnMobs(List<ActiveMob> mobs) {
        for (ActiveMob mob : mobs) {
            mob.despawn();
        }
    }

    public static void spawnMob(String mobID, Location location, String arenaName) {
        try (MythicBukkit instance = MythicBukkit.inst()) {
            MythicMob mob = instance.getMobManager().getMythicMob(mobID).orElse(null);

            if (mob != null) {
                ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(location), 1, SpawnReason.SUMMON, (amob) -> {
                    PersistentDataContainer pdc = amob.getPersistentDataContainer();

                    pdc.set(BossesArena.mobNSK, PersistentDataType.STRING, arenaName);
                });
            }
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.ERROR, "Impossible to spawn mobs", e);
        }
    }
}
