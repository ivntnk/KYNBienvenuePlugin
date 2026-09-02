package fr.plugin.bienvenue.managers;

import fr.plugin.bienvenue.BienvenuePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private final BienvenuePlugin plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<String, Integer> leaderboard = new HashMap<String, Integer>();

    public DataManager(BienvenuePlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        load();
    }

    public void load() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            leaderboard.clear();
            ConfigurationSection section = dataConfig.getConfigurationSection("leaderboard");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    leaderboard.put(key, section.getInt(key, 0));
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur chargement data.yml : " + e.getMessage());
        }
    }

    public void save() {
        try {
            for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
                dataConfig.set("leaderboard." + entry.getKey(), entry.getValue());
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur sauvegarde data.yml : " + e.getMessage());
        }
    }

    public void addWelcome(String playerName) {
        int current = leaderboard.containsKey(playerName) ? leaderboard.get(playerName) : 0;
        leaderboard.put(playerName, current + 1);
        save();
    }

    public List<Map.Entry<String, Integer>> getSortedLeaderboard() {
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(leaderboard.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });
        return list;
    }
}