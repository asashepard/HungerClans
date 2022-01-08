package com.gmail.creepycucumber1.hungerclans.event;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EventManager implements Listener {

    private HungerClans plugin;

    public EventManager(HungerClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        boolean res = plugin.getGUIManager().onClick(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), e.getCurrentItem(), e.getView());
        if(res) e.setCancelled(true);
    }

}
