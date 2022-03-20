package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class PlayerGUI extends GUI {
    private Player player;

    public PlayerGUI(HungerClans plugin, Player player, String uuid) {
        super(plugin, player.getUniqueId(), "§lClan Member", 1);
        this.player = player;

        ItemStack skull = getSkull(uuid);
        items[4] = new GUIItem(skull, "skull");

        ItemStack grayPane = ItemUtil.createItemStack(Material.GRAY_STAINED_GLASS_PANE, " ");
        GUIItem gray = new GUIItem(grayPane, "pane");
        items[2] = gray; items[6] = gray; items[0] = gray; items[8] = gray;

        ItemStack whitePane = ItemUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, " ");
        GUIItem white = new GUIItem(whitePane, "pane");
        items[3] = white; items[5] = white;

        ItemStack green = ItemUtil.createItemStack(Material.GREEN_CONCRETE, "&2Promote",
                "&7if leader: promotes trusted to leader,",
                "&7leader is demoted");
        items[1] = new GUIItem(green, uuid);

        ItemStack red = ItemUtil.createItemStack(Material.RED_CONCRETE, "&4Demote/Kick");
        items[7] = new GUIItem(red, uuid);


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
        if(item.getItem().getType().equals(Material.GREEN_CONCRETE)) {
            p.closeInventory();
            Bukkit.getServer().dispatchCommand(p, "c promote " + Bukkit.getOfflinePlayer(UUID.fromString(item.getId())).getName());
        } else if(item.getItem().getType().equals(Material.RED_CONCRETE)) {
            p.closeInventory();
            Bukkit.getServer().dispatchCommand(p, "c demote " + Bukkit.getOfflinePlayer(UUID.fromString(item.getId())).getName());
        }
    }

}
