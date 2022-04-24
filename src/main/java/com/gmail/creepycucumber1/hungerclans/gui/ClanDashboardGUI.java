package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

public class ClanDashboardGUI extends GUI {
    private Player player;

    public ClanDashboardGUI(HungerClans plugin, Player player) {
        super(plugin, player.getUniqueId(), ColorUtil.colorToStringCode(plugin.getClanManager().getColor(plugin.getClanManager().getClan(player))) + "&lClan Dashboard", 4);
        this.player = player;
        String clanName = plugin.getClanManager().getClan(player);
        ChatColor color = plugin.getClanManager().getColor(clanName);
        String colorCode = ColorUtil.colorToStringCode(color);
        String role = plugin.getClanManager().getRole(player);

        ItemStack banner = plugin.getClanManager().getBanner(clanName);
        ItemMeta bannerMeta = banner.getItemMeta();
        bannerMeta.setDisplayName(TextUtil.convertColor(colorCode + "&l" + clanName + " &r" + colorCode + "[" + plugin.getClanManager().getCode(clanName) + "]"));
        ArrayList<String> bannerLore = new ArrayList<>();
        if(plugin.getClanManager().getMotto(clanName).length() > 0)
            bannerLore.add(TextUtil.convertColor("&f&o\"" + plugin.getClanManager().getMotto(clanName) + "&f&o\""));
        bannerMeta.setLore(bannerLore);
        banner.setItemMeta(bannerMeta);
        items[13] = new GUIItem(banner, "banner");

        ItemStack viewInfo = ItemUtil.createItemStack(Material.PAPER, "&7Members: &f" + plugin.getClanManager().getMembers(clanName).size(),
                "&7Points: &e" + plugin.getClanManager().getPoints(clanName),
                "&7Color: " + color + ColorUtil.colorToString(color),
                "&7Created: &f" + plugin.getClanManager().getCreated(clanName));
        ItemStack changeColor = ItemUtil.createItemStack(Material.PAINTING, TextUtil.toRainbow("Change Clan Color"));
        if(role.equalsIgnoreCase("trusted") || role.equalsIgnoreCase("leader")) {
            items[10] = new GUIItem(changeColor, "color");
            items[0] = new GUIItem(viewInfo, "info");
        } else {
            items[10] = new GUIItem(viewInfo, "info");
        }

        ItemStack help = ItemUtil.createItemStack(Material.COMPASS, "&7Help");
        items[8] = new GUIItem(help, "help");

        ItemStack viewClans = ItemUtil.createItemStack(Material.SPYGLASS, "&7View All Clans");
        items[16] = new GUIItem(viewClans, "clans");

        for(int i = 0; i < plugin.getClanManager().getMembers(clanName).size(); i++) {
            String uuid = plugin.getClanManager().getMembers(clanName).get(i);
            ItemStack skull = getSkull(uuid);
            int slot = 27 + i;
            items[slot] = new GUIItem(skull, uuid);
        }

        ItemStack grayPane = ItemUtil.createItemStack(Material.GRAY_STAINED_GLASS_PANE, " ");
        GUIItem gray = new GUIItem(grayPane, "pane");
        for(int i = 0; i <= 26; i += 2)
            if(items[i] == null)
                items[i] = gray;

        ItemStack warPane = ItemUtil.createItemStack((plugin.getWarManager().isInWar(clanName) ? Material.BLACK_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE), " ");
        GUIItem wPane = new GUIItem(warPane, "pane");
        for(int i = 1; i <= 26; i += 2)
            if(i != 13)
                items[i] = wPane;

        ItemStack clanPane = ItemUtil.createItemStack(ColorUtil.colorToGlass(plugin.getClanManager().getColor(clanName)).getType(), " ");
        GUIItem cPane = new GUIItem(clanPane, "pane");
        items[4] = cPane; items[12] = cPane; items[14] = cPane; items[22] = cPane;

    }

    public ItemStack getSkull(String uuid) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(TextUtil.convertColor("&2" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + " &7[" +
                plugin.getClanManager().getRole(Bukkit.getOfflinePlayer(UUID.fromString(uuid))) + "]"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(TextUtil.convertColor("&7Joined: &f" + plugin.getPlayerManager().getJoinedClan(Bukkit.getOfflinePlayer(UUID.fromString(uuid)))));
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
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
                item.getItemId().equalsIgnoreCase("pane") ||
                item.getItemId().equalsIgnoreCase("banner")) {
            // do nothing
        }
        else { //skull
            p.closeInventory();
            plugin.getGUIManager().openGUI(p, new PlayerGUI(plugin, p, item.getId()));
        }

    }

}
