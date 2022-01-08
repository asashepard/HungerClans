package com.gmail.creepycucumber1.hungerclans.clan;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.data.DataManager;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Banner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClanManager {

    private HungerClans plugin;

    public ClanManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void createNewClan(Player player, String clanName) {
        if(isInClan(player)) {
            player.sendMessage("You are already in a clan!"); return;
        }
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        for(String existingName : cfg.getKeys(false))
            if(existingName.toLowerCase().contains(clanName.toLowerCase()) ||
                    clanName.toLowerCase().contains(existingName.toLowerCase()) ||
                    clanName.substring(0, 4).equalsIgnoreCase(existingName.substring(0, 4))) {
                player.sendMessage(TextUtil.convertColor("That clan name is too similar to another!"));
                return;
            }
        if(clanName.length() < 4) {
            player.sendMessage(TextUtil.convertColor("That clan name is too short!"));
            return;
        }
        if(clanName.length() > 16) {
            player.sendMessage(TextUtil.convertColor("That clan name is too long!"));
            return;
        }
        if(clanName.substring(0, 4).contains(" ")) {
            player.sendMessage(TextUtil.convertColor("The first four characters of your clan name must not include spaces."));
            return;
        }
        if(!clanName.matches("^[ A-Za-z]+$")) {
            player.sendMessage(TextUtil.convertColor("A clan name must only have letters and spaces!"));
            return;
        }
        List<String> bannedNames = List.of("join", "leave", "gabe", "hayes", "xarkenz", "longbread", "nigger", "fag",
                "cunt", "burn jews", "fuck", "shit", "clan");
        for(String str : bannedNames)
            if(clanName.toLowerCase().contains(str)) {
                player.sendMessage(TextUtil.convertColor("Please think of something more creative."));
                return;
            }

        plugin.getDataManager().getConfig().createSection("clans." + clanName);

        //create map with info and add to data file section
        //clan code
        String clanCode = clanName.substring(0, 4).toUpperCase();
        //clan color
        List<ChatColor> COLORS = List.of(ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.AQUA,
                ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.BLUE, ChatColor.DARK_GREEN, ChatColor.RED);
        String clanColor = ColorUtil.colorToString(COLORS.get((int) (Math.random() * COLORS.size() + 1)));
        HashMap<String, String> relationships = new HashMap<>(); //other clan name, status
        ArrayList<String> members = new ArrayList<>(); //unique IDs
        members.add(player.getUniqueId().toString());
        ArrayList<String> trusted = new ArrayList<>(); //unique IDs
        trusted.add(player.getUniqueId().toString());
        String leader = player.getUniqueId().toString(); //unique ID
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String created = dateFormat.format(date);

        Map<String, Object> map = new HashMap<>();
        map.put("code", clanCode);
        map.put("color", clanColor);
        map.put("members", members);
        map.put("trusted", trusted);
        map.put("leader", leader);
        map.put("relationships", relationships);
        map.put("banner", banner);
        map.put("created", created);

        plugin.getDataManager().getConfig().createSection("clans." + clanName, map);
        plugin.getDataManager().saveConfig();

        player.sendMessage(TextUtil.convertColor("You have successfully created a clan!"));
    }

    //player methods
    //getter
    public boolean isInClan(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        for(String str : cfg.getKeys(false))
            for(String uuid : (ArrayList<String>) plugin.getDataManager().getConfig().get("clans." + str + ".members"))
                if(uuid.equals(player.getUniqueId().toString()))
                    return true;
        return false;
    }

    public String getClan(OfflinePlayer player) {
        if(!isInClan(player)) return null;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        for(String str : cfg.getKeys(false))
            for(String uuid : (ArrayList<String>) plugin.getDataManager().getConfig().get("clans." + str + ".members"))
                if(uuid.equals(player.getUniqueId().toString()))
                    return str;
        return null;
    }

    public String getRole(OfflinePlayer player) {
        if(!isInClan(player)) return null;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        if(player.getUniqueId().toString().equals(cfg.get(getClan(player) + ".leader").toString()))
            return "leader";
        for(String uuid : (ArrayList<String>) cfg.get(getClan(player) + ".trusted"))
            if(uuid.equals(player.getUniqueId().toString()))
                return "trusted";
        for(String uuid : (ArrayList<String>) cfg.get(getClan(player) + ".members"))
            if(uuid.equals(player.getUniqueId().toString()))
                return "member";
        return null;
    }

    //setter
    public void addRole(OfflinePlayer player, String role) {
        if(!isInClan(player)) return;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        String clanName = plugin.getClanManager().getClan(player);
        if(role.equalsIgnoreCase("leader"))
            cfg.set(clanName + ".leader", player.getUniqueId().toString());
        else if(role.equalsIgnoreCase("trusted")) {
            ArrayList<String> trusted = new ArrayList<>(cfg.getStringList(clanName + ".trusted"));
            trusted.add(player.getUniqueId().toString());
            cfg.set(clanName + ".trusted", trusted);
        }
    }

    public void removeRole(OfflinePlayer player, String role) {
        if(!isInClan(player)) return;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        String clanName = plugin.getClanManager().getClan(player);
        if(role.equalsIgnoreCase("trusted")) {
            ArrayList<String> trusted = new ArrayList<>(cfg.getStringList(clanName + ".trusted"));
            trusted.remove(player.getUniqueId().toString());
            cfg.set(clanName + ".trusted", trusted);
        } else if(role.equalsIgnoreCase("member")) {
            ArrayList<String> trusted = new ArrayList<>(cfg.getStringList(clanName + ".trusted"));
            trusted.remove(player.getUniqueId().toString());
            cfg.set(clanName + ".trusted", trusted);
            ArrayList<String> members = new ArrayList<>(cfg.getStringList(clanName + ".members"));
            members.remove(player.getUniqueId().toString());
            cfg.set(clanName + ".members", members);
        }
    }

    //clan and other methods
    //getter
    public ArrayList<String> getClanList() {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        return new ArrayList<>(cfg.getKeys(false));
    }

    public ChatColor getColor(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        String colorStr = cfg.getString("color");
        return ColorUtil.colorFromString(colorStr);
    }

    public String getCode(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return (String) cfg.get("code");
    }

    public ItemStack getBanner(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return cfg.getItemStack("banner");
    }

    public ArrayList<String> getMembers(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return new ArrayList<String>(cfg.getStringList("members"));
    }

    public String getCreated(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return cfg.getString("created");
    }

    public String getRelationshipBetween(String clanName, String otherClanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> map = (HashMap<String, String>) cfg.get("relationships");
        assert map != null;
        if(!map.containsKey(otherClanName)) return "none";
        return map.get(otherClanName);
    }

    //setter
    public void setColor(String clanName, String color) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);

        cfg.set("color", color);

        plugin.getDataManager().saveConfig();
    }

    public void setBanner(Player p) {
        if(!p.getInventory().getItemInMainHand().toString().toLowerCase().contains("banner")) {
            p.sendMessage(TextUtil.convertColor("You must be holding a banner in your main hand."));
            return;
        }
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." +
                plugin.getClanManager().getClan(p));

        cfg.set("banner", p.getInventory().getItemInMainHand());

        plugin.getDataManager().saveConfig();
    }

    public void setRelationship(String clanName, String otherClanName, String relation) {
        if(!getClanList().contains(otherClanName)) return;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> map = new HashMap<>((HashMap<String, String>) cfg.get("relationships"));
        map.put(otherClanName, relation);
        cfg.set("relationships", map);
        ConfigurationSection cfg2 = plugin.getDataManager().getConfig().getConfigurationSection("clans." + otherClanName);
        HashMap<String, String> map2 = new HashMap<>((HashMap<String, String>) cfg2.get("relationships"));
        map.put(clanName, relation);
        cfg2.set("relationships", map2);
        plugin.getDataManager().saveConfig();
    }

    public void removeRelationship(String clanName, String otherClanName) {
        if(!getClanList().contains(otherClanName)) return;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> map = new HashMap<>((HashMap<String, String>) cfg.get("relationships"));
        map.remove(otherClanName);
        cfg.set("relationships", map);
        ConfigurationSection cfg2 = plugin.getDataManager().getConfig().getConfigurationSection("clans." + otherClanName);
        HashMap<String, String> map2 = new HashMap<>((HashMap<String, String>) cfg2.get("relationships"));
        map.remove(otherClanName);
        cfg2.set("relationships", map2);
        plugin.getDataManager().saveConfig();
    }

}
