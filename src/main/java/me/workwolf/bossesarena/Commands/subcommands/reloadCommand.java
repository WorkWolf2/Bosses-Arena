package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import org.bukkit.command.CommandSender;

public class reloadCommand extends SubCommand {

    private final BossesArena plugin;
    private final ConfigManager settings;

    public reloadCommand(BossesArena plugin) {
        this.plugin = plugin;
        this.settings = new ConfigManager(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reload command";
    }

    @Override
    public String getSyntax() {
        return "/ba reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ba.*") || !sender.hasPermission("ba.reload") || !sender.isOp()) {
            sender.sendMessage(settings.getNoPerms());
            return;
        }

        plugin.reloadConfig();

        sender.sendMessage(settings.getReloadCompleted());

    }
}
