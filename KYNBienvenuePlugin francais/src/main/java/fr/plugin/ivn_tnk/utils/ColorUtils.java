package fr.plugin.bienvenue.utils;

import org.bukkit.ChatColor;

public class ColorUtils {

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}