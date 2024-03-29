package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class PlaceTopCommand extends CommandBase {
    public PlaceTopCommand(HungerClans plugin) {
        super(plugin, "placetop", "Get a leaderboard of the players on the server with the most blocks placed", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;

        HashMap<Double, String> map = new HashMap<>(); //time played, player name
        double placed;
        long total = 0;
        for(String uuidString : plugin.getDataManager().getConfig().getConfigurationSection("players").getKeys(false)) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
            placed = plugin.getPlayerManager().getBlocksPlaced(p);
            total += placed;
            while(map.containsKey(placed)) placed += Math.random();
            map.put(placed, p.getName());
        }
        ArrayList<Double> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        Collections.reverse(list);

        int page = 0;
        if(args.length > 0)
            try {
                page = Math.abs(Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {}
        if(page > ((list.size() - 1) / 10) + 1) {
            player.sendMessage(TextUtil.convertColor("&cThere aren't yet " + page + " pages of this " +
                    (((list.size() - 1) / 10) + 1) + "-page leaderboard!"));
            return true;
        }
        if(page == 0) page = 1;
        int index = (page - 1) * 10;

        player.sendMessage(TextUtil.convertColor("&2&lLeaderboard:&r&2 Blocks Placed " +
                "&7(page " + page + " of " + (((list.size() - 1) / 10) + 1) + ")\n"));
        DecimalFormat d = new DecimalFormat("###,###,###,###");
        if(page == 1) player.sendMessage(TextUtil.convertColor("&7&oServer total: " + d.format(total) + " blocks"));
        for(int i = index; i < index + 10; i++) {
            if(i > list.size() - 1) return true;
            player.sendMessage(TextUtil.convertColor("&a" + (i + 1) + ". &7" + map.get(list.get(i)) + "&8 - &f" + d.format((int) Math.floor(list.get(i))) + " blocks"));
        }

        return true;
    }
}
