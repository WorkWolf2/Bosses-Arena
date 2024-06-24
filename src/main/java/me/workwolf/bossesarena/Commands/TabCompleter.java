package me.workwolf.bossesarena.Commands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final BossesArena plugin;

    private final FileManager fileManager = new FileManager();

    public TabCompleter(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("ba")) {
            List<String> tabCompleter = new ArrayList<>();

            if (args.length == 1) {
                CommandManager cmdManager = new CommandManager(plugin);

                ArrayList<SubCommand> commands = cmdManager.getSubCommands();

                for (SubCommand subCommand : commands) {
                    tabCompleter.add(subCommand.getName().trim());
                }

                return tabCompleter;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("help")) {
                    return null;
                }

                return fileManager.getFiles(plugin);
            }

            if (args.length == 3 && args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("enter")) {
                List<String> OnlinePlayers = new ArrayList<>();

                Bukkit.getOnlinePlayers().forEach(p -> OnlinePlayers.add(p.getName()));

                return OnlinePlayers;
            }


        }
        return new ArrayList<>();
    }
}
