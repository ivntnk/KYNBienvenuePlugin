package fr.plugin.ivn_tnk.managers;

import fr.plugin.ivn_tnk.BienvenuePlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NewPlayerManager {

    private final BienvenuePlugin plugin;
    private final Map<UUID, Set<UUID>> newPlayers = new HashMap<>();

    public NewPlayerManager(BienvenuePlugin plugin) {
        this.plugin = plugin;
    }

    public void registerNewPlayer(final UUID uuid) {
        newPlayers.put(uuid, new HashSet<UUID>());
        int timeout = plugin.getConfig().getInt("new-player-timeout", 300);
        new BukkitRunnable() {
            @Override
            public void run() {
                newPlayers.remove(uuid);
            }
        }.runTaskLater(plugin, (long) timeout * 20L);
    }

    public boolean isNewPlayer(UUID uuid) {
        return newPlayers.containsKey(uuid);
    }

    public boolean hasAlreadyWelcomed(UUID newPlayer, UUID welcomer) {
        Set<UUID> set = newPlayers.get(newPlayer);
        return set != null && set.contains(welcomer);
    }

    public void markWelcomed(UUID newPlayer, UUID welcomer) {
        Set<UUID> set = newPlayers.get(newPlayer);
        if (set != null) {
            set.add(welcomer);
        }
    }

    public void onPlayerQuit(UUID uuid) {
        newPlayers.remove(uuid);
    }
}