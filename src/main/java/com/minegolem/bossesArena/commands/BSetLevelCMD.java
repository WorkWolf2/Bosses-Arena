package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BSetLevelCMD extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("setlevel")
    @Syntax("<name> <level>")
    @CommandPermission("bossesarena.commands.mmocorelevel")
    @Description("set the mmocore level to enter the arena")
    @CommandCompletion("@arenas")
    public void onMmoCoreLevelSet(CommandSender sender, @Single String arenaName, @Default("-1") int level) throws IOException {
        if (!BossesArena.mmoCoreInstalled) {
            sender.sendMessage(ChatUtils.serialize("<gray>You don't have MMOCore installed on the server. Please install it first!</gray>"));
            return;
        }

        FileManager fm = plugin.getFileManager();

        if (fm.getArena().get(arenaName) == null) {
            sender.sendMessage(ChatUtils.serialize("<gray>Arena with that name does not exist!</gray>"));
            return;
        }

        Arena arena = fm.getArena().get(arenaName);

        fm.setMmoCoreLevel(arena, level);
        sender.sendMessage(ChatUtils.serialize("<gray>Level changed successfully!</gray>"));
    }
}
