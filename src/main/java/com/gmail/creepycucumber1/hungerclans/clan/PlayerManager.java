package com.gmail.creepycucumber1.hungerclans.clan;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PlayerManager {
    private HungerClans plugin;

    public PlayerManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void createNewPlayer(Player player) {
        String uuid = player.getUniqueId().toString();

        plugin.getDataManager().getConfig().createSection("players." + uuid);

        //joined server
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String joinedServer = dateFormat.format(date);
        //joined clan
        String joinedClan = "n/a";
        //suffered last damage by enemy
        long lastDamagedByEnemy = 0;

        HashMap<String, Object> map = new HashMap<>();
        map.put("joinedServer", joinedServer);
        map.put("joinedClan", joinedClan);
        map.put("lastDamagedByEnemy", lastDamagedByEnemy);

        plugin.getDataManager().getConfig().createSection("players." + uuid, map);
        plugin.getDataManager().saveConfig();

    }

    //setter
    public void setLastDamagedByEnemyToNow(Player player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        long time = Instant.now().toEpochMilli();
        cfg.set("lastDamagedByEnemy", time);
        plugin.getDataManager().saveConfig();
    }

    public void setJoinedClanToNow(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String joined = dateFormat.format(date);
        cfg.set("joinedClan", joined);
        plugin.getDataManager().saveConfig();
    }

    public void removeJoinedClan(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("joinedClan", "n/a");
        plugin.getDataManager().saveConfig();
    }

    //getter
    public long getLastDamagedByEnemy(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getLong("lastDamagedByEnemy");
    }

    public String getJoined(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getString("joinedServer");
    }

    public String getJoinedClan(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getString("joinedClan");
    }

}
