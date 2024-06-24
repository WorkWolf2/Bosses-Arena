package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class deleteCommand extends SubCommand {

    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public deleteCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete an Arena";
    }

    @Override
    public String getSyntax() {
        return "/ba delete <arena name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ConfigManager settings = new ConfigManager(plugin);

        if (!(sender.hasPermission("ba.*") || sender.hasPermission("ba.delete") || sender.isOp())) {
            sender.sendMessage(settings.getNoPerms());
            return;
        }

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(settings.getNoName());
            return;
        }

        String arenaName = args[1];

        if (!fileManager.checkFiles(plugin)) {
            player.sendMessage(settings.getNoArenas());
            return;
        }

        if (!fileManager.checkFileInFolder(arenaName + ".yml", plugin)) {
            player.sendMessage(settings.getNoArena());
            return;
        }

        fileManager.deleteFile(plugin, arenaName, settings.deleteArenaSuccess(), settings.deleteArenaFail(), player);
    }
}
