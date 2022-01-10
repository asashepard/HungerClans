package com.gmail.creepycucumber1.hungerclans.clan;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WarManager {
    private HungerClans plugin;

    public WarManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void createNewWar(String side1, String side2) {

        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");
        plugin.getDataManager().getConfig().createSection("wars." + side1 + "vs" + side2);
        int side1score = 0;
        int side2score = 0;

        Date currentDate = Calendar.getInstance().getTime();
        DateFormat dayFormat = new SimpleDateFormat("dd");
        DateFormat monthFormat = new SimpleDateFormat("MM");
        int currentDay = Integer.parseInt(dayFormat.format(currentDate));
        String currentMonth = monthFormat.format(currentDate);
        int last = 30;
        if(Integer.parseInt(currentMonth) % 2 == 1)
            last = 31;
        else if(Integer.parseInt(currentMonth) == 2)
            last = 28;
        String endsByTime = String.valueOf((currentDay + 4) % last);

        Map<String, Object> map = new HashMap<>();
        map.put("side1", side1); //side that declared the war
        map.put("side2", side2);
        map.put("endsByTime", endsByTime); //wars are 3 days long
        map.put("side1score", side1score);
        map.put("side2score", side2score);

        plugin.getDataManager().getConfig().createSection("wars." + side1 + "vs" + side2, map);
        plugin.getDataManager().saveConfig();

    }

    //setter
    public void endWar(String war) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");

        String side1 = cfg.getString(war + ".side1");
        String side2 = cfg.getString(war + ".side2");
        int side1score = cfg.getInt(war + ".side1score");
        int side2score = cfg.getInt(war + ".side2score");
        String winner = "draw";
        String loser = "draw";
        if(side1score > side2score) {
            winner = side1;
            loser = side2;
        }
        else {
            winner = side2;
            loser = side1;
        }
        int difference = plugin.getClanManager().getPoints(winner) - plugin.getClanManager().getPoints(loser);

        if(winner.equalsIgnoreCase("draw")) {
            plugin.getClanManager().addPoints(winner, 20);
            plugin.getClanManager().addPoints(winner, 20);
            return;
        }

        for(String uuid : plugin.getDataManager().getConfig().getConfigurationSection("clans." + winner).getStringList("members")) {
            plugin.getEssentials().getUser(UUID.fromString(uuid)).addMail("Congratulations, your clan won a war against " + loser + "! " +
                    "You have received $5000 as a result. The score was " + side1score + " - " + side2score + ".");
            plugin.getVault().depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), 5000);
        }
        for(String uuid : plugin.getDataManager().getConfig().getConfigurationSection("clans." + loser).getStringList("members")) {
            plugin.getEssentials().getUser(UUID.fromString(uuid)).addMail("Your clan lost a war against " + loser + ". " +
                    "The score was " + side1score + " - " + side2score + ".");
            plugin.getVault().depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), 5000);
        }

        int bonus = Math.abs(difference) / 50;
        int rand = (int) (Math.random() * 50 + 1);

        plugin.getClanManager().addPoints(winner, (difference > 0 ? 150 + rand : 150 + rand + bonus));
        plugin.getClanManager().removePoints(loser, (difference > 0 ? 45 - (rand / 2) : 20 - (rand / 4)));

        cfg.set(war, null);
        plugin.getDataManager().saveConfig();
    }

    public void endWar(String war, String winner) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");

        for(String uuid : plugin.getDataManager().getConfig().getConfigurationSection("clans." + winner).getStringList("members")) {
            plugin.getEssentials().getUser(UUID.fromString(uuid)).addMail("Congratulations, your clan won a war! " +
                    "You won so hard, the other team doesn't even exist anymore! You have received $5000 as a result.");
            plugin.getVault().depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), 5000);
        }
        int rand = (int) (Math.random() * 50 + 1);
        plugin.getClanManager().addPoints(winner, 150 + rand);

        cfg.set(war, null);
        plugin.getDataManager().saveConfig();
    }

    public void addPoints(String war, String side, int amount) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);

        if(isSide1(war, side))
            cfg.set("side1score", cfg.getInt("side1score") + amount);
        else
            cfg.set("side2score", cfg.getInt("side2score") + amount);

        plugin.getDataManager().saveConfig();
    }

    //getter
    public boolean isInWar(String side) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");
        for(String str : cfg.getKeys(false))
            if(cfg.getString(str + ".side1").equalsIgnoreCase(side) || cfg.getString(str + ".side2").equalsIgnoreCase(side))
                return true;
        return false;
    }

    public boolean areAtWar(String side1, String side2) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");
        return cfg.getKeys(false).contains(side1 + "vs" + side2) || cfg.getKeys(false).contains(side2 + "vs" + side1);
    }

    public ArrayList<String> getWars(String side) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars");
        ArrayList<String> wars = new ArrayList<>();
        for(String str : cfg.getKeys(false)) {
            if(cfg.getString(str + ".side1").equalsIgnoreCase(side) || cfg.getString(str + ".side2").equalsIgnoreCase(side)) {
                wars.add(str);
            }
        }
        return wars;
    }

    public String getSide1(String war) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        return cfg.getString("side1");
    }

    public String getSide2(String war) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        return cfg.getString("side2");
    }

    public String getOpposition(String war, String side) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        if(cfg.getString("side1").equalsIgnoreCase(side)) {
            return cfg.getString("side2");
        }
        else if(cfg.getString("side2").equalsIgnoreCase(side)) {
            return cfg.getString("side1");
        }
        return "none";
    }

    public boolean isSide1(String war, String side) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        return cfg.getString("side1").equalsIgnoreCase(side);
    }

    public int getSide1Points(String war) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        return cfg.getInt("side1score");
    }

    public int getSide2Points(String war) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        return cfg.getInt("side2score");
    }

    public String getEndDay(String war) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("wars." + war);
        return cfg.getString("endsByTime");
    }
}
