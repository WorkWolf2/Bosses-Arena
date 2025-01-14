package com.minegolem.bossesArena.managers.configManagers;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.utils.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class ConfigManager {

    private final FileConfiguration config = BossesArena.INSTANCE.getConfig();

    public Sound enterSound = Sound.valueOf(config.getString("arena.enter-sound"));

    public boolean useKey = config.getBoolean("arena.use-key");

    public int endTime = config.getInt("arena.end-time");

    public boolean useTitle = config.getBoolean("arena.use-title");

    public List<String> endTitle = List.of(Objects.requireNonNull(ChatUtils.serialize(config.getString("arena.end-title.title"))), Objects.requireNonNull(ChatUtils.serialize(config.getString("arena.end-title.subtitle"))));

    public void sendTitle(Player player) {
        if (useTitle) {
            final String mainTitle = ChatUtils.serialize(endTitle.getFirst());
            final String subTitle = ChatUtils.serialize(endTitle.getLast());

            final Title title = Title.title(Component.text(mainTitle), Component.text(subTitle));

            Audience playerAudience = BossesArena.INSTANCE.getAdventure().player(player);
            playerAudience.showTitle(title);
        }
    }
}
