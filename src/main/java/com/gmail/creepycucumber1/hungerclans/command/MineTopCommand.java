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

public class MineTopCommand extends CommandBase {
    public MineTopCommand(HungerClans plugin) {
        super(plugin, "minetop", "Get a leaderboard of the players on the server with the most blocks mined", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;

        HashMap<Integer, String> map = new HashMap<>(); //time played, player name
        int mined;
        long total = 0;
        for(String uuidString : plugin.getDataManager().getConfig().getConfigurationSection("players").getKeys(false)) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
            mined = plugin.getPlayerManager().getBlocksMined(player);
            map.put(mined, p.getName());
            total += mined;
        }
        ArrayList<Integer> list = new ArrayList<>(map.keySet());
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

        player.sendMessage(TextUtil.convertColor("&2&lLeaderboard:&r&2 Blocks Mined " +
                "&7(page " + page + " of " + (((list.size() - 1) / 10) + 1) + ")\n"));
        DecimalFormat d = new DecimalFormat("###,###,###,###");
        if(page == 1) player.sendMessage(TextUtil.convertColor("&7&oServer total: " + d.format(total) + " blocks"));
        for(int i = index; i < index + 10; i++) {
            if(i > list.size() - 1) return true;
            player.sendMessage(TextUtil.convertColor("&a" + (i + 1) + ". &7" + map.get(list.get(i)) + "&8 - &f" + d.format(list.get(i)) + " blocks"));
        }

        return true;
    }
}
