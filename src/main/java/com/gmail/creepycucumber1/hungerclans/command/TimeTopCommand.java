package com.gmail.creepycucumber1.hungerclans.command;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class TimeTopCommand extends CommandBase {
    public TimeTopCommand(HungerClans plugin) {
        super(plugin, "timetop", "Get a leaderboard of the longest-time-played players on the server", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.convertColor("&4CLANS &8» &cYou must be a player to use this command!"));
            return true;
        }
        Player player = (Player) sender;

        HashMap<Long, String> map = new HashMap<>(); //time played, player name
        long time;
        for(String uuidString : plugin.getDataManager().getConfig().getConfigurationSection("players").getKeys(false)) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
            time = plugin.getPlayerManager().getTotalTimePlayed(p);
            map.put(time, p.getName());
        }
        ArrayList<Long> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        Collections.reverse(list);

        int page = 0;
        if(args.length > 0)
            try {
                page = Math.abs(Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {}
        if(page > ((list.size() - 1) / 10) + 1) {
            player.sendMessage(TextUtil.convertColor("&4CLANS &8» &cThere aren't yet " + page + " pages of this " +
                    (((list.size() - 1) / 10) + 1) + "-page leaderboard!"));
            return true;
        }
        if(page == 0) page = 1;
        int index = (page - 1) * 10;

        player.sendMessage(TextUtil.convertColor("&2&lLeaderboard:&r&2 Time Played " +
                "&7(page " + page + " of " + (((list.size() - 1) / 10) + 1) + ")\n"));
        for(int i = index; i < index + 10; i++) {
            if(i > list.size() - 1) return true;
            player.sendMessage(TextUtil.convertColor("&a" + (i + 1) + ". &f" + map.get(list.get(i)) +
                    " &7(tier " + getLevel(list.get(i)) + ")"));
        }

        return true;
    }

    private int getLevel(long time) {
        if(time >= 4320000000L) return 7;
        else if(time >= 2160000000L) return 6;
        else if(time >= 864000000L) return 5;
        else if(time >= 432000000L) return 4;
        else if(time >= 86400000L) return 3;
        else if(time >= 3600000L) return 2;
        return 1;
    }
}
