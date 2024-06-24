package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class setLockCommand extends SubCommand {

    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public setLockCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setlock";
    }

    @Override
    public String getDescription() {
        return "set lock to enter";
    }

    @Override
    public String getSyntax() {
        return "/ba setlock <arena name>";
    }

    public static Map<UUID, String> arenaName = new HashMap<>();

    public Map<UUID, String> getArenaName() {
        return arenaName;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ConfigManager settings = new ConfigManager(plugin);

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (!settings.getUseKey()) {
            player.sendMessage(settings.disabledFunction());
            return;
        }

        if (!(player.hasPermission("ba.*") || player.hasPermission("ba.setlock") || player.isOp())) {
            player.sendMessage(settings.getNoPerms());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(settings.getNoName());
            return;
        }

        String name = args[1];

        if (!fileManager.checkFiles(plugin)) {
            player.sendMessage(settings.getNoArenas());
            return;
        }

        if (!fileManager.checkFileInFolder(name + ".yml", plugin)) {
            player.sendMessage(settings.getNoArena());
            return;
        }

        if (arenaName.isEmpty()) {
            player.sendMessage(settings.getSetKeyLock());
        } else {
            sender.sendMessage(settings.getAlreadyKeyLock());
        }

        arenaName.put(player.getUniqueId(), name);

        System.out.println(getArenaName());
    }
}
