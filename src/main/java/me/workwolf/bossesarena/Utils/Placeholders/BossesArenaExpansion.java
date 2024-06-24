package me.workwolf.bossesarena.Utils.Placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BossesArenaExpansion extends PlaceholderExpansion {
    private final BossesArena plugin;
    private final CustomRegionManager regionManager;

    private final FileManager fileManager = new FileManager();

    private final ConfigManager settings;

    public BossesArenaExpansion(BossesArena plugin) {
        this.plugin = plugin;
        regionManager = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));
        this.settings = new ConfigManager(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bossesarena";
    }

    @Override
    public @NotNull String getAuthor() {
        return "WorkWolf_2";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.contains("isplayer") && params.contains("in")) {

            Pattern pattern = Pattern.compile("\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(params);

            String[] tempArray = params.split("_");

            if (matcher.find()) {
                String placeholder = matcher.group(0);
                params = params.replace(placeholder, "placeholder");
                tempArray[1] = matcher.group(0);
            }

            String[] parts = params.split("_");

            if (parts.length >= 3) {
                String[] newArray = new String[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    if (i == 1 && matcher.find()) {
                        newArray[i] = matcher.group(0);
                        params = params.replace("placeholder", matcher.group(0));
                    } else if (i == 1) {
                        newArray[i] = tempArray[i];
                    } else {
                        newArray[i] = parts[i];
                    }
                }

                if (regionManager.isPlayerInsideRegion(newArray[1].replace('"', ' ').trim(), newArray[3].trim())) {
                    return settings.getTrue();
                } else {
                    return settings.getFalse();
                }

            }
        }

        if (params.equals("total_arenas")) {
            return String.valueOf(fileManager.TotalFiles(plugin));
        }

        if (params.contains("player") && params.contains("arena")) {
            return regionManager.regionNameFinder(plugin, player.getPlayer().getLocation().getX(), player.getPlayer().getLocation().getY(), player.getPlayer().getLocation().getZ());
        }

        return null;
    }
}
