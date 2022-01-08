package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OtherClanGUI extends GUI {
    private Player player;

    public OtherClanGUI(HungerClans plugin, Player player, String clanName) {
        super(plugin, player.getUniqueId(), "Other Clan", 1);
        this.player = player;

        //todo
    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
        if(item.getItemId().equalsIgnoreCase("peace")) {
            //todo
        } else if(item.getItemId().equalsIgnoreCase("war")) {
            //todo
        }
        p.closeInventory();
    }
}
