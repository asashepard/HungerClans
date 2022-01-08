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
        super(plugin, player.getUniqueId(), "Clan Color Editor", 1);
        this.player = player;

        ItemStack gray = ItemUtil.createItemStack(Material.GRAY_WOOL, "&8Gray");
        items[0] = new ColorItem(gray, ChatColor.DARK_GRAY, "gray");
        ItemStack green = ItemUtil.createItemStack(Material.LIME_WOOL, "&aGreen");
        items[1] = new ColorItem(green, ChatColor.GREEN, "green");
        ItemStack yellow = ItemUtil.createItemStack(Material.YELLOW_WOOL, "&eYellow");
        items[2] = new ColorItem(yellow, ChatColor.YELLOW, "yellow");
        ItemStack aqua = ItemUtil.createItemStack(Material.LIGHT_BLUE_WOOL, "&bAqua");
        items[3] = new ColorItem(aqua, ChatColor.AQUA, "aqua");
        ItemStack magenta = ItemUtil.createItemStack(Material.MAGENTA_WOOL, "&dMagenta");
        items[4] = new ColorItem(magenta, ChatColor.LIGHT_PURPLE, "magenta");
        ItemStack orange = ItemUtil.createItemStack(Material.ORANGE_WOOL, "&6Orange");
        items[5] = new ColorItem(orange, ChatColor.GOLD, "orange");
        ItemStack blue = ItemUtil.createItemStack(Material.BLUE_WOOL, "&9Blue");
        items[6] = new ColorItem(blue, ChatColor.BLUE, "blue");
        ItemStack darkGreen = ItemUtil.createItemStack(Material.GREEN_WOOL, "&2Dark Green");
        items[7] = new ColorItem(darkGreen, ChatColor.DARK_GREEN, "darkGreen");
        ItemStack red = ItemUtil.createItemStack(Material.RED_WOOL, "&cRed");
        items[8] = new ColorItem(red, ChatColor.RED, "red");
    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
        String clanName = plugin.getClanManager().getClan(p);
        plugin.getClanManager().setColor(clanName, item.getId());
        player.sendMessage(TextUtil.convertColor("Successfully set your clan color to " +
                ColorUtil.colorToStringCode(ColorUtil.colorFromString(item.getId())) + item.getId() + "!"));
        player.closeInventory();
    }

}
