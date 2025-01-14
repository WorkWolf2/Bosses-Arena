package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BSetKeyCMD extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("setkey")
    @CommandPermission("bossesarena.commands.setkey")
    @Syntax("<name>")
    @Description("set the key to enter the arena")
    @CommandCompletion("@arenas")
    public void onKeySet(Player player, @Single String arenaName) throws IOException {
        ConfigManager cm = plugin.getConfigManager();

        if (!cm.useKey) {
            player.sendMessage(ChatUtils.serialize("<gray>questa funzione è disabilitata dal config!</gray>"));
            return;
        }

        FileManager fm = plugin.getFileManager();

        if (fm.getArena().get(arenaName) == null) {
            player.sendMessage(ChatUtils.serialize("<gray>Arena with that name does not exist!</gray>"));
            return;
        }

        ItemStack key = player.getInventory().getItemInMainHand();

        if (key.getType().isAir()) {
            player.sendMessage(ChatUtils.serialize("<gray>secondo me sei stupido in culo</gray>"));
            return;
        }

        Arena arena = fm.getArena().get(arenaName);

        fm.setKey(arena, key);
        player.sendMessage(ChatUtils.serialize("<gray>Key changed successfully!</gray>"));
    }
}
