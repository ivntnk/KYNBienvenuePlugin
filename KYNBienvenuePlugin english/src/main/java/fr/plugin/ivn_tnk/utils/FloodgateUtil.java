package fr.plugin.ivn_tnk.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FloodgateUtil {

    private static Boolean floodgatePresent = null;

    public static boolean isFloodgatePresent() {
        if (floodgatePresent == null) {
            try {
                Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                floodgatePresent = Bukkit.getPluginManager().getPlugin("floodgate") != null;
            } catch (ClassNotFoundException e) {
                floodgatePresent = false;
            }
        }
        return floodgatePresent;
    }

    public static Player resolvePlayer(String input) {
        String name = input;
        if (name.startsWith(".") && isFloodgatePresent()) {
            name = name.substring(1);
        } else if (name.startsWith(".")) {
            name = name.substring(1);
        }

        Player exact = Bukkit.getPlayerExact(name);
        if (exact != null) return exact;

        if (isFloodgatePresent()) {
            String bedrockPrefix = getBedrockPrefix();
            Player withPrefix = Bukkit.getPlayerExact(bedrockPrefix + name);
            if (withPrefix != null) return withPrefix;
        }

        String lower = name.toLowerCase();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().equals(lower)) return p;
        }

        return null;
    }

    public static boolean isBedrockPlayer(Player player) {
        if (!isFloodgatePresent()) return false;
        try {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        } catch (Exception e) {
            return false;
        }
    }

    private static String getBedrockPrefix() {
        if (!isFloodgatePresent()) return ".";
        try {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().getPlayerPrefix();
        } catch (Exception e) {
            return ".";
        }
    }

    public static String getTabName(Player player) {
        if (isBedrockPlayer(player)) {
            String prefix = getBedrockPrefix();
            String name = player.getName();
            if (name.startsWith(prefix)) {
                return "." + name.substring(prefix.length());
            }
        }
        return player.getName();
    }

    public static UUID resolveUUID(String input) {
        Player p = resolvePlayer(input);
        return p != null ? p.getUniqueId() : null;
    }
}