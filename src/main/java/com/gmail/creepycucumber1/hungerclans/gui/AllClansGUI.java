package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AllClansGUI extends GUI {
    private Player player;

    public AllClansGUI(HungerClans plugin, Player player) {
        super(plugin, player.getUniqueId(), "Clan Explorer", 2);
        this.player = player;

        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        ArrayList<String> clansList = new ArrayList<>(cfg.getKeys(false));
        for(int i = 0; i < clansList.size(); i++) {
            String clanName = clansList.get(i);
            ItemStack clan = ItemUtil.createItemStack(plugin.getClanManager().getBanner(clanName).getType(),
                    plugin.getClanManager().getColor(clanName) + clanName,
                    "&7Members: &f" + plugin.getClanManager().getMembers(clanName).size(),
                    "&7Members: &f" + plugin.getClanManager().getCreated(clanName));
            items[i] = new GUIItem(clan, clanName);
        }
    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
        p.closeInventory();
        if(item.getItemId().equalsIgnoreCase(plugin.getClanManager().getClan(p))) {
            plugin.getGUIManager().openGUI(p, new ClanDashboardGUI(plugin, p));
        }
        plugin.getGUIManager().openGUI(p, new OtherClanGUI(plugin, p, item.getItemId()));
    }

}
