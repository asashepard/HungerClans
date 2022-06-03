package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkDiscordCommand extends CommandBase {
    public LinkDiscordCommand(HungerClans plugin) {
        super(plugin, "linkdiscord", "Link a discord account", "[discord tag]", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 0) {
            if(plugin.getPlayerManager().getDiscordID(player).equalsIgnoreCase("none")) return false;
            else player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You are linked with Discord account " +
                    plugin.getPlayerManager().getDiscordID(player)));
        }

        if(player.hasPermission("hungercore.staff") && args.length > 0) {
            if(args[0].equalsIgnoreCase("edit")) {
                if(args.length == 3) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                    plugin.getPlayerManager().setDiscordID(p, args[2]);
                    player.sendMessage("Change made.");
                    plugin.getDiscordManager().updateUserRoles(p);
                    return true;
                }
                else if(args.length == 2) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                    player.sendMessage("Roles updated.");
                    plugin.getDiscordManager().updateUserRoles(p);
                    return true;
                }
                else {
                    player.sendMessage("Usage: /linkdiscord edit [player] [discord ID]");
                    return true;
                }
            }
        }

        if(!(plugin.getPlayerManager().getDiscordID(player).equals("none"))) { //already linked account
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou already have linked the discord account " +
                    plugin.getPlayerManager().getDiscordID(player) +
                    ". Contact an administrator if there is an issue."));
            return true;
        }

        String tag = args[0];
        if(tag.split("#").length != 2 || tag.split("#")[1].length() != 4) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cPlease enter a valid discord tag such as example#0123"));
            return true;
        }

        long discordID;
        try {
            discordID = DiscordUtil.getJda().getUserByTag(tag).getIdLong();
        } catch (Exception exception) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat discord tag was not recognized. Is the account in the server?"));
            return true;
        }

        for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            String otherDiscordID = plugin.getPlayerManager().getDiscordID(op);
            if(String.valueOf(discordID).equals(otherDiscordID)) {
                player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cAnother player has already linked to that discord account."));
                return true;
            }
        }

        sendClickableCommand(player,
                TextUtil.convertColor("&4&lCLANS &8» &3Click here to confirm account link with " + tag + "."),
                "/linkdiscord " + tag + " confirm",
                "Confirm (Irreversible)");

        if(args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            plugin.getPlayerManager().setDiscordID(player, String.valueOf(discordID));
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Discord account " + tag + " linked!"));
            plugin.getDiscordManager().updateUserRoles(player);
        }

        return true;
    }

    public void sendClickableCommand(Player player, String message, String command, String hover) {
        TextComponent component = new TextComponent(TextUtil.convertColor(message));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(component);
    }

}
