package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
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
            sender.sendMessage(TextUtil.convertColor("&cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;
        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.allowSurrender")) {
            player.sendMessage(TextUtil.convertColor("&cSurrendering has been turned off."));
            return true;
        }
        if(!plugin.getClanManager().isInClan(player)) {
            player.sendMessage(TextUtil.convertColor("&cYou must be part of a clan to use this command!"));
            return true;
        }
        String clan = plugin.getClanManager().getClan(player);
        if(!plugin.getWarManager().isInWar(clan)) {
            player.sendMessage(TextUtil.convertColor("&cYour clan is not currently at war!"));
            return true;
        }
        if(!plugin.getClanManager().getRole(player).equalsIgnoreCase("leader")) {
            player.sendMessage(TextUtil.convertColor("&cYou must be the clan leader to use this command."));
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(TextUtil.convertColor("&cUsage: &o/surrender [opponent]"));
            return true;
        }
        String otherClan = args[1];
        if(!plugin.getDataManager().getConfig().getConfigurationSection("clans").getKeys(false).contains(otherClan)) {
            player.sendMessage(TextUtil.convertColor("&cThat clan (case sensitive) does not exist."));
            return true;
        }
        String color = ColorUtil.colorToStringCode(plugin.getClanManager().getColor(otherClan));
        if(!plugin.getWarManager().areAtWar(clan, otherClan)) {
            player.sendMessage(TextUtil.convertColor("&cYour clan is not at war with " + color + otherClan + "."));
            return true;
        }
        String war = otherClan + "vs" + clan;

        if(!plugin.getWarManager().getWars(clan).contains(war)) { //clan of sender declared war
            player.sendMessage(TextUtil.convertColor("&c&oYou &r&cdeclared war against " + color + otherClan + ", &r&cno?"));
            return true;
        }
        int surrenderWait = plugin.getConfigManager().getConfig().getInt("integer.surrenderWait");
        int warLength = plugin.getConfigManager().getConfig().getInt("integer.warLength");
        int okToSurrender = Integer.parseInt(plugin.getWarManager().getEndDay(war)) - (warLength - surrenderWait);
        if(okToSurrender > getToday()) { //war less than X day(s) old
            player.sendMessage(TextUtil.convertColor("&cPlease wait until the " +
                     okToSurrender + TextUtil.getSuffix(String.valueOf(okToSurrender)) + " to surrender to " + color + otherClan + "."));
            return true;
        }
        if(args.length == 2 && args[1].equalsIgnoreCase(PASSWORD)) { //ENDS WAR
            plugin.getWarManager().endWar(war, otherClan, true);
            player.sendMessage(TextUtil.convertColor("&3You have surrendered to " + color + otherClan + "&3."));
            return true;
        }
        if(plugin.getWarManager().getSide1Points(war) < plugin.getWarManager().getSide2Points(war)) { //clan of sender is winning
            sendClickableCommand(player, TextUtil.convertColor("&4You are in the lead! Are you sure you want to surrender to " + color + otherClan + " and lose the war? Click here to confirm."),
                    "/c surrender " + PASSWORD, TextUtil.convertColor("Surrender to " + color + otherClan));
            return true;
        }

        sendClickableCommand(player, TextUtil.convertColor("&4Are you sure you want to surrender to " + color + otherClan + " and lose the war? Click here to confirm."),
                "/c surrender " + PASSWORD, TextUtil.convertColor("Surrender to " + color + otherClan));

        return true;
    }

    public int getToday() {
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat dayFormat = new SimpleDateFormat("dd");
        DateFormat monthFormat = new SimpleDateFormat("MM");
        int currentDay = Integer.parseInt(dayFormat.format(currentDate));
        return currentDay;
    }

    public void sendClickableCommand(Player player, String message, String command, String hover) {
        TextComponent component = new TextComponent(TextUtil.convertColor(message));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(component);
    }

}
