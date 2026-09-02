package fr.plugin.ivn_tnk.utils;

import org.bukkit.ChatColor;

public class ColorUtils {

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}