package me.workwolf.bossesarena.Commands.subcommands;

import com.jeff_media.customblockdata.CustomBlockData;
import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Logger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class barklCommand extends SubCommand {

    private final BossesArena plugin;

    public barklCommand(BossesArena plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getName() {
        return "barkl";
    }

    @Override
    public String getDescription() {
        return "Delete a keylock. You need to have console!";
    }

    @Override
    public String getSyntax() {
        return "/ba barkl <key lock name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }

        ConfigManager settings = new ConfigManager(plugin);

        Player player = (Player) sender;

        if (!(player.hasPermission("ba.barkl") || player.hasPermission("ba.*") || player.isOp())) {
            player.sendMessage(settings.getNoPerms());
            return;
        }

        Set<Material> m = null;

        Block block = player.getTargetBlock(m, 5);

        if (block == null) return;

        CustomBlockData pdc = new CustomBlockData(block, plugin);

        String name = args[1];

        if (args.length < 2) {
            player.sendMessage(settings.getNoName());
            return;
        }

        Logger.log(Logger.LogLevel.INFO, "Old Pdc: " + pdc.getKeys());

        pdc.remove(new NamespacedKey(plugin, name));
        player.sendMessage(settings.GetRemovedLock());

        Logger.log(Logger.LogLevel.INFO, "New Pdc: " + pdc.getKeys());
    }
}
