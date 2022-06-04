package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClanColorGUI extends GUI {
    private Player player;

    public ClanColorGUI(HungerClans plugin, Player player) {
        super(plugin, player.getUniqueId(), "§lClan Color Editor" +
                (plugin.getConfigManager().getConfig().getInt("integer.setColorCost") != 0 ? " - $" + plugin.getConfigManager().getConfig().getInt("integer.setColorCost") : ""), 2);
        this.player = player;

        ItemStack lightGray = ItemUtil.createItemStack(Material.LIGHT_GRAY_WOOL, "&7Light Gray");
        items[0] = new ColorItem(lightGray, ChatColor.GRAY.asBungee(), "lightGray");
        ItemStack gray = ItemUtil.createItemStack(Material.GRAY_WOOL, "&8Gray");
        items[1] = new ColorItem(gray, ChatColor.DARK_GRAY.asBungee(), "gray");
        ItemStack pink = ItemUtil.createItemStack(Material.PINK_WOOL, ColorUtil.getColor("#fb91ff") + "Pink");
        items[2] = new ColorItem(pink, net.md_5.bungee.api.ChatColor.of("#fb91ff"), "pink");
        ItemStack green = ItemUtil.createItemStack(Material.LIME_WOOL, "&aGreen");
        items[3] = new ColorItem(green, ChatColor.GREEN.asBungee(), "green");
        ItemStack yellow = ItemUtil.createItemStack(Material.YELLOW_WOOL, "&eYellow");
        items[4] = new ColorItem(yellow, ChatColor.YELLOW.asBungee(), "yellow");
        ItemStack aqua = ItemUtil.createItemStack(Material.LIGHT_BLUE_WOOL, "&bAqua");
        items[5] = new ColorItem(aqua, ChatColor.AQUA.asBungee(), "aqua");
        ItemStack magenta = ItemUtil.createItemStack(Material.MAGENTA_WOOL, "&dMagenta");
        items[6] = new ColorItem(magenta, ChatColor.LIGHT_PURPLE.asBungee(), "magenta");
        ItemStack orange = ItemUtil.createItemStack(Material.ORANGE_WOOL, "&6Orange");
        items[7] = new ColorItem(orange, ChatColor.GOLD.asBungee(), "orange");
        ItemStack white = ItemUtil.createItemStack(Material.WHITE_WOOL, "&fWhite");
        items[8] = new ColorItem(white, ChatColor.WHITE.asBungee(), "white");
        ItemStack cyan = ItemUtil.createItemStack(Material.CYAN_WOOL, "&3Cyan");
        items[9] = new ColorItem(cyan, ChatColor.DARK_AQUA.asBungee(), "cyan");
        ItemStack purple = ItemUtil.createItemStack(Material.PURPLE_WOOL, "&5Purple");
        items[10] = new ColorItem(purple, ChatColor.DARK_PURPLE.asBungee(), "purple");
        ItemStack blue = ItemUtil.createItemStack(Material.BLUE_WOOL, "&9Blue");
        items[11] = new ColorItem(blue, ChatColor.BLUE.asBungee(), "blue");
        ItemStack brown = ItemUtil.createItemStack(Material.BROWN_WOOL, ColorUtil.getColor("#8f7257") + "Brown");
        items[12] = new ColorItem(brown, net.md_5.bungee.api.ChatColor.of("#8f7257"), "brown");
        ItemStack darkGreen = ItemUtil.createItemStack(Material.GREEN_WOOL, "&2Dark Green");
        items[13] = new ColorItem(darkGreen, ChatColor.DARK_GREEN.asBungee(), "darkGreen");
        ItemStack red = ItemUtil.createItemStack(Material.RED_WOOL, "&cRed");
        items[14] = new ColorItem(red, ChatColor.RED.asBungee(), "red");
        ItemStack black = ItemUtil.createItemStack(Material.BLACK_WOOL, "&0Black");
        items[15] = new ColorItem(black, ChatColor.BLACK.asBungee(), "black");
        ItemStack custom = ItemUtil.createItemStack(Material.PAPER, "&fCustom");
        items[17] = new ColorItem(custom, "custom");
    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
        String clanName = plugin.getClanManager().getClan(p);

        int cost = plugin.getConfigManager().getConfig().getInt("integer.setColorCost");
        if(cost != 0 && plugin.getVault().getBalance(player) < cost) {
            player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &cYou must have $" + cost + " to set your clan color."));
            return;
        }

        if(item.getId().equals("custom")){
            player.sendMessage(TextUtil.convertColor("&7Use &a/c color <HEX code> &7to change your clan color!"));
            player.closeInventory();
            return;
        }

        plugin.getClanManager().setColor(clanName, item.getId());
        player.sendMessage(TextUtil.convertColor("&4&lCLANS &8» &3Successfully set your clan color to ") +
                ColorUtil.colorFromString(item.getId()) + item.getId() + "!");
        plugin.getVault().withdrawPlayer(p, cost);
        if(cost != 0) player.sendMessage(TextUtil.convertColor("&7Balance: $" + plugin.getVault().getBalance(p)));
        player.closeInventory();
    }

}
