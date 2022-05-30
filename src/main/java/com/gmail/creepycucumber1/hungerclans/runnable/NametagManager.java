/*
 * Copyright 2021 Gabriel Keller
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.gmail.creepycucumber1.hungerclans.runnable;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class NametagManager {

    private HungerClans plugin;

    public NametagManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    public void nametag() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {

                    if(!plugin.getConfigManager().getConfig().getBoolean("boolean.nametags")) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                "nte player " + p.getName() + " suffix ''");
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                "nte player " + p.getName() + " prefix ''");
                        return;
                    }
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
                        String clanCode = plugin.getClanManager().getCode(plugin.getClanManager().getClan(p));
                        ChatColor color = plugin.getClanManager().getColor(plugin.getClanManager().getClan(p));
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                "nte player " + p.getName() + " prefix '" + ColorUtil.colorToStringCode(color) +
                                        "[" + clanCode.toUpperCase() + "] &f'");
                    } else
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                "nte player " + p.getName() + " prefix ''");

                }
            }
        }, 0, 40);

    }

}
