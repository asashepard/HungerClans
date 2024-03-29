package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SurrenderCommand extends CommandBase {
    public SurrenderCommand(HungerClans plugin) {
        super(plugin, "surrender", "Lose more quickly", "", "");
    }

    String PASSWORD = "uAr9bP?2cvmhJ=Gshungaemcee";

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.allowSurrender")) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cSurrendering has been turned off."));
            return true;
        }
        if(!plugin.getClanManager().isInClan(player)) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be part of a clan to use this command!"));
            return true;
        }
        String clan = plugin.getClanManager().getClan(player);
        if(!plugin.getWarManager().isInWar(clan)) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYour clan is not currently at war!"));
            return true;
        }
        if(!plugin.getClanManager().getRole(player).equalsIgnoreCase("leader")) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be the clan leader to use this command."));
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(TextUtil.convertColor("&7Usage: &o/surrender [opponent]"));
            return true;
        }
        String otherClanDisplay = args[0];
        if(!plugin.getClanManager().getClanList().contains(otherClanDisplay)) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cThat clan (case sensitive) does not exist."));
            return true;
        }
        String otherClan = plugin.getClanManager().getClanName(otherClanDisplay);
        ChatColor color = plugin.getClanManager().getColor(otherClan);
        if(!plugin.getWarManager().areAtWar(clan, otherClan)) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYour clan is not at war with " + color + otherClan + "&c."));
            return true;
        }
        String war = otherClanDisplay + "vs" + plugin.getClanManager().getDisplayName(clan);

        if(!plugin.getWarManager().getWars(clan).contains(war)) { //clan of sender declared war because the order would be reversed
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &4&oYou &r&4declared war against " + color + otherClanDisplay + "&r&4, no?"));
            return true;
        }
        int surrenderWait = plugin.getConfigManager().getConfig().getInt("integer.surrenderWait");
        int warLength = plugin.getConfigManager().getConfig().getInt("integer.warLength");
        int okToSurrender = Integer.parseInt(plugin.getWarManager().getEndDay(war)) - (warLength - surrenderWait);
        if(okToSurrender > getToday()) { //war less than X day(s) old
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &7Please wait until the &f" +
                     okToSurrender + TextUtil.getSuffix(String.valueOf(okToSurrender)) + "&7 to surrender to " + color + otherClanDisplay + "&7."));
            return true;
        }
        if(args.length == 2 && args[1].equalsIgnoreCase(PASSWORD)) { //ENDS WAR
            plugin.getWarManager().endWar(war, otherClan, true);
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3You have surrendered to " + color + otherClanDisplay + "&3."));
            return true;
        }
        if(plugin.getWarManager().getSide1Points(war) < plugin.getWarManager().getSide2Points(war)) { //clan of sender is winning
            sendClickableCommand(player, TextUtil.convertColor("&4&lCLANS &8» &4You are in the lead! Are you sure you want to surrender to " + color + otherClanDisplay + "&4 and lose the war? Click here to confirm."),
                    "/c surrender " + PASSWORD, TextUtil.convertColor("Surrender to " + color + otherClanDisplay));
            return true;
        }

        sendClickableCommand(player, TextUtil.convertColor("&4&lCLANS &8» &4Are you sure you want to surrender to " + color + otherClanDisplay + "&4 and lose the war? Click here to confirm."),
                "/c surrender " + PASSWORD, TextUtil.convertColor("Surrender to " + color + otherClanDisplay));

        return true;
    }

    public int getToday() {
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat dayFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dayFormat.format(currentDate));
    }

    public void sendClickableCommand(Player player, String message, String command, String hover) {
        TextComponent component = new TextComponent(TextUtil.convertColor(message));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(component);
    }

}
