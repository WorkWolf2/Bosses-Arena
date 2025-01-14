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
public class BSetMoneyCMD extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("setmoney")
    @Syntax("<name> <money>")
    @Description("set the vault money to enter the arena")
    @CommandPermission("bossesarena.commands.setmoney")
    @CommandCompletion("@arenas")
    public void onVaultMoneySet(CommandSender sender, @Single String arenaName, @Default("-1") double money) throws IOException {
        FileManager fm = plugin.getFileManager();

        if (fm.getArena().get(arenaName) == null) {
            sender.sendMessage(ChatUtils.serialize("<gray>Arena with that name does not exist!</gray>"));
            return;
        }

        Arena arena = fm.getArena().get(arenaName);

        fm.setVaultMoney(arena, money);
        sender.sendMessage(ChatUtils.serialize("<gray>Money changed successfully!</gray>"));
    }
}
