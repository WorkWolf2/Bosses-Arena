package me.workwolf.bossesarena.Events;

import com.jeff_media.customblockdata.CustomBlockData;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class enterEvent implements Listener {

    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public enterEvent(BossesArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOpenArena(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getItem() == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        ConfigManager settings = new ConfigManager(plugin);

        Player player = event.getPlayer();

        ItemStack is = event.getItem();
        ItemMeta meta = is.getItemMeta();

        assert meta != null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        CustomBlockData pdcBlock = new CustomBlockData(event.getClickedBlock(), plugin);

        if (pdcBlock.isEmpty() || pdc.isEmpty()) return;


        if (Objects.equals(pdc.getKeys(), pdcBlock.getKeys())) {
            int itemAmount = is.getAmount();
            is.setAmount(itemAmount - 1);

            CustomRegionManager regionManager = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));

            Map<String, Object> data = fileManager.getData(pdc.getKeys().iterator().next().getKey(), plugin);

            enter(regionManager, data, player, settings, event);

        }
    }

    @SuppressWarnings("unchecked")
    private void enter(CustomRegionManager regionManager,  Map<String, Object> data, Player player, ConfigManager settings, PlayerInteractEvent event) {
        Map<String, Object> Mobspawn = (Map<String, Object>) data.get("locationboss");

        World world = Bukkit.getWorld((String) Mobspawn.get("world"));
        float yaw = Float.parseFloat(String.valueOf((double) Mobspawn.get("yaw")));
        float pitch = Float.parseFloat(String.valueOf((double) Mobspawn.get("pitch")));

        Location bossLoc = new Location(Bukkit.getWorld((String) Mobspawn.get("world")), (double) Mobspawn.get("x"), (double) Mobspawn.get("y"), (double) Mobspawn.get("z"), yaw, pitch);

        String regionID = ((String) data.get("regionID")).trim();

        List<Player> playersInRegion = regionManager.getPlayersInsideRegion(regionID);
        Collection<ActiveMob> mobsInRegion = regionManager.getMobsInsideRegion(regionID);

        if (playersInRegion.isEmpty()) {
            loadChunks(regionManager, regionID, world);

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    String mobId = ((String) data.get("mythicmobID")).trim();

                    despawnMobs(mobId, mobsInRegion);

                    spawnMob(mobId, bossLoc);
                }
            }, 5L);
        }

        Map<String, Object> spawnLoc = (Map<String, Object>) data.get("spawnLoc");

        tpPlayer(spawnLoc, player, settings);

        settings.sendTitle(player);
    }

    private void despawnMobs(String mobId, Collection<ActiveMob> mobsInRegion) {
        Collection<ActiveMob> activeMobs = MythicBukkit.inst().getMobManager().getActiveMobs(am -> am.getMobType().equals(mobId));

        for (ActiveMob activeMob : activeMobs) {
            activeMob.despawn();
        }

        for (ActiveMob mobbino : mobsInRegion) {
            mobbino.despawn();
        }
    }

    private void tpPlayer(Map<String, Object> spawnLoc, Player player, ConfigManager settings) {
        float yawSpawn = Float.parseFloat(String.valueOf((double) spawnLoc.get("yaw")));
        float pitchSpawn = Float.parseFloat(String.valueOf((double) spawnLoc.get("pitch")));

        Location spawnLocation = new Location(Bukkit.getWorld((String) spawnLoc.get("world")), (double) spawnLoc.get("x"), (double) spawnLoc.get("y"), (double) spawnLoc.get("z"), yawSpawn, pitchSpawn);

        Bukkit.getWorld((String) spawnLoc.get("world")).playSound(spawnLocation, settings.getenterSound(), 3, 1);
        player.teleport(spawnLocation);
    }

    private void spawnMob(String mobId, Location bossLoc) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobId).orElse(null);

        if (mob != null) {
            ActiveMob aMob = mob.spawn(BukkitAdapter.adapt(bossLoc), 1);
        }
    }

    private void loadChunks(CustomRegionManager regionManager, String regionID, World world) {
        Location minPoint = regionManager.getMinPoint(regionID, plugin, world);
        Location maxPoint = regionManager.getMaxPoint(regionID, plugin, world);

        int chunkX1 = (int) minPoint.getX() >> 4;
        int chunkZ1 = (int) minPoint.getZ() >> 4;
        int chunkX2 = (int) maxPoint.getX() >> 4;
        int chunkZ2 = (int) maxPoint.getZ() >> 4;

        for (int x = Math.min(chunkX1, chunkX2); x <= Math.max(chunkX1, chunkX2); x++) {
            for (int z = Math.min(chunkZ1, chunkZ2); z <= Math.max(chunkZ1, chunkZ2); z++) {
                Chunk chunk = world.getChunkAt(x, z);
            }
        }
    }
}
