package com.minegolem.bossesArena.utils;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
public class ChatUtils {
    private static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer
            .builder()
            .useUnusualXRepeatedCharacterHexFormat()
            .hexColors()
            .build();

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static String serialize(String string) {
        if (string == null) {
            Logger.log(Logger.LogLevel.ERROR, "The string cannot be null!", new NullPointerException());
            return null;
        }

        return COMPONENT_SERIALIZER.serialize(mm.deserialize(string));
    }

    public static String deserialize(String legacyString) {
        if (legacyString == null) {
            Logger.log(Logger.LogLevel.ERROR, "The string cannot be null!", new NullPointerException());
            return null;
        }

        if (legacyString.contains("&") || legacyString.contains("§")) {
            Component component = COMPONENT_SERIALIZER.deserialize(legacyString);

            return mm.serialize(component);
        }

        return legacyString;
    }

    public static String applyPlaceholders(CommandSender sender, @NotNull String message) {
        if (sender instanceof Player player) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }

        return message;
    }

    public static String applyPlaceholders(Player player, @NotNull String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
