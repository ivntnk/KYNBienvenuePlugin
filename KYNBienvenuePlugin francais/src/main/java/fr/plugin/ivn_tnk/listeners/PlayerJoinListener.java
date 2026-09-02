package fr.plugin.bienvenue.listeners;

import fr.plugin.bienvenue.BienvenuePlugin;
import fr.plugin.bienvenue.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final BienvenuePlugin plugin;

    public PlayerJoinListener(BienvenuePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) return;

                    plugin.getNewPlayerManager().registerNewPlayer(player.getUniqueId());

                    List<String> cmds = plugin.getConfig().getStringList("execute-command");
                    for (String cmd : cmds) {
                        if (cmd != null && !cmd.isEmpty()) {
                            String finalCmd = cmd
                                    .replace("%player%", player.getName())
                                    .replace("%uuid%", player.getUniqueId().toString());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                        }
                    }

                    String joinMsg = plugin.getConfig().getString("message-join-nouveau", "NONE");
                    if (joinMsg != null && !joinMsg.equalsIgnoreCase("NONE") && !joinMsg.isEmpty()) {
                        Bukkit.broadcastMessage(ColorUtils.colorize(joinMsg.replace("{newplayer}", player.getName())));
                    }
                }
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getNewPlayerManager().onPlayerQuit(event.getPlayer().getUniqueId());
    }
}