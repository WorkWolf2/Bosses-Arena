package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.utils.ArenaUtils;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BEnterCMD extends BaseCommand {

    private final BossesArena plugin;

    @Default
    @CommandPermission("bossesarena.commands.enter")
    @Subcommand("enter")
    @Syntax("<name> <player_name>")
    @Description("Enter command to enter an arena")
    public void onEnter(CommandSender sender, @Single String arenaName, Player player) {
        FileManager fm = plugin.getFileManager();
        Arena arena = fm.getArena().get(arenaName);

        ArenaUtils.enterInArena(arena, player, plugin);

    }
}
