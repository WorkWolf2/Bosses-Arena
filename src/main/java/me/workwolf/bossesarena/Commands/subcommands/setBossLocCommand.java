package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setBossLocCommand extends SubCommand {

    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public setBossLocCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setbossloc";
    }

    @Override
    public String getDescription() {
        return "setup boss spawn location";
    }

    @Override
    public String getSyntax() {
        return "/ba setbossloc <arena name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ConfigManager settings = new ConfigManager(plugin);

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ba.*") || !player.hasPermission("ba.setbossloc") || !player.isOp()) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage(settings.getNoName());
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

        String world_name = player.getWorld().getName();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        fileManager.setBossLoc(plugin, arenaName, world_name, x, y, z, yaw, pitch);

        player.sendMessage(settings.SetLocation());
    }
}
