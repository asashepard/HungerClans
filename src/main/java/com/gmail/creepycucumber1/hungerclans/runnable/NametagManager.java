/*
 * Copyright 2021 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.runnable;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

public class NametagManager {

    private HungerClans plugin;

    public NametagManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void nametagSlow() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                for(Player p : Bukkit.getOnlinePlayers()) {

                    updateNametag(p);

                }

            }
        }, 0, 400);

    }

    public void nametagCheck() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                if(!plugin.getConfigManager().getConfig().getBoolean("boolean.nametags")) {

                    for(Player p : Bukkit.getOnlinePlayers()) {

                        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.nametags")) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                    "nte player " + p.getName() + " suffix ''");
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                    "nte player " + p.getName() + " prefix ''");
                            return;
                        }

                    }

                }
            }
        }, 0, 100);

    }

    public void updateNametag(Player p) {

        if(!plugin.getConfigManager().getConfig().getBoolean("boolean.nametags")) return;

        boolean isAdmin = p.hasPermission("hungercore.adminmode");

        //suffix
        if(isAdmin)
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "nte player " + p.getName() + " suffix ' &4⚡'");
        else
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "nte player " + p.getName() + " suffix ''");

        //prefix
        if(plugin.getClanManager().isInClan(p)) {
            String clanName = plugin.getClanManager().getClan(p);
            String clanCode = plugin.getClanManager().getCode(clanName);
            ChatColor color = plugin.getClanManager().getColor(clanName);
            boolean isLeader = plugin.getClanManager().getRole(p).equalsIgnoreCase("leader");
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "nte player " + p.getName() + " prefix '" + color +
                             clanCode.toUpperCase() + ChatColor.of(new Color(70, 70, 70)) + (isLeader ? " ♠" : " |") + " &f'");
        } else
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "nte player " + p.getName() + " prefix ''");

    }

}
