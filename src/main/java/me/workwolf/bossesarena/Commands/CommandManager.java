package me.workwolf.bossesarena.Commands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

    private final BossesArena plugin;

    private ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandManager(BossesArena plugin) {
        this.plugin = plugin;
        subCommands.add(new createCommand(plugin));
        subCommands.add(new giveCommand(plugin));
        subCommands.add(new regionCommand(plugin));
        subCommands.add(new setLockCommand(plugin));
        subCommands.add(new reloadCommand(plugin));
        subCommands.add(new setPlayerLocCommand(plugin));
        subCommands.add(new deleteCommand(plugin));
        subCommands.add(new setBossLocCommand(plugin));
        subCommands.add(new enterCommand(plugin));
        subCommands.add(new helpCommand(plugin));
        subCommands.add(new barklCommand(plugin));
        subCommands.add(new editorCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            if (args.length > 0) {
                for(int i = 0; i < getSubCommands().size(); i++) {
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(sender, args);
                    }
                }
            } else {
                helpCommand help = new helpCommand(plugin);
                help.perform(sender, args);
            }

        return true;
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }
}
