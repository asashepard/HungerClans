package com.gmail.creepycucumber1.hungerclans.runnable;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Teleport {

    private HungerClans plugin;

    public Teleport(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player player, World world, int x, int y, int z) {

        Location startPos = player.getLocation();
        Location telePos = new Location(world, (double) x, (double) y, (double) z);

        player.sendMessage(TextUtil.convertColor("&3Teleportation will begin in 5 seconds..."));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            public void run() {

                Location endPos = player.getLocation();
                if(!(endPos.distance(startPos) < 2))
                    player.sendMessage(TextUtil.convertColor("&cYou moved. Teleportation has been canceled."));
                else
                    player.teleport(telePos);

            }
        }, 100);

    }
}
