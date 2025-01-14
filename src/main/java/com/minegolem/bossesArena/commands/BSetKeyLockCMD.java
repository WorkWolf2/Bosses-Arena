package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.BoxSelection;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BSetKeyLockCMD extends BaseCommand {
    private final BossesArena plugin;

    @Getter
    public static final Map<UUID, String> arenaNameMap = new HashMap<>();

    @Default
    @Subcommand("setlock")
    @Syntax("<name>")
    @Description("set the arena's lock")
    @CommandPermission("bossesarena.commands.setlock")
    @CommandCompletion("@arenas")
    public void onRegionSet(Player player, @Single String arenaName) throws IOException {
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

        player.sendMessage(ChatUtils.serialize("<gray>Clicca su un blocco!</gray>"));

        if (arenaNameMap.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatUtils.serialize("<gray>Bro ho detto clicca su un blocco!</gray>"));
            return;
        }

        arenaNameMap.put(player.getUniqueId(), arenaName);
    }
}
