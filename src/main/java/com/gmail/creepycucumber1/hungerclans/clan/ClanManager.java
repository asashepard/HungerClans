package com.gmail.creepycucumber1.hungerclans.clan;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClanManager {

    private HungerClans plugin;

    private ArrayList<String> clansThisStartup;

    public ClanManager(HungerClans plugin) {
        this.plugin = plugin;
        clansThisStartup = new ArrayList<>(getClanList());
    }

    public void createNewClan(Player player, String clanName) {
        if(isInClan(player)) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou are already in a clan!")); return;
        }
        int cost = plugin.getConfigManager().getConfig().getInt("integer.createClanCost");
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        if(cost != 0 && plugin.getVault().getBalance(player) < cost) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must have $" + cost + " to create a clan."));
            return;
        }
        for(String existingName : cfg.getKeys(false))
            if(existingName.toLowerCase().contains(clanName.toLowerCase()) ||
                    clanName.toLowerCase().contains(existingName.toLowerCase()) ||
                    clanName.substring(0, 4).equalsIgnoreCase(existingName.substring(0, 4))) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat clan name is too similar to another!"));
                return;
            }
        if(stripSpaces(clanName).length() < 4) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat clan name is too short!"));
            return;
        }
        if(stripSpaces(clanName).length() > 18) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat clan name is too long!"));
            return;
        }
        if(!clanName.matches("^[A-Za-z]+$")) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cA clan name must only have letters!"));
            return;
        }
        List<String> bannedNames = new ArrayList<>(List.of("join", "leave", "gabe", "hayes", "xarkenz", "longbread", "nigg", "fag",
                "cunt", "burn jews", "fuck", "shit", "clan", "none", "amogus"));
        bannedNames.addAll(TextUtil.blockedWords);
        for(String str : bannedNames)
            if(clanName.toLowerCase().contains(str)) {
                Bukkit.getLogger().info(player.getName() + "'s clan name blocked: " + clanName);
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease think of something more creative, you swine."));
                return;
            }

        plugin.getDataManager().getConfig().createSection("clans." + clanName);
        clansThisStartup.add(clanName);

        //create map with info and add to data file section
        //clan code
        String clanCode = getClanCode(clanName);
        //clan color
        List<ChatColor> COLORS = List.of(ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.AQUA,
                ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.BLUE, ChatColor.DARK_GREEN, ChatColor.RED);
        String clanColor = ColorUtil.colorToString(COLORS.get((int) (Math.random() * COLORS.size())));
        //clan motto
        String motto = "";
        //roles
        ArrayList<String> members = new ArrayList<>(); //unique IDs
        members.add(player.getUniqueId().toString());
        ArrayList<String> trusted = new ArrayList<>(); //unique IDs
        trusted.add(player.getUniqueId().toString());
        String leader = player.getUniqueId().toString(); //unique ID
        //banner
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        //creation date
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String created = dateFormat.format(date);
        //points
        int points = 1000;
        //clan home
        ArrayList<String> home = new ArrayList<>();
        //blocked from war declaration
        HashMap<String, String> noDeclareMap = new HashMap<>();
        List<HashMap<String, String>> noDeclare = List.of(noDeclareMap);

        Map<String, Object> map = new HashMap<>();
        map.put("code", clanCode);
        map.put("color", clanColor);
        map.put("motto", motto);
        map.put("members", members);
        map.put("trusted", trusted);
        map.put("leader", leader);
        map.put("banner", banner);
        map.put("created", created);
        map.put("points", points);
        map.put("home", home);
        map.put("hasHome", false);
        map.put("noDeclare", noDeclare);

        plugin.getDataManager().getConfig().createSection("clans." + clanName, map);
        plugin.getDataManager().saveConfig();

        plugin.getPlayerManager().setJoinedClanToNow(player);
        plugin.getVault().withdrawPlayer(player, cost);
        player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You have successfully created a clan!"));
        plugin.getDiscordManager().updateUserRoles(player);
    }

    //PLAYER METHODS
    //getter
    public boolean isInClan(OfflinePlayer player) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        for(String clan : cfg.getKeys(false)) {
            for(String uuid : cfg.getStringList(clan + ".members")) {
                if(uuid.equals(player.getUniqueId().toString()))
                    return true;
            }
        }
        return false;
    }

    public String getClan(OfflinePlayer player) {
        if(!isInClan(player)) return "none";
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        for(String str : cfg.getKeys(false))
            for(String uuid : (ArrayList<String>) plugin.getDataManager().getConfig().get("clans." + str + ".members"))
                if(uuid.equals(player.getUniqueId().toString()))
                    return str;
        return "none";
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
    public void addMember(OfflinePlayer player, String clanName) {
        if(isInClan(player)) return;
        if(getMembers(clanName).size() >= 18) return;
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        ArrayList<String> members = new ArrayList<>(cfg.getStringList(clanName + ".members"));
        members.add(player.getUniqueId().toString());

        plugin.getPlayerManager().setJoinedClanToNow(player);

        cfg.set(clanName + ".members", members);
        plugin.getDataManager().saveConfig();
        plugin.getDiscordManager().updateUserRoles(player);
    }

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
        plugin.getDataManager().saveConfig();
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
        plugin.getDataManager().saveConfig();
        plugin.getDiscordManager().updateUserRoles(player);
    }

    //CLAN AND OTHER METHODS
    //getter
    public ArrayList<String> getClanList() {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        return new ArrayList<>(cfg.getKeys(false));
    }

    public ArrayList<String> getClansThisStartup() {
        return clansThisStartup;
    }

    public net.md_5.bungee.api.ChatColor getColor(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        String colorStr = cfg.getString("color");
        return ColorUtil.colorFromString(colorStr);
    }

    public String getCode(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return (String) cfg.get("code");
    }

    public String getMotto(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return (String) cfg.get("motto");
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

    public int getPoints(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return cfg.getInt("points");
    }

    public ArrayList<String> getHome(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return (ArrayList<String>) cfg.getStringList("home");
    }

    public boolean getHasHome(String clanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        return cfg.getBoolean("hasHome");
    }

    public String getDeclareAgainDay(String clanName, String otherClanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> noDeclareMap = new HashMap<>((Map<? extends String, ? extends String>) cfg.getMapList("noDeclare").get(0));
        return noDeclareMap.get(otherClanName);
    }

    public boolean declarationsBlocked(String clanName, String otherClanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> noDeclareMap = new HashMap<>((Map<? extends String, ? extends String>) cfg.getMapList("noDeclare").get(0));
        return noDeclareMap.containsKey(otherClanName);
    }

    //setter
    public void setColor(String clanName, String color) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);

        cfg.set("color", color);

        plugin.getDataManager().saveConfig();
    }

    public void setBanner(Player p) {
        if(!p.getInventory().getItemInMainHand().toString().toLowerCase().contains("banner")) {
            p.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be holding a banner in your main hand."));
            return;
        }
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." +
                plugin.getClanManager().getClan(p));

        ItemStack banner = new ItemStack(p.getInventory().getItemInMainHand());
        ItemMeta meta = banner.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        banner.setItemMeta(meta);

        cfg.set("banner", banner);

        if(p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() &&
                !p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toLowerCase().contains("banner")) {
            List<String> bannedWords = List.of("nigg", "fag", "cunt", "burn jews", "fuck", "shit");
            for(String str : bannedWords)
                if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().toLowerCase().contains(str)) {
                    cfg.set("motto", "We are very bad");
                    plugin.getDataManager().saveConfig();
                    return;
                }
            cfg.set("motto", p.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
        }
        else
            cfg.set("motto", "");

        plugin.getDataManager().saveConfig();
    }

    public void addPoints(String clanName, int points) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        cfg.set("points", cfg.getInt("points") + points);
        plugin.getDataManager().saveConfig();
    }

    public void setHome(String clanName, Location location) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        ArrayList<String> home = new ArrayList<>();
        home.add(location.getWorld().getName());
        home.add(String.valueOf((int) location.getX() + 0.5));
        home.add(String.valueOf((int) location.getY()));
        home.add(String.valueOf((int) location.getZ() + 0.5));
        cfg.set("home", home);
        cfg.set("hasHome", true);
        plugin.getDataManager().saveConfig();
    }

    public void setDeclarationBlock(String clanName, String otherClanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> noDeclareMap = new HashMap<>((Map<? extends String, ? extends String>) cfg.getMapList("noDeclare").get(0));

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
        String day = String.valueOf((currentDay + plugin.getConfigManager().getConfig().getInt("integer.declareWait")) % last);

        noDeclareMap.put(otherClanName, day);
        List<HashMap<String, String>> noDeclare = List.of(noDeclareMap);
        cfg.set("noDeclare", noDeclare);

        plugin.getDataManager().saveConfig();
    }

    public void removeDeclarationBlock(String clanName, String otherClanName) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clanName);
        HashMap<String, String> noDeclareMap = new HashMap<>((Map<? extends String, ? extends String>) cfg.getMapList("noDeclare").get(0));

        noDeclareMap.remove(otherClanName);

        List<HashMap<String, String>> noDeclare = List.of(noDeclareMap);
        cfg.set("noDeclare", noDeclare);

        plugin.getDataManager().saveConfig();
    }

    private String stripSpaces(String str) {
        return str.replaceAll("\\s", "");
    }

    private String stripVowels(String str) {
        return str.replaceAll("[aeiou]", "");
    }

    private String getClanCode(String str) {
        String stripped = stripSpaces(stripVowels(str));
        if(stripped.length() > 4)
            return stripped.substring(0, 4).toUpperCase();
        return stripSpaces(str).substring(0, 4).toUpperCase();
    }

}
