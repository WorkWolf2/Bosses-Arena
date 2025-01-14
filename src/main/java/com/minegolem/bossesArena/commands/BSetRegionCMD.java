package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
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
public class BSetRegionCMD extends BaseCommand {
    private final BossesArena plugin;

    @Getter
    public static final Map<UUID, String> arenaNameMap = new HashMap<>();
    @Getter
    public static Map<UUID, BoxSelection> selectionMap = new HashMap<>();

    @Default
    @Subcommand("setregion")
    @Syntax("<name>")
    @Description("set the arena's region")
    @CommandPermission("bossesarena.commands.setregion")
    @CommandCompletion("@arenas")
    public void onRegionSet(Player player, @Single String arenaName) throws IOException {
        FileManager fm = plugin.getFileManager();

        if (fm.getArena().get(arenaName) == null) {
            player.sendMessage(ChatUtils.serialize("<gray>Arena with that name does not exist!</gray>"));
            return;
        }

        if (selectionMap.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatUtils.serialize("<gray>Stai già settando una region!</gray>"));
            return;
        }

        player.sendMessage(ChatUtils.serialize("<gray>region setter iniziato!</gray>"));

        selectionMap.put(player.getUniqueId(), new BoxSelection());
        arenaNameMap.put(player.getUniqueId(), arenaName);

    }
}
