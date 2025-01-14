package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.regionManager.CustomRegion;
import com.minegolem.bossesArena.managers.regionManager.RegionManager;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BSetBossLocationCMD extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("setbosslocation")
    @Syntax("<name>")
    @CommandPermission("bossesarena.commands.setbosslocation")
    @Description("set the location where the boss spawns")
    @CommandCompletion("@arenas")
    public void onBossLocationSet(Player player, @Single String arenaName) throws IOException {
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
            System.out.println(arenaName);
            System.out.println(region);
            System.out.println(region.isInsideRegion(location));
            return;
        }

        Arena arena = fm.getArena().get(arenaName);

        fm.setBossLocation(arena, location);
        player.sendMessage(ChatUtils.serialize("<gray>Spawn location changed successfully!</gray>"));
    }
}
