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
public class BSetMythicMobIdCMD extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("setmythicmobid")
    @CommandPermission("bossesarena.commands.mythicmobid")
    @Syntax("<name> <id>")
    @Description("set the mythic mob id to spawn the boss")
    @CommandCompletion("@arenas")
    public void onMythicMobSet(CommandSender sender, @Single String arenaName, @Single String mythicID) throws IOException {
        FileManager fm = plugin.getFileManager();

        if (fm.getArena().get(arenaName) == null) {
            sender.sendMessage(ChatUtils.serialize("<gray>Arena with that name does not exist!</gray>"));
            return;
        }

        Arena arena = fm.getArena().get(arenaName);

        fm.setMythicMobID(arena, mythicID);
        sender.sendMessage(ChatUtils.serialize("<gray>Mythic Mob ID changed successfully!</gray>"));
    }
}
