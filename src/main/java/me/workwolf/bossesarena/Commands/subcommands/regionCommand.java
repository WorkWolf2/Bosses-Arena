package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.BoxSelection;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class regionCommand extends SubCommand {

    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public regionCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "region";
    }

    @Override
    public String getDescription() {
        return "Set region of an Arena";
    }

    @Override
    public String getSyntax() {
        return "/ba region <arena name>";
    }


    public Map<UUID, String> getArenaNameMap() {
        return arenaNameMap;
    }

    public Map<UUID, BoxSelection> getSelectionMap() {
        return selectionMap;
    }

    public final static Map<UUID, String> arenaNameMap = new HashMap<>();

    public static Map<UUID, BoxSelection> selectionMap = new HashMap<>();


    @Override
    public void perform(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        ConfigManager settings = new ConfigManager(plugin);

        if (!player.hasPermission("ba.*") || !player.hasPermission("ba.region") || !player.isOp()) {
            player.sendMessage(settings.getNoPerms());
            return;
        }


        if (args.length < 2) {
            player.sendMessage(settings.getNoName());
            return;
        }

        String arenaName = args[1];

        CustomRegionManager regionManager = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));

        if (!fileManager.checkFiles(plugin)) {
            player.sendMessage(settings.getNoArenas());
            return;
        }

        if (!fileManager.checkFileInFolder(arenaName + ".yml", plugin)) {
            player.sendMessage(settings.getNoArena());
            return;
        }

        if (regionManager.doesArenaExist(arenaName)) {
            player.sendMessage(settings.getAlreadyRegion());
            return;
        }

        if (selectionMap.isEmpty()) {
            player.sendMessage(settings.getRegionSetter());
            selectionMap.put(player.getUniqueId(), new BoxSelection());
            arenaNameMap.put(player.getUniqueId(), arenaName);
        } else {
            player.sendMessage(settings.getAlreadyRegionSetter());
        }
    }
}
