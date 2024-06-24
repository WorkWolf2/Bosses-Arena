package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Menu.MainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class editorCommand extends SubCommand {
    private final BossesArena plugin;

    public editorCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "editor";
    }

    @Override
    public String getDescription() {
        return "open editor menu";
    }

    @Override
    public String getSyntax() {
        return "/ba editor";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }

        ConfigManager settings = new ConfigManager(plugin);

        Player player = (Player) sender;

        if (!(player.hasPermission("ba.editor") || player.hasPermission("ba.*") || player.isOp())) {
            player.sendMessage(settings.getNoPerms());
            return;
        }

        try {
            new MainMenu(plugin).displayTo(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
