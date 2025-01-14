package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.User;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private final BossesArena plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = new User(player.getUniqueId(), player.getName(), 0, 0);

        plugin.getData().ensureUser(user);
    }
}
