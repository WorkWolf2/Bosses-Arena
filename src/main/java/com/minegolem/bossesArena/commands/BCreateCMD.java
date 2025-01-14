package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BCreateCMD extends BaseCommand {

    private final BossesArena plugin;

    @Default
    @CommandPermission("bossesarena.commands.create")
    @Subcommand("create")
    @Syntax("<name>")
    @Description("Create an arena with the specified name")
    public void onCreate(CommandSender sender, @Single String arenaName) {
        boolean exists = plugin.getFileManager().createArena(arenaName.trim().toLowerCase());

        if (exists) {
            sender.sendMessage(ChatUtils.serialize("<gray>Arena with that name already exists!</gray>"));
            return;
            // throw new ArenaAlreadyExistsException("An arena with that name already exists");
        }

        sender.sendMessage(ChatUtils.serialize("<gray>Arena created successfully!</gray>"));
    }
}