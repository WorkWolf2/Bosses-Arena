package com.minegolem.bossesArena.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@UtilityClass
public class Logger {

    public static void log(LogLevel level, String message) {
        if (message == null) return;

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', level.getPrefix() + message));
    }

    public static void log(LogLevel level, String message, Throwable throwable) {
        if (message == null) return;

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', level.getPrefix() + message + "|&r " + throwable));
    }

    @Getter
    public enum LogLevel {
        ERROR("&8[&c&lERROR&r&8] &f"),
        WARNING("&8[&6&lWARNING&r&8] &f"),
        INFO("&8[&e&lINFO&r&8] &f"),
        SUCCESS("&8[&a&lSUCCESS&r&8] &f"),
        OUTLINE("&7");

        private final String prefix;

        LogLevel(String prefix) {
            this.prefix = prefix;
        }
    }

}
