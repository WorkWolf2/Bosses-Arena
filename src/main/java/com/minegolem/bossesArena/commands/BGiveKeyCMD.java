package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.configManagers.ConfigManager;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.utils.ChatUtils;
import com.minegolem.bossesArena.utils.ItemStackUtils;
import com.minegolem.bossesArena.utils.YAMLUtils;
import io.th0rgal.oraxen.api.OraxenItems;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BGiveKeyCMD extends BaseCommand {
    private final BossesArena plugin;

    @Default
    @Subcommand("givekey")
    @CommandPermission("bossesarena.commands.givekey")
    @Syntax("<name> <quantity> [player_name]")
    @Description("Gives the arena's key to a player")
    @CommandCompletion("@arenas @range:1-64 @players")
    public void onGive(CommandSender sender, @Single String name, @Default("1") int quantity, @Optional String targetPlayer) throws IOException, InvalidConfigurationException {
        ConfigManager cm = plugin.getConfigManager();

        if (!cm.useKey) {
            sender.sendMessage(ChatUtils.serialize("<gray>questa funzione è disabilitata dal config!</gray>"));
            return;
        }

        if (sender instanceof Player player) {

            if (targetPlayer == null) {

                giveItemToPlayer(player, name, quantity);
                player.sendMessage(String.format("Hai ricevuto %d chiavi!",
                        quantity
                ));

            } else {

                giveItemToPlayer(Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)), name, quantity);

                player.sendMessage(String.format("Hai dato %d chiavi a %s!",
                        quantity,
                        Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)).getName()
                        ));

                Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)).sendMessage(String.format("Hai ricevuto %d chiavi!",
                        quantity
                ));

            }
        } else {

            if (targetPlayer == null) {

                sender.sendMessage("Specifica a chi desideri dare la key!");

            } else {

                giveItemToPlayer(Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)), name, quantity);

                sender.sendMessage(String.format("Hai dato %d chiavi a %s!",
                        quantity,
                        Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)).getName()
                ));

                Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)).sendMessage(String.format("Hai ricevuto %d chiavi!",
                        quantity
                ));
            }
        }
    }

    private void giveItemToPlayer(@NotNull Player player, @NotNull String arenaName, int quantity) throws IOException, InvalidConfigurationException {
        FileManager fm = plugin.getFileManager();

        Arena arena = fm.getArena().get(arenaName);

        arena.getConfig().load(arena.getFile());

        ItemStack key = ItemStackUtils.deserializeItemStack(YAMLUtils.yamlKeyToMap(Objects.requireNonNull(arena.getConfig().getConfigurationSection("key"))));

        if (OraxenItems.exists(key)) {
            assert key != null;
            key.setAmount(quantity);
            insertPDC(key, arenaName);

            player.getInventory().addItem(key);
            return;
        }

        assert key != null;

        insertPDC(key, arenaName);

        ItemMeta meta = key.getItemMeta();
        assert meta != null;

        if (meta.hasLore()) {
            List<String> rawLore = meta.getLore();
            assert rawLore != null;

            List<String> lore = new ArrayList<>();

            rawLore.forEach(s -> lore.add(ChatUtils.serialize(s)));

            meta.setLore(lore);
        }

        if (meta.hasDisplayName()) {
            meta.setDisplayName(ChatUtils.serialize(meta.getDisplayName()));
        }

        key.setItemMeta(meta);
        key.setAmount(quantity);

        player.getInventory().addItem(key);
    }

    private void insertPDC (ItemStack key, String arenaName) {
        assert key != null;
        ItemMeta meta = key.getItemMeta();

        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(BossesArena.keyNSK, PersistentDataType.STRING, arenaName);

        key.setItemMeta(meta);
    }
}
