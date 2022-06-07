package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class AllClansGUI extends GUI {
    private Player player;

    public AllClansGUI(HungerClans plugin, Player player) {
        super(plugin, player.getUniqueId(), "§lClan Explorer", (plugin.getClanManager().getClanList().size() > 9 ? 2 : 1));
        this.player = player;

        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("clans");
        ArrayList<String> clansList = new ArrayList<>(cfg.getKeys(false));
        for(int i = 0; i < clansList.size(); i++) {
            String clanName = clansList.get(i);
            ItemStack banner = plugin.getClanManager().getBanner(clanName);
            ItemMeta bannerMeta = banner.getItemMeta();
            bannerMeta.setDisplayName(TextUtil.convertColor(plugin.getClanManager().getColor(clanName) + "&l" + clanName +
                    "&r" + plugin.getClanManager().getColor(clanName) +
                    " [" + plugin.getClanManager().getCode(clanName) + "]"));
            ArrayList<String> bannerLore = new ArrayList<>();
            if(plugin.getClanManager().getMotto(clanName).length() > 0)
                bannerLore.add(TextUtil.convertColor("&f&o\"" + plugin.getClanManager().getMotto(clanName) + "\""));
            bannerLore.add(TextUtil.convertColor("&7 - Points: &e" + plugin.getClanManager().getPoints(clanName)));
            int online = 0;
            ArrayList<String> members = plugin.getClanManager().getMembers(clanName);
            for(String member : members)
                if(Bukkit.getOfflinePlayer(UUID.fromString(member)).isOnline()) online++;
            bannerLore.add(TextUtil.convertColor("&7 - Members: &f" + members.size() + " &a(" + online + " online)"));
            bannerLore.add(TextUtil.convertColor("&7 - Created: &f" + plugin.getClanManager().getCreated(clanName)));
            bannerMeta.setLore(bannerLore);
            banner.setItemMeta(bannerMeta);
            items[i] = new GUIItem(banner, clanName);
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
        } else {
            plugin.getGUIManager().openGUI(p, new OtherClanGUI(plugin, p, item.getItemId()));
        }
    }

}
