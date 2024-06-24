package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import org.bukkit.command.CommandSender;

public class helpCommand extends SubCommand {
    private final BossesArena plugin;

    public helpCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Help command";
    }

    @Override
    public String getSyntax() {
        return "/ba help";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ConfigManager settings = new ConfigManager(plugin);

        if (!(sender.hasPermission("ba.*") || sender.hasPermission("ba.help") || sender.isOp())) {
            sender.sendMessage(settings.getNoPerms());
            return;
        }

        sender.sendMessage(settings.parse("<gray><st>--->--*-------------------------------------*--<---</gray>\n").append(settings.getPrefix()).append(settings.parse("<gray>Here's the documentation: <gold><click:open_url:https://docs.minegolem.com/minegolem/minegolems-plugin/bosses-arena/commands-and-permissions>https://docs.minegolem.com/minegolem/minegolems-plugin/bosses-arena/commands-and-permissions</click></gold>\n")).append(settings.parse("<gray><st>--->--*-------------------------------------*--<---</gray>")));
    }
}
