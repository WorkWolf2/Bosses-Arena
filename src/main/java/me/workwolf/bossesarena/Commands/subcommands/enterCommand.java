package me.workwolf.bossesarena.Commands.subcommands;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class enterCommand extends SubCommand {
    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public enterCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "enter";
    }

    @Override
    public String getDescription() {
        return "Enter an Arena";
    }

    @Override
    public String getSyntax() {
        return "<color:#ac8761>/ba enter <arena name> <player name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ConfigManager settings = new ConfigManager(plugin);

        if (!(sender.hasPermission("ba.*") || sender.hasPermission("ba.enter") || sender.isOp())) {
            sender.sendMessage(settings.getNoPerms());
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(settings.getNoName());
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(settings.getPrefix().append(settings.parse(getSyntax())));
            return;
        }

        String arenaName = args[1];

        Player target = null;
        try {
            target = Bukkit.getPlayer(args[2]);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (target == null) {
            sender.sendMessage(settings.getOffPlayer());
            return;
        }


        if (!fileManager.checkFiles(plugin)) {
            sender.sendMessage(settings.getNoArenas());
            return;
        }

        if (!fileManager.checkFileInFolder(arenaName + ".yml", plugin)) {
            sender.sendMessage(settings.getNoArena());
            return;
        }

        CustomRegionManager regionManager = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));

        Map<String, Object> data = fileManager.getData(arenaName, plugin);

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

        tpPlayer(spawnLoc, target, settings);

        settings.sendTitle(target);

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
