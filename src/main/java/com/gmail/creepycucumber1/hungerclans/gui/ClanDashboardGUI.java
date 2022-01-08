package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

public class ClanDashboardGUI extends GUI {
    private Player player;

    public ClanDashboardGUI(HungerClans plugin, Player player) {
        super(plugin, player.getUniqueId(), "&lClan Dashboard", 4);
        this.player = player;
        String clanName = plugin.getClanManager().getClan(player);
        ChatColor color = plugin.getClanManager().getColor(clanName);
        String role = plugin.getClanManager().getRole(player);

        ItemStack banner = ItemUtil.createItemStack(plugin.getClanManager().getBanner(clanName).getType(), color + "&l" + clanName);
        items[13] = new GUIItem(banner, "banner");

        ItemStack grayPane = ItemUtil.createItemStack(Material.GRAY_STAINED_GLASS_PANE, "");
        GUIItem gray = new GUIItem(grayPane, "gray");
        items[4] = gray; items[11] = gray; items[12] = gray; items[14] = gray; items[15] = gray; items[22] = gray;

        ItemStack viewInfo = ItemUtil.createItemStack(Material.PAPER, "&7Members: &f" + plugin.getClanManager().getMembers(clanName).size(),
                "&7Color: " + color + ColorUtil.colorToString(color),
                "&7Created: &f" + plugin.getClanManager().getCreated(clanName));
        ItemStack changeColor = ItemUtil.createItemStack(Material.PAINTING, "&6Change &3Clan &2Color");
        if(role.equalsIgnoreCase("trusted") || role.equalsIgnoreCase("leader")) {
            items[10] = new GUIItem(changeColor, "color");
            items[0] = new GUIItem(viewInfo, "info");
        } else {
            items[10] = new GUIItem(viewInfo, "info");
        }

        ItemStack help = ItemUtil.createItemStack(Material.COMPASS, "&7Command Help");
        items[8] = new GUIItem(help, "help");

        ItemStack viewClans = ItemUtil.createItemStack(Material.SPYGLASS, "&7View All Clans");
        items[16] = new GUIItem(viewClans, "clans");

        int firstSlot = (plugin.getClanManager().getMembers(clanName).size() > 9 ? 18 : 27);
        for(int i = 0; i < plugin.getClanManager().getMembers(clanName).size(); i++) {
            boolean jump = firstSlot == 18 && i > 4;
            String uuid = plugin.getClanManager().getMembers(clanName).get(i);
            ItemStack skull = getSkull(uuid);
            int slot = firstSlot + i;
            if(jump) slot++;
            items[slot] = new GUIItem(skull, uuid);
        }

    }

    public ItemStack getSkull(String uuid) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(TextUtil.convertColor("&2" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + " &7[" +
                plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(uuid))) + "]"));
        skull.setItemMeta(skullMeta);
        return skull;
    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
        String clanName = plugin.getClanManager().getClan(p);
        if(item.getItemId().equalsIgnoreCase("color")) {
            p.closeInventory();
            plugin.getGUIManager().openGUI(p, new ClanColorGUI(plugin, p));
        }
        else if(item.getItemId().equalsIgnoreCase("clans")) {
            p.closeInventory();
            plugin.getGUIManager().openGUI(p, new AllClansGUI(plugin, p));
        }
        else if(item.getItemId().equalsIgnoreCase("help")) {
            p.closeInventory();
            plugin.getServer().dispatchCommand(p, "c help");
        }
        else if(item.getItemId().equalsIgnoreCase("info") ||
                item.getItemId().equalsIgnoreCase("gray")) {
            // do nothing
        }
        else { //skull
            p.closeInventory();
            plugin.getGUIManager().openGUI(p, new PlayerGUI(plugin, p, item.getId()));
        }

    }

}
