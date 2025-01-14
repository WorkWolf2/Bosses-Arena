package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.CustomRegion;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;

@AllArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BSetSpawnLocation extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("setspawnlocation")
    @Syntax("<name>")
    @CommandPermission("bossesarena.commands.setspawnlocation")
    @Description("set the spawn location when you enter the arena")
    @CommandCompletion("@arenas")
    public void onSpawnLocationSet(Player player, @Single String arenaName) throws IOException {
        FileManager fm = plugin.getFileManager();
        RegionManager regionManager = plugin.getRegionManager();

        if (fm.getArena().get(arenaName) == null) {
            player.sendMessage(ChatUtils.serialize("<gray>Arena with that name does not exist!</gray>"));
            return;
        }

        Location location = player.getLocation();

        CustomRegion region = regionManager.getRegionByName(arenaName);

        if (region == null || !region.isInsideRegion(location)) {
            player.sendMessage(ChatUtils.serialize("<gray>This location is not in a region or you have to set a region first!</gray>"));
            return;
        }

        Arena arena = fm.getArena().get(arenaName);

        fm.setTeleportLocation(arena, location);
        player.sendMessage(ChatUtils.serialize("<gray>Spawn location changed successfully!</gray>"));
    }
}
