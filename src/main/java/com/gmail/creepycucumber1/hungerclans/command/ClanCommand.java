package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.gui.ClanColorGUI;
import com.gmail.creepycucumber1.hungerclans.gui.ClanDashboardGUI;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ClanCommand extends CommandBase {
    public ClanCommand(HungerClans plugin) {
        super(plugin, "clan", "Manage clans", "", "c");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;
        boolean inClan = plugin.getClanManager().isInClan(player);
        Map<String, Integer> roleMap = new HashMap<>();
        roleMap.put("leader", 3);
        roleMap.put("trusted", 2);
        roleMap.put("member", 1);

        if(args.length == 0) {
            if(inClan)
                plugin.getGUIManager().openGUI(player, new ClanDashboardGUI(plugin, player));
            else
                player.sendMessage(TextUtil.convertColor("You are NOT in a clan. Use &o/c create [name] &rto make one or" +
                        " Use &o/c help &rto view commands!"));

        }
        else if(args[0].equalsIgnoreCase("help")) {
            player.sendMessage(TextUtil.convertColor("&lClan Commands: &r\n" +
                    " - &o/create [name] &r&7| create a new clan&r\n" +
                    " - &o/list &r&7| list existing clans&r\n" +
                    " - &o/members [clan name] &r&7| list members of a clan&r\n" +
                    " - &o/color &r&7| change your clan's color&r\n" +
                    " - &o/banner &r&7| set clan banner to held banner&r\n" +
                    " - &o/[promote/demote/kick/leave] &r&7| manage members&r")
        }
        else if(args[0].equalsIgnoreCase("create")) {
            //make new clan, add to data file
            if(args.length < 2) {
                player.sendMessage(TextUtil.convertColor("Usage: &o/c create [name]"));
                return true;
            }
            //check if name already exists, is too short or long, etc.
            String clanName = "";
            for(int i = 1; i < args.length; i++)
                clanName += args[i] + " ";
            clanName = clanName.substring(0, clanName.length() - 1);

            plugin.getClanManager().createNewClan(player, clanName);
        }
        else if(args[0].equalsIgnoreCase("list")) {
            //list clans
            player.sendMessage(TextUtil.convertColor("&lClan List:"));
            ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
            for(String str : cfg.getKeys(false)) { //clans
                ChatColor color = ColorUtil.colorFromString(cfg.getString(str + ".color"));
                player.sendMessage(" - " + color + str);
            }
        }
        else if(args[0].equalsIgnoreCase("members")) {
            //list members
            if(args.length > 1) {
                String clanName = "";
                for(int i = 1; i < args.length; i++)
                    clanName += args[i] + " ";
                clanName = clanName.substring(0, clanName.length() - 1);
                String clan = clanName;
                if(!plugin.getClanManager().getClanList().contains(clanName)) {
                    player.sendMessage("There is no clan matching that name.");
                    return true;
                }
                player.sendMessage(TextUtil.convertColor("&oMembers of " +
                        ColorUtil.colorToStringCode(plugin.getClanManager().getColor(clan)) + clan + "&f:"));
                ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clan);
                for(String uuid : (ArrayList<String>) cfg.get("members"))
                    player.sendMessage(" - " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ChatColor.GRAY +
                            " [" + plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(uuid))) + "]");
                return true;
            }
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String clan = plugin.getClanManager().getClan(player);
            player.sendMessage(ChatColor.BOLD + "Members of " + plugin.getClanManager().getColor(clan) + clan + ChatColor.WHITE + ":");
            ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clan);
            for(String uuid : (ArrayList<String>) cfg.get("members"))
                player.sendMessage(" - " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ChatColor.GRAY +
                        " [" + plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(uuid))) + "]");
        }
        else if(args[0].equalsIgnoreCase("color")) {
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String role = plugin.getClanManager().getRole(player);
            if(role.equalsIgnoreCase("trusted") || role.equalsIgnoreCase("leader")) {

                plugin.getGUIManager().openGUI(player, new ClanColorGUI(plugin, player));
                return true;

            } else {
                player.sendMessage("You need to be trusted or the clan leader to change the clan color.");
                return true;
            }
        }
        else if(args[0].equalsIgnoreCase("banner")) {
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String role = plugin.getClanManager().getRole(player);
            if(role.equalsIgnoreCase("leader")) {

                ItemStack before = plugin.getClanManager().getBanner(plugin.getClanManager().getClan(player));
                plugin.getClanManager().setBanner(player);
                if(before.equals(plugin.getClanManager().getBanner(plugin.getClanManager().getClan(player))))
                    player.sendMessage(TextUtil.convertColor("Successfully changed your clan banner!"));

            } else {
                player.sendMessage("You need to be the clan leader to change the clan banner.");
                return true;
            }
        }
        else if(args[0].equalsIgnoreCase("promote")) {
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            if(args.length != 2) {
                player.sendMessage("Please specify one player to promote.");
                return true;
            }
            if(!plugin.getClanManager().getMembers(clanName).contains(UUID.fromString(args[1]).toString())) {
                player.sendMessage("That player isn't in your clan!");
            }
            String senderRole = plugin.getClanManager().getRole(player);
            String receiverRole = plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])));
            if(roleMap.get(senderRole) <= roleMap.get(receiverRole)) {
                player.sendMessage("That player is of equal or greater rank!");
                return true;
            }

            if(receiverRole.equals("member"))
                plugin.getClanManager().addRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "trusted");
            if(receiverRole.equals("trusted"))
                plugin.getClanManager().addRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "leader");
        }
        else if(args[0].equalsIgnoreCase("demote")) {
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            if(args.length != 2) {
                player.sendMessage("Please specify one player to promote.");
                return true;
            }
            if(!plugin.getClanManager().getMembers(clanName).contains(UUID.fromString(args[1]).toString())) {
                player.sendMessage("That player isn't in your clan!");
            }
            String senderRole = plugin.getClanManager().getRole(player);
            String receiverRole = plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])));
            if(roleMap.get(senderRole) <= roleMap.get(receiverRole)) {
                player.sendMessage("That player is of equal or greater rank!");
                return true;
            }

            if(receiverRole.equals("member"))
                plugin.getClanManager().removeRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "member");
            if(receiverRole.equals("trusted"))
                plugin.getClanManager().removeRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "trusted");
        }
        else if(args[0].equalsIgnoreCase("kick")) {
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            if(args.length != 2) {
                player.sendMessage("Please specify one player to promote.");
                return true;
            }
            if(!plugin.getClanManager().getMembers(clanName).contains(UUID.fromString(args[1]).toString())) {
                player.sendMessage("That player isn't in your clan!");
            }
            String senderRole = plugin.getClanManager().getRole(player);
            String receiverRole = plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])));
            if(roleMap.get(senderRole) <= roleMap.get(receiverRole)) {
                player.sendMessage("That player is of equal or greater rank!");
                return true;
            }

            if(receiverRole.equals("member"))
                plugin.getClanManager().removeRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "member");
            if(receiverRole.equals("trusted")) {
                plugin.getClanManager().removeRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "trusted");
                plugin.getClanManager().removeRole(Bukkit.getOfflinePlayer(UUID.fromString(args[1])), "member");
            }
        }
        else if(args[0].equalsIgnoreCase("leave")) {
            if(!inClan) {
                player.sendMessage("You aren't in a clan!");
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            String senderRole = plugin.getClanManager().getRole(player);
            int members = plugin.getClanManager().getMembers(clanName).size();
            if(senderRole.equalsIgnoreCase("leader")) {
                if(members != 1)
                    player.sendMessage("You can't leave a clan where you are leader: promote someone first!");
                else {
                    player.sendMessage("You have left the clan. The clan has been disbanded.");
                    plugin.getDataManager().getConfig().set("clans." + clanName, null); //todo make "active" boolean under clan?
                    plugin.getDataManager().saveConfig();
                }
                return true;
            }
            else if(senderRole.equalsIgnoreCase("trusted")) {
                plugin.getClanManager().removeRole(player, "trusted");
                plugin.getClanManager().removeRole(player, "member");
            }
            else
                plugin.getClanManager().removeRole(player, "member");
        }

        //invite <leader or trusted>
        //chat [on/off]
        //war <leader or trusted>

        return true;
    }

}
