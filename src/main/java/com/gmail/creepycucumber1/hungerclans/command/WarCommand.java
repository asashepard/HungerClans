package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarCommand extends CommandBase {
    public WarCommand(HungerClans plugin) {
        super(plugin, "war", "View war(s)", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 0) {
            if(!plugin.getClanManager().isInClan(player)) {
                player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYou aren't in a clan! Use &o/war all&r&c to see all current wars."));
                return true;
            }
            if(!plugin.getWarManager().isInWar(plugin.getClanManager().getClan(player))) {
                player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYour clan is not currently at war. Use &o/war all&r&c to see all current wars."));
                return true;
            }

            String clanName = plugin.getClanManager().getClan(player);

            for(String war : plugin.getWarManager().getWars(clanName))
                sendWarMessage(player, war, true);

            return true;
        }
        else if(args[0].equalsIgnoreCase("all")) {
            for (String war : plugin.getDataManager().getConfig().getConfigurationSection("wars").getKeys(false)) {
                sendWarMessage(player, war, false);
            }
            if(plugin.getDataManager().getConfig().getConfigurationSection("wars").getKeys(false).size() == 0) {
                player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cThere are currently no active wars."));
            }
            return true;
        }

        return true;
    }

    public void sendWarMessage(Player player, String war, boolean includesPlayer) {
        String side1 = plugin.getWarManager().getSide1(war);
        String side2 = plugin.getWarManager().getSide2(war);

        String you1 = "";
        String you2 = "";
        if(includesPlayer) {
            if(plugin.getWarManager().isSide1(war, plugin.getClanManager().getClan(player)))
                you1 = TextUtil.convertColor(" &3(you)");
            else
                you2 = TextUtil.convertColor(" &3(you)");
        }

        player.sendMessage(TextUtil.convertColor("&3&l> WAR: " + ColorUtil.colorToStringCode(plugin.getClanManager().getColor(side1)) + side1 + you1 +
                " &3vs " + ColorUtil.colorToStringCode(plugin.getClanManager().getColor(side2)) + side2 + you2 + "&3&l <"));
        player.sendMessage(TextUtil.convertColor("&7 - Points for " + ColorUtil.colorToStringCode(plugin.getClanManager().getColor(side1)) + side1 +
                ": &e" + plugin.getWarManager().getSide1Points(war)));
        player.sendMessage(TextUtil.convertColor("&7 - Points for " + ColorUtil.colorToStringCode(plugin.getClanManager().getColor(side2)) + side2 +
                ": &e" + plugin.getWarManager().getSide2Points(war)));

        String ends = plugin.getWarManager().getEndDay(war);
        String suffix = TextUtil.getSuffix(ends);

        player.sendMessage(TextUtil.convertColor("&7This war ends on the &f" + ends + suffix + "&7."));
    }
}
