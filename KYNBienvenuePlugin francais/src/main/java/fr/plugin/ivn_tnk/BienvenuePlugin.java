package fr.plugin.bienvenue;

import fr.plugin.bienvenue.commands.BvnCommand;
import fr.plugin.bienvenue.commands.BvnLeaderboardCommand;
import fr.plugin.bienvenue.listeners.PlayerJoinListener;
import fr.plugin.bienvenue.managers.DataManager;
import fr.plugin.bienvenue.managers.NewPlayerManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BienvenuePlugin extends JavaPlugin {

    private static BienvenuePlugin instance;
    private DataManager dataManager;
    private NewPlayerManager newPlayerManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.dataManager = new DataManager(this);
        this.newPlayerManager = new NewPlayerManager(this);

        BvnCommand bvnCmd = new BvnCommand(this);
        PluginCommand bvn = getCommand("bvn");
        if (bvn != null) {
            bvn.setExecutor(bvnCmd);
            bvn.setTabCompleter(bvnCmd);
        }

        PluginCommand lb = getCommand("bvnleaderboard");
        if (lb != null) {
            lb.setExecutor(new BvnLeaderboardCommand(this));
        }

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getLogger().info("BienvenuePlugin active !");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
        getLogger().info("BienvenuePlugin desactive.");
    }

    public static BienvenuePlugin getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public NewPlayerManager getNewPlayerManager() {
        return newPlayerManager;
    }
}