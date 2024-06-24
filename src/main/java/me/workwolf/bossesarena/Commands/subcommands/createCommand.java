package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class createCommand extends SubCommand {
    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    private final ConfigManager settings;

    public createCommand(BossesArena plugin) {
        this.plugin = plugin;
        this.settings = new ConfigManager(plugin);
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create an Arena";
    }

    @Override
    public String getSyntax() {
        return "/ba create <arena name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(settings.getNoName());
            return;
        }

        if (!(player.hasPermission("ba.*") || player.hasPermission("ba.create") || player.isOp())) {
            player.sendMessage(settings.getNoPerms());
            return;
        }

        String arenaName = args[1];

        fileManager.createArena(arenaName, player, plugin);

    }
}
