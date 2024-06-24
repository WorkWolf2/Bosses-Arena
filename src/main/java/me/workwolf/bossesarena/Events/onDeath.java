package me.workwolf.bossesarena.Events;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
import java.util.Map;

public class onDeath implements Listener {

    private final BossesArena plugin;

    private final FileManager fileManager;


    ConfigManager settings;

    public onDeath(BossesArena plugin) {
        this.plugin = plugin;
        this.fileManager = new FileManager();
        this.settings = new ConfigManager(plugin);

    }

    @EventHandler
    public void onBossDeath(MythicMobDeathEvent event) {
        int endtime = settings.getEndTime();
        ActiveMob mob = event.getMob();

        if (mob != null) {
            String mobID = mob.getType().getInternalName();

            CustomRegionManager regionManager = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));

            String getBoss = fileManager.getBoss(plugin, mobID);

            if (getBoss != null) {
                String trueMobId = getBoss.replace(".yml", "");

                Map<String, Object> data = fileManager.getData(trueMobId, plugin);

                String regionID = (String) data.get("regionID");

                List<Player> playersInRegion = regionManager.getPlayersInsideRegion(regionID);

                Component bossRoomDoneMessage = MiniMessage.miniMessage().deserialize(settings.BossRoomDone(), Placeholder.unparsed("time", String.valueOf(endtime)));
                playersInRegion.forEach(p -> p.sendMessage(settings.getPrefix().append(bossRoomDoneMessage)));

                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (Player p : playersInRegion) {
                            if (p.getBedSpawnLocation() != null) {
                                p.teleport(p.getBedSpawnLocation());
                            } else {
                                p.teleport(settings.getGlobalSpawn());
                            }


                        }
                    }
                }, endtime * 20L);
            }
        }
    }
}
