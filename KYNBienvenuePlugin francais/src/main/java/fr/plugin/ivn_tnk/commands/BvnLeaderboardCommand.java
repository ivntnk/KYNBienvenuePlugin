package fr.plugin.bienvenue.commands;

import fr.plugin.bienvenue.BienvenuePlugin;
import fr.plugin.bienvenue.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class BvnLeaderboardCommand implements CommandExecutor {

    private final BienvenuePlugin plugin;

    public BvnLeaderboardCommand(BienvenuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Map.Entry<String, Integer>> sorted = plugin.getDataManager().getSortedLeaderboard();
        int maxEntries = plugin.getConfig().getInt("leaderboard.nombre-entrees", 10);
        String title = plugin.getConfig().getString("leaderboard.title", "&6&l--- TOP BIENVENUES ---");
        String ligne = plugin.getConfig().getString("leaderboard.ligne", "&e#{rank} &f{player} &7- &a{count} bienvenue(s)");

        sender.sendMessage(ColorUtils.colorize(title));

        if (sorted.isEmpty()) {
            sender.sendMessage(ColorUtils.colorize("&7Aucune bienvenue enregistree."));
        } else {
            int rank = 0;
            for (Map.Entry<String, Integer> entry : sorted) {
                if (rank >= maxEntries) break;
                rank++;
                String line = ligne
                        .replace("{rank}", String.valueOf(rank))
                        .replace("{player}", entry.getKey())
                        .replace("{count}", String.valueOf(entry.getValue()));
                sender.sendMessage(ColorUtils.colorize(line));
            }
        }

        return true;
    }
}