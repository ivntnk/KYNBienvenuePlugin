package fr.plugin.ivn_tnk.commands;

import fr.plugin.ivn_tnk.BienvenuePlugin;
import fr.plugin.ivn_tnk.utils.ColorUtils;
import fr.plugin.ivn_tnk.utils.FloodgateUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BvnCommand implements CommandExecutor, TabCompleter {

    private final BienvenuePlugin plugin;

    public BvnCommand(BienvenuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.colorize("&cSeuls les joueurs peuvent utiliser cette commande."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("bienvenue.use")) {
            player.sendMessage(ColorUtils.colorize("&cVous n'avez pas la permission."));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ColorUtils.colorize("&cUsage: /bvn <joueur> | /bvn reload"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("bienvenue.admin")) {
                player.sendMessage(ColorUtils.colorize("&cVous n'avez pas la permission."));
                return true;
            }
            plugin.reloadConfig();
            plugin.getDataManager().load();
            player.sendMessage(ColorUtils.colorize("&a[BienvenuePlugin] Configuration rechargee !"));
            return true;
        }

        String targetInput = args[0];
        Player targetPlayer = FloodgateUtil.resolvePlayer(targetInput);

        if (targetPlayer == null) {
            player.sendMessage(ColorUtils.colorize("&cCe joueur n'est pas connecte."));
            return true;
        }

        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            player.sendMessage(ColorUtils.colorize("&cVous ne pouvez pas vous accueillir vous-meme !"));
            return true;
        }

        if (!plugin.getNewPlayerManager().isNewPlayer(targetPlayer.getUniqueId())) {
            String msg = plugin.getConfig().getString("message-pas-nouveau", "&cCe joueur n'est pas un nouveau joueur !");
            player.sendMessage(ColorUtils.colorize(msg));
            return true;
        }

        if (plugin.getNewPlayerManager().hasAlreadyWelcomed(targetPlayer.getUniqueId(), player.getUniqueId())) {
            player.sendMessage(ColorUtils.colorize("&cVous avez deja accueilli ce joueur !"));
            return true;
        }

        plugin.getNewPlayerManager().markWelcomed(targetPlayer.getUniqueId(), player.getUniqueId());

        String message = plugin.getConfig().getString("message-bienvenue", "NONE");
        if (message != null && !message.equalsIgnoreCase("NONE") && !message.isEmpty()) {
            message = message.replace("{player}", player.getName()).replace("{newplayer}", targetPlayer.getName());
            Bukkit.broadcastMessage(ColorUtils.colorize(message));
        }

        String rewardMsg = plugin.getConfig().getString("message-recompense", "NONE");
        if (rewardMsg != null && !rewardMsg.equalsIgnoreCase("NONE") && !rewardMsg.isEmpty()) {
            rewardMsg = rewardMsg.replace("{player}", player.getName()).replace("{newplayer}", targetPlayer.getName());
            player.sendMessage(ColorUtils.colorize(rewardMsg));
        }

        String rewardCmd = plugin.getConfig().getString("reward-command", "");
        if (rewardCmd != null && !rewardCmd.isEmpty()) {
            rewardCmd = rewardCmd.replace("{player}", player.getName()).replace("{newplayer}", targetPlayer.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCmd);
        }

        plugin.getDataManager().addWelcome(player.getName());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (plugin.getNewPlayerManager().isNewPlayer(p.getUniqueId())) {
                    String displayName = FloodgateUtil.getTabName(p);
                    if (displayName.toLowerCase().startsWith(input)) {
                        completions.add(displayName);
                    }
                }
            }
            if ("reload".startsWith(input) && sender.hasPermission("bienvenue.admin")) {
                completions.add("reload");
            }
        }
        return completions;
    }
}