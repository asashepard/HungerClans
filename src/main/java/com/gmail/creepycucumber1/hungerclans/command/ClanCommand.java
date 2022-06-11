package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.gui.ClanColorGUI;
import com.gmail.creepycucumber1.hungerclans.gui.ClanDashboardGUI;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClanCommand extends CommandBase {
    public ClanCommand(HungerClans plugin) {
        super(plugin, "clan", "Manage clans", "", "c");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.clans")) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cClans have not been enabled."));
            return true;
        }

        boolean inClan = plugin.getClanManager().isInClan(player);
        Map<String, Integer> roleMap = new HashMap<>();
        roleMap.put("leader", 3);
        roleMap.put("trusted", 2);
        roleMap.put("member", 1);
        String PASSWORD = "uAr9bP?2cvmhJ=Gshungaemcee";

        if(args.length == 0) {
            if(inClan)
                plugin.getGUIManager().openGUI(player, new ClanDashboardGUI(plugin, player));
            else
                sendClickableCommand(player, TextUtil.convertColor("&4&lCLANS &8» &cYou are not in a clan, so you don't have a dashboard to view." +
                        " Use &o/c create [clan name] &r&cto make one or use &o/c help &r&cto view commands."), "/c help", "Clans Help");

        }
        else if(args[0].equalsIgnoreCase("help")) {
            player.sendMessage(TextUtil.convertColor("&4&lClan Commands: &r\n" +
                    " &8- &r&o/c create [clan name] &r&7| create a new clan&r\n" +
                    " &8- &r&o/c list &r&7| list existing clans&r\n" +
                    " &8- &r&o/c members [clan name] &r&7| list members of a clan&r\n" +
                    " &8- &r&o/c color &r&7| change your clan's color&r\n" +
                    " &8- &r&o/c banner &r&7| set clan banner to held banner&r\n" +
                    " &8- &r&o/c [promote/demote/kick/invite] [player] &r&7| manage&r\n" +
                    " &8- &r&o/c leave &r&7| leave clan&r\n" +
                    " &8- &r&o/c [home/sethome] &r&7| teleport to a shared home&r\n" +
                    " &8- &r&o/[cw/cmsg] &r&7| privately message clan members&r\n" +
                    " &8- &r&o/c trust &r&7| trust clan members in a claim&r"));
        } //all
        else if(args[0].equalsIgnoreCase("create")) {
            //make new clan, add to data file
            if(args.length < 2) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &7Usage: &o/c create [name]"));
                return true;
            }
            if(!player.hasPermission("hungerclans.createclan")) {
                sendClickableLink(player, TextUtil.convertColor("&4&lCLANS &8» &cYou do not have permission to create a clan. " +
                                "Contact a server administrator if you believe this is in error, or click here."),
                        "shop.hungermc.com", "Go to shop.hungermc.com");
                return true;
            }

            String clanName = "";
            for(int i = 1; i < args.length; i++)
                clanName += args[i] + " ";
            clanName = clanName.substring(0, clanName.length() - 1);

            plugin.getClanManager().createNewClan(player, clanName);
        } //supporter
        else if(args[0].equalsIgnoreCase("list")) {
            //list clans
            player.sendMessage(TextUtil.convertColor("&4&lClan List:"));
            ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
            for(String str : cfg.getKeys(false)) { //clans
                net.md_5.bungee.api.ChatColor color = ColorUtil.colorFromString(cfg.getString(str + ".color"));
                player.sendMessage(TextUtil.convertColor(" &7- " + color + str + " [" + cfg.getString(str + ".code") + "]"));
            }
        } //all
        else if(args[0].equalsIgnoreCase("members")) {
            //list members
            if(args.length > 1) {
                String clanName = "";
                for(int i = 1; i < args.length; i++)
                    clanName += args[i] + " ";
                clanName = clanName.substring(0, clanName.length() - 1);
                String clan = clanName;

                boolean match = false;
                for(String str : plugin.getClanManager().getClanList())
                    if(str.equalsIgnoreCase(clan)) {
                        match = true;
                        break;
                    }
                if(!match) {
                    player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThere is no clan with the name \"&f" + clan + "&7\"."));
                    return true;
                }

                player.sendMessage(TextUtil.convertColor("&3&lMembers of " +
                        plugin.getClanManager().getColor(clan) + "&l" + clan + "&3:"));
                ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clan);
                for(String uuid : (ArrayList<String>) cfg.get("members"))
                    player.sendMessage(TextUtil.convertColor(" &7- &f" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() +
                            " &7[" + plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(uuid))) + "]"));
                return true;
            }
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clan = plugin.getClanManager().getClan(player);
            player.sendMessage(TextUtil.convertColor("&3&lMembers of " +
                    plugin.getClanManager().getColor(clan) + "&l" +  clan + "&3:"));
            ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans." + clan);
            for(String uuid : (ArrayList<String>) cfg.get("members"))
                player.sendMessage(TextUtil.convertColor(" &7- &f" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() +
                        " &7[" + plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(uuid))) + "]"));
        } //all
        else if(args[0].equalsIgnoreCase("color")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String role = plugin.getClanManager().getRole(player);
            if(role.equalsIgnoreCase("trusted") || role.equalsIgnoreCase("leader")) {

                if(args.length == 2 && args[1].startsWith("#")) { //hex code
                    String hexCode = args[1];
                    Pattern pattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                    Matcher matcher = pattern.matcher(hexCode);
                    if(!matcher.find()) {
                        player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease use a valid HEX code prefixed with #!\n" +
                                "Or type /c color without anything to access a simple GUI!"));
                        return true;
                    }
                    String clanName = plugin.getClanManager().getClan(player);
                    plugin.getClanManager().setColor(clanName, hexCode);
                    player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Successfully set your clan color to " +
                            ChatColor.of(hexCode) + hexCode + "&3!"));

                    int cost = plugin.getConfigManager().getConfig().getInt("integer.setColorCost");
                    plugin.getVault().withdrawPlayer(player, cost);

                    return true;
                }

                plugin.getGUIManager().openGUI(player, new ClanColorGUI(plugin, player));

            } else {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be trusted or the clan leader to change the clan color."));
            }
            return true;
        } //trusted/leader
        else if(args[0].equalsIgnoreCase("banner")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            if(!plugin.getClanManager().getRole(player).equalsIgnoreCase("leader")) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be the clan leader to change your clan banner."));
                return true;
            }
            int cost = plugin.getConfigManager().getConfig().getInt("integer.setBannerCost");
            if(cost != 0 && plugin.getVault().getBalance(player) < cost) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must have &f$" + cost + " &cto set your clan banner."));
                return true;
            }

            plugin.getClanManager().setBanner(player);
            plugin.getVault().withdrawPlayer(player, cost);
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Successfully changed your clan banner!"));
            if(cost != 0) player.sendMessage(TextUtil.convertColor("&7Balance: &f$" + plugin.getVault().getBalance(player)));

        } //leader
        else if(args[0].equalsIgnoreCase("promote")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            net.md_5.bungee.api.ChatColor color = plugin.getClanManager().getColor(clanName);
            if(args.length != 2) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease specify a player to promote."));
                return true;
            }
            try {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            } catch (Exception e) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThere exists no player of that name."));
                return true;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if(!plugin.getClanManager().getMembers(clanName).contains(p.getUniqueId().toString())) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player isn't in your clan!"));
            }
            String senderRole = plugin.getClanManager().getRole(player);
            String receiverRole = plugin.getClanManager().getRole(p);
            if(roleMap.get(senderRole) <= roleMap.get(receiverRole)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player is of equal or greater rank!"));
                return true;
            }
            if(receiverRole.equals("member")) {
                plugin.getClanManager().addRole(Bukkit.getOfflinePlayer(args[1]), "trusted");
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Promoted "
                        + Bukkit.getOfflinePlayer(args[1]).getName() +
                        " to &b[trusted]&3."));
                if(p.isOnline())
                    p.getPlayer().sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You have been promoted to &b[trusted] &3in &l" +
                            color + clanName + "&r&3 by &7" + player.getName() + "&3."));
            }
            if(receiverRole.equals("trusted")) {
                plugin.getClanManager().addRole(Bukkit.getOfflinePlayer(args[1]), "leader");
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Promoted &7"
                        + Bukkit.getOfflinePlayer(args[1]).getName() +
                        " &3to &b[leader]&3. You are now &b[trusted]&3."));
                if(p.isOnline())
                    p.getPlayer().sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You have been promoted to &b[leader] &3in &l" +
                            color + clanName + "&r&3 by &7" + player.getName() + "&3."));
            }
        } //higher role
        else if(args[0].equalsIgnoreCase("demote")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            net.md_5.bungee.api.ChatColor color = plugin.getClanManager().getColor(clanName);
            if(args.length != 2) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease specify a player to demote."));
                return true;
            }
            try {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            } catch (Exception e) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThere exists no player of that name."));
                return true;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if(!plugin.getClanManager().getMembers(clanName).contains(p.getUniqueId().toString())) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player isn't in your clan!"));
            }
            String senderRole = plugin.getClanManager().getRole(player);
            String receiverRole = plugin.getClanManager().getRole(p);
            if(roleMap.get(senderRole) <= roleMap.get(receiverRole)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player is of equal or greater rank!"));
                return true;
            }
            if(receiverRole.equals("member")) {
                plugin.getClanManager().removeRole(p, "member");
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Removed the &b[member] &3role from &7"
                        + p.getName() +
                        "&3 and kicked them from the clan."));
                if(p.isOnline())
                    p.getPlayer().sendMessage(TextUtil.convertColor("&4&lCLANS &8» &4You have been kicked from &l" +
                            color + clanName + "&r&4 by &7" + player.getName() + "&4."));
            }
            if(receiverRole.equals("trusted")) {
                plugin.getClanManager().removeRole(p, "trusted");
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Demoted &7" + p.getName() +
                        "&3 to &b[member]&3."));
                if(p.isOnline())
                    p.getPlayer().sendMessage(TextUtil.convertColor("&4&lCLANS &8» &4You have been demoted to &c[member] &4in &l" +
                            color + clanName + "&r&4 by &7" + player.getName()) + "&4.");
            }
        } //higher role
        else if(args[0].equalsIgnoreCase("kick")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            net.md_5.bungee.api.ChatColor color = plugin.getClanManager().getColor(clanName);
            if(args.length != 2) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease specify a player to kick."));
                return true;
            }
            try {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            } catch (Exception e) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThere exists no player of that name."));
                return true;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if(!plugin.getClanManager().getMembers(clanName).contains(p.getUniqueId().toString())) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player isn't in your clan!"));
            }
            String senderRole = plugin.getClanManager().getRole(player);
            String receiverRole = plugin.getClanManager().getRole(p);
            if(roleMap.get(senderRole) <= roleMap.get(receiverRole)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player is of equal or greater rank!"));
                return true;
            }

            if(receiverRole.equals("member"))
                plugin.getClanManager().removeRole(p, "member");
            if(receiverRole.equals("trusted")) {
                plugin.getClanManager().removeRole(p, "trusted");
                plugin.getClanManager().removeRole(p, "member");
            }
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Kicked player &7" + args[1] + "." +
                    "&3 from the clan."));
            if(p.isOnline())
                p.getPlayer().sendMessage(TextUtil.convertColor("&4&lCLANS &8» &4You have been kicked from &l" +
                        color + clanName + "&r&4 by &7" + player.getName() + "&4."));
            plugin.getDiscordManager().updateUserRoles(p);
        } //higher role
        else if(args[0].equalsIgnoreCase("leave")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &7You aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            String senderRole = plugin.getClanManager().getRole(player);

            if(args.length == 2 && args[1].equalsIgnoreCase(PASSWORD)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You have left the clan."));
                if(senderRole.equalsIgnoreCase("leader")) {
                    plugin.getDataManager().getConfig().set("clans." + clanName, null);
                    plugin.getDataManager().saveConfig();
                    player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3The clan has been disbanded."));
                }
                else if(senderRole.equalsIgnoreCase("trusted")) {
                    plugin.getClanManager().removeRole(player, "trusted");
                    plugin.getClanManager().removeRole(player, "member");
                }
                else if(senderRole.equalsIgnoreCase("member")) {
                    plugin.getClanManager().removeRole(player, "member");
                }
                plugin.getPlayerManager().removeJoinedClan(player);
                plugin.getDiscordManager().updateUserRoles(player);
                return true;
            }

            if(senderRole.equalsIgnoreCase("leader")) {
                int members = plugin.getClanManager().getMembers(clanName).size();
                if(members != 1)
                    player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou can't leave a clan where you are leader: promote someone first!"));
                else
                    sendClickableCommand(player, TextUtil.convertColor("&4&lCLANS &8» &4Are you sure you want to go? The clan will be disbanded. Click here to confirm."),
                            "/c leave " + PASSWORD, "Leave Clan");
                return true;
            }
            sendClickableCommand(player, TextUtil.convertColor("&4&lCLANS &8» &4Are you sure you want to go? &rClick here to confirm."),
                    "/c leave " + PASSWORD, "Leave Clan");
            return true;

        } //member/trusted/leader (CONSOLE)
        else if(args[0].equalsIgnoreCase("invite")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            net.md_5.bungee.api.ChatColor color = plugin.getClanManager().getColor(clanName);
            String role = plugin.getClanManager().getRole(player);
            if(role.equalsIgnoreCase("member")) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be trusted or the clan leader to send an invite."));
                return true;
            }
            if(plugin.getClanManager().getMembers(clanName).size() >= 18) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cClans have a maximum size of 18 members. Kick someone or complain to an admin."));
                return true;
            }
            if(args.length != 2) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease specify a player to invite."));
                return true;
            }
            try {
                OfflinePlayer invited = Bukkit.getOfflinePlayer(args[1]);
            } catch (Exception e) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThere exists no player of that name."));
                return true;
            }
            OfflinePlayer invited = Bukkit.getOfflinePlayer(args[1]);
            if(!invited.isOnline()) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease specify one online player to invite."));
                return true;
            }
            if(plugin.getClanManager().isInClan(invited) && plugin.getClanManager().getClan(invited).equals(plugin.getClanManager().getClan(player))) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat player is already part of your clan."));
                return true;
            }
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Invite sent to &7" + invited.getName() + "&3."));
            sendClickableCommand(invited.getPlayer(),
                    "&4&lCLANS &8» &3You have been invited to &l" + color + clanName + "&r&b - click to accept.",
                    "/c join " + clanName + " " + PASSWORD,
                    "Join Clan");

        } //trusted/leader
        else if(args[0].equalsIgnoreCase("join")) {
            if(!args[2].equalsIgnoreCase(PASSWORD)) return true; //secret password
            if(plugin.getClanManager().isInClan(player)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou are already in a clan! Leave to join another."));
                return true;
            }
            net.md_5.bungee.api.ChatColor color = plugin.getClanManager().getColor(args[1]);
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You are now part of " + color + args[1] + "&3!"));
            plugin.getClanManager().addMember(player, args[1]);
        } //CONSOLE
        else if(args[0].equalsIgnoreCase("home")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            if(!plugin.getClanManager().getHasHome(clanName)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYour clan doesn't have a home - ask your leader to set one."));
                return true;
            }
            World world = Bukkit.getWorld(plugin.getClanManager().getHome(clanName).get(0));
            double x = Double.parseDouble(plugin.getClanManager().getHome(clanName).get(1));
            double y = Double.parseDouble(plugin.getClanManager().getHome(clanName).get(2));
            double z = Double.parseDouble(plugin.getClanManager().getHome(clanName).get(3));
            plugin.getTeleporter().teleport(player, world, x, y, z);
            return true;
        } //member/trusted/leader
        else if(args[0].equalsIgnoreCase("sethome")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            if(!plugin.getClanManager().getRole(player).equalsIgnoreCase("leader")) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be the clan leader to set the clan home."));
                return true;
            }
            int cost = plugin.getConfigManager().getConfig().getInt("integer.setHomeCost");
            if(cost != 0 && plugin.getVault().getBalance(player) < cost) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must have &f$" + cost + "&c to set your clan home."));
                return true;
            }

            plugin.getClanManager().setHome(clanName, player.getLocation());
            plugin.getVault().withdrawPlayer(player, cost);
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Successfully set the clan home to your location."));
            if(cost != 0) player.sendMessage(TextUtil.convertColor("&7Balance: &f$" + plugin.getVault().getBalance(player)));
            return true;
        } //leader
        else if(args[0].equalsIgnoreCase("trust") || args[0].equalsIgnoreCase("t")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            Bukkit.getServer().dispatchCommand(player, "trust clan " + plugin.getClanManager().getClan(player));
        } //claim owner
        else if(args[0].equalsIgnoreCase("untrust")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            Bukkit.getServer().dispatchCommand(player, "untrust clan");
        } //claim owner
        else if(args[0].equalsIgnoreCase("war")) {
            if(!inClan) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou aren't in a clan!"));
                return true;
            }
            String clanName = plugin.getClanManager().getClan(player);
            if(!plugin.getClanManager().getRole(player).equalsIgnoreCase("leader")) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be the clan leader to change war acceptance status."));
                return true;
            }
            plugin.getClanManager().toggleAcceptingWar(clanName);
            if(plugin.getClanManager().getAcceptingWar(clanName))
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Your clan is now a potential participant in new wars."));
            else
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Your clan is now no longer a potential participant in new wars."));
        } //leader
        else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            String clanName = "none";
            try {
                clanName = plugin.getClanManager().getClan(p);
            }
            catch (Exception ignored) {}
            if(clanName.equalsIgnoreCase("none")) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &7Player " + args[0] + " isn't in a clan."));
                return true;
            }
            ChatColor color = plugin.getClanManager().getColor(clanName);
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &7Player &a" + args[0] + "&7 is of the clan " + color + clanName + "&7."));
            sendClickableCommand(player, TextUtil.convertColor("&7Click here to view all members of the clan."),
                    "/c members " + clanName,
                    "Members of " + clanName);
            return true;
        }

        return true;
    }

    public void sendClickableCommand(Player player, String message, String command, String hover) {
        TextComponent component = new TextComponent(TextUtil.convertColor(message));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(component);
    }

    public void sendClickableLink(Player player, String message, String link, String hover) {
        TextComponent component = new TextComponent(TextUtil.convertColor(message));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        player.spigot().sendMessage(component);
    }

}
