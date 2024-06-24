package me.workwolf.bossesarena.Events;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.subcommands.setLockCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.event.block.Action;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Sound.ITEM_TOTEM_USE;

public class KeyLockEvent implements Listener {

    private final BossesArena plugin;

    private final setLockCommand setlockCommand;

    private final ConfigManager settings;

    private final FileManager fileManager = new FileManager();

    public Map<UUID, String> arenaName;

    public KeyLockEvent(BossesArena plugin) {
        this.plugin = plugin;
        this.setlockCommand = new setLockCommand(plugin);
        this.settings = new ConfigManager(plugin);
        this.arenaName = getSetLockCommand().getArenaName();
    }

    public setLockCommand getSetLockCommand() {
        return setlockCommand;
    }

    @EventHandler
    public void setKeyLock(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();

        if(!arenaName.containsKey(player.getUniqueId())) return;

        Block block = event.getClickedBlock();

        Location armorStandLoc = block.getLocation().add(.5, .3, .5);

        PersistentDataContainer pdc = new CustomBlockData(block, plugin);

        String name = arenaName.get(player.getUniqueId());

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            player.sendMessage(settings.leftClick());

            arenaName.clear();
            return;
        }

        if (pdc.isEmpty() || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.setCancelled(true);

            pdc.set(new NamespacedKey(plugin, name), PersistentDataType.STRING, name);

            ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(armorStandLoc.getWorld()).spawn(armorStandLoc, ArmorStand.class);
            block.getWorld().playSound(armorStandLoc, ITEM_TOTEM_USE, SoundCategory.NEUTRAL, 1f, 1f);

            armorStand.setGravity(false);
            armorStand.setSmall(false);
            armorStand.customName(settings.getPrefix().append(Component.text(name)));
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);

            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    armorStand.remove();
                }
            }, 3 * 20L);

            arenaName.clear();
        }
    }
}
