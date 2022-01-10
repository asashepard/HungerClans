package com.gmail.creepycucumber1.hungerclans.gui;

import com.gmail.creepycucumber1.hungerclans.HungerClans;
import com.gmail.creepycucumber1.hungerclans.util.ColorUtil;
import com.gmail.creepycucumber1.hungerclans.util.ItemUtil;
import com.gmail.creepycucumber1.hungerclans.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class OtherClanGUI extends GUI {
    private Player player;

    public OtherClanGUI(HungerClans plugin, Player player, String clanName) {
        super(plugin, player.getUniqueId(), "Other Clan", 1);
        this.player = player;
        ChatColor color = plugin.getClanManager().getColor(clanName);
        String colorCode = ColorUtil.colorToStringCode(color);
        int members = plugin.getClanManager().getMembers(clanName).size();

        ItemStack banner = plugin.getClanManager().getBanner(clanName);
        ItemMeta bannerMeta = banner.getItemMeta();
        bannerMeta.setDisplayName(TextUtil.convertColor(colorCode + "&l" + clanName + " &r" + colorCode + "[" + plugin.getClanManager().getCode(clanName) + "]"));
        ArrayList<String> bannerLore = new ArrayList<>();
        bannerLore.add(TextUtil.convertColor("&7 - Members: &f" + members));
        if(plugin.getClanManager().getMotto(clanName).length() > 0)
            bannerLore.add(TextUtil.convertColor("&f&o\"" + plugin.getClanManager().getMotto(clanName) + "\""));
        bannerMeta.setLore(bannerLore);
        banner.setItemMeta(bannerMeta);
        items[4] = new GUIItem(banner, clanName);

        ItemStack grayPane = ItemUtil.createItemStack(Material.GRAY_STAINED_GLASS_PANE, "");
        GUIItem gray = new GUIItem(grayPane, "pane");
        items[3] = gray; items[5] = gray; items[0] = gray; items[8] = gray;

        ItemStack whitePane = ItemUtil.createItemStack(Material.WHITE_STAINED_GLASS_PANE, "");
        GUIItem white = new GUIItem(whitePane, "pane");
        items[2] = white; items[6] = white;

        ItemStack green = ItemUtil.createItemStack(Material.GREEN_CONCRETE, "&2Peace");
        items[1] = new GUIItem(green, clanName);

        ItemStack red = ItemUtil.createItemStack(Material.RED_CONCRETE, "&4Declare War &f- $5000");
        items[7] = new GUIItem(red, clanName);

    }

    @Override
    public void open() {
        player.openInventory(createInventory());
    }

    @Override
    public void clicked(Player p, GUIItem item) {
        if(item.getItem().getType().toString().toLowerCase().contains("banner")) {
            p.closeInventory();
            plugin.getServer().dispatchCommand(p, "c members " + item.getItemId());
        } else if(item.getItem().getType().equals(Material.GREEN_CONCRETE)) {
            p.closeInventory();
            if(plugin.getWarManager().areAtWar(plugin.getClanManager().getClan(p), item.getItemId())) {
                p.sendMessage(TextUtil.convertColor("&cPeace? But you are at war! Peace was never an option."));
                return;
            }
            plugin.getGUIManager().openGUI(p, new AllClansGUI(plugin, p));
        } else if(item.getItem().getType().equals(Material.RED_CONCRETE)) {
            p.closeInventory();
            if(!plugin.getClanManager().getRole(p).equalsIgnoreCase("leader")) {
                p.sendMessage(TextUtil.convertColor("&cYou must be the clan leader to declare war."));
                return;
            }

            if(plugin.getVault().getBalance(p) < 5000) {
                p.sendMessage(TextUtil.convertColor("&cYou don't have $5000 to declare war."));
                return;
            }

            String clan = plugin.getClanManager().getClan(p);
            String otherClan = item.getItemId();

            if(plugin.getWarManager().areAtWar(clan, otherClan)) {
                p.sendMessage(TextUtil.convertColor("&4You are already at war with " +
                        ColorUtil.colorToStringCode(plugin.getClanManager().getColor(otherClan)) +
                        item.getItemId() + "&r&4! Are you winning yet?"));
                return;
            }

            if(plugin.getClanManager().declarationsBlocked(clan, otherClan)) {

                String ends = plugin.getClanManager().getDeclareAgainDay(clan, otherClan);
                String suffix = TextUtil.getSuffix(ends);

                p.sendMessage(TextUtil.convertColor("&cPlease wait until the " + ends + suffix + " to declare war against " +
                        ColorUtil.colorToStringCode(plugin.getClanManager().getColor(otherClan)) + otherClan + " &cagain."));
                return;

            }

            plugin.getWarManager().createNewWar(clan, otherClan);
            plugin.getClanManager().setDeclarationBlock(clan, otherClan);
            plugin.getVault().withdrawPlayer(p, 5000);
            p.sendMessage(TextUtil.convertColor("&4Declared war on " +
                    ColorUtil.colorToStringCode(plugin.getClanManager().getColor(item.getItemId())) +
                    item.getItemId() + "&r&4! Go get 'em!"));

        }
    }
}
