package com.gmail.creepycucumber1.hungerclans.clan;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

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

        HashMap<String, Object> map = new HashMap<>();
        map.put("joinedServer", joinedServer);
        map.put("joinedClan", joinedClan);
        map.put("lastDamagedByEnemy", (long) 0);
        map.put("lastDamagedByPlayer", (long) 0);
        map.put("timePlayedToday", (long) 0);
        map.put("totalTimePlayed", (long) 0);
        map.put("blocksMined", 0);
        map.put("blocksPlaced", 0);
        map.put("updatedTimeAt", Instant.now().toEpochMilli());
        map.put("receivedReward", false);
        map.put("combatLogged", false);

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

    public void setLastDamagedByPlayerToNow(Player player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        long time = Instant.now().toEpochMilli();
        cfg.set("lastDamagedByPlayer", time);
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

    public void updateTimePlayed(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("timePlayedToday", getTimePlayedToday(player) + (Instant.now().toEpochMilli()) - getUpdateTimeLast(player));
        cfg.set("totalTimePlayed", getTotalTimePlayed(player) + (Instant.now().toEpochMilli()) - getUpdateTimeLast(player));
        plugin.getDataManager().saveConfig();
    }

    public void resetTimePlayedToday(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("totalTimePlayed", getTotalTimePlayed(player) + getTimePlayedToday(player));
        cfg.set("timePlayedToday", (long) 0);
        plugin.getDataManager().saveConfig();
    }

    public void setUpdateTimeLastToNow(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        long now = Instant.now().toEpochMilli();
        cfg.set("updatedTimeAt", now);
        plugin.getDataManager().saveConfig();
    }

    public boolean checkUpdate() {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("date");
        String day = String.valueOf(cfg.get("update"));
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd");
        String currentDay = dateFormat.format(date);
        if(!day.equals(currentDay)) {
            cfg.set("update", currentDay);
            plugin.getDataManager().saveConfig();
            return true;
        }
        return false;
    }

    public void addMinedBlocks(OfflinePlayer player, int blocks) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("blocksMined", getBlocksMined(player) + blocks);
        plugin.getDataManager().saveConfig();
    }

    public void addPlacedBlocks(OfflinePlayer player, int blocks) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("blocksPlaced", getBlocksPlaced(player) + blocks);
        plugin.getDataManager().saveConfig();
    }

    public void setReceivedReward(OfflinePlayer player, boolean received) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("receivedReward", received);
        plugin.getDataManager().saveConfig();
    }

    public void setCombatLogged(OfflinePlayer player, boolean combatLogged) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        cfg.set("combatLogged", combatLogged);
        plugin.getDataManager().saveConfig();
    }

    //getter
    public long getLastDamagedByEnemy(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getLong("lastDamagedByEnemy");
    }

    public long getLastDamagedByPlayer(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getLong("lastDamagedByPlayer");
    }

    public String getJoinedServer(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getString("joinedServer");
    }

    public String getJoinedClan(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getString("joinedClan");
    }

    public long getTotalTimePlayed(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getLong("totalTimePlayed");
    }

    public long getTimePlayedToday(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getLong("timePlayedToday");
    }

    public long getUpdateTimeLast(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getLong("updatedTimeAt");
    }

    public int getBlocksMined(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getInt("blocksMined");
    }

    public int getBlocksPlaced(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getInt("blocksPlaced");
    }

    public boolean receivedReward(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getBoolean("receivedReward");
    }

    public boolean getCombatLogged(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("players." + player.getUniqueId().toString());
        return cfg.getBoolean("combatLogged");
    }

}
